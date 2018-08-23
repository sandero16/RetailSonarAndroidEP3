package jsf.controller;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet; 

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import data.Bedrijf;
import data.Group;
import data.User;
import ejb.BedrijfManagementEJBLocal;
import ejb.GebruikerManagementEJBLocal;
import ejb.GroupManagementEJBLocal;

/**
 * Klasse die alle verwerkingen in verband met een user behandeld.
 * */
@WebServlet("/UserController") 
@ManagedBean(name="UserController")
@SessionScoped
public class UserController implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/*Objecten*/
	@EJB
	private GebruikerManagementEJBLocal userEJB;
	@EJB
	private BedrijfManagementEJBLocal bedrijfEJB;
	@EJB
	
	/*Variabelen*/
	private GroupManagementEJBLocal groupEJB;
	private User user = new User();
	private User loggedInUser = new User();
	
	private Bedrijf bedrijf = new Bedrijf();
	private int bedrijfId;
	private Group group = new Group();
	private int userGroup;
	private String newPassword = "";
	
	

	/*Methoden*/
	
	/** 
	 * Methode om alle gebruikers van een bedrijf te vinden.
	 * 
	 * @return List Deze methode geeft een lijst met objecten van het type {@link User} terug.
	 * @see userEJB
	 * */
	public List<User> findAllUsersBedrijf(){
		String uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		loggedInUser = userEJB.findPerson(uname);
		if(loggedInUser.getGroupId() == 3){
			bedrijfId = loggedInUser.getBedrijfId();
		}
		
		return userEJB.findAllUsersBedrijf(bedrijfId);
	}
	
	/**
	 * Methode om een nieuwe gebruiker aan te maken. Het correcte bedrijf wordt toegevoegd aan de user, het wachtwoord
	 * wordt gehashed, de correcte user group wordt toegekend (afhankelijk van wat de gerbuiker koos) alsook het groupId wordt
	 * toegekend.
	 *
	 * @return String De gebruikers wordt doorverwezen naar het bijhorende dashboard door een uitdrukking van de vorm xx.faces?faces-redirect=true.
	 * @see bedrijfEJB
	 * @see userEJB
	 * */
	public String maakGebruiker(){
		//Bedrijf ophalen
		user.setBedrijf(bedrijfEJB.findBedrijf(bedrijfId));
		user.setPassword(userEJB.hash(user.getPassword()));
		group=groupEJB.findGroup(userGroup);
		user.addGroup(group);
		user.setGroupId(userGroup);
		userEJB.maakGebruiker(user);
		if(findLoggedOnUser().getGroupId() == 2){
			return "Gebruikers.faces?faces-redirect=true";
		}else if(findLoggedOnUser().getGroupId() == 3){
			return "Dashboard-exp.faces?faces-redirect=true";
		}else{
			return null;
		}
	}
	
	/**
	 * Methode om een nieuwe admin aan te maken. Het wachtwoord
	 * wordt gehashed, de correcte user group wordt toegekend 
	 * alsook het groupId wordt
	 * toegekend.
	 *
	 * @return String De gebruikers wordt doorverwezen naar het bijhorende dashboard door een uitdrukking van de vorm xx.faces?faces-redirect=true.
	 * @see userEJB
	 * @see bedrijfEJB
	 * */
	public String maakAdmin(){
		userGroup = 2;
		user.setBedrijf(bedrijfEJB.findBedrijf(0));
		user.setPassword(userEJB.hash(user.getPassword()));
		group=groupEJB.findGroup(userGroup);
		user.addGroup(group);
		user.setGroupId(userGroup);
		userEJB.maakGebruiker(user);
		return "Dashboard-admin.faces?faces-redirect=true";
	}
	
	/**
	 * Methode om bij het inloggen de gebruiker automatisch door te verwijzen naar het juiste dashboard.
	 * 
	 * @return String De gebruiker wordt naar het juiste dashboard doorverwezen door een uitdrukking van de vorm xx.faces?faces-redirect=true
	 * @see userEJB
	 * */
	public String redirect(){
		
		String uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		
		loggedInUser = userEJB.findPerson(uname);
		
		if(loggedInUser.getGroupId() == 2){
			return "/admin/Dashboard-admin.jsf";
		}else if(loggedInUser.getGroupId() == 1){
			return "/RegioManager/Dashboard-reg.jsf";
		}else if(loggedInUser.getGroupId() == 3){
			return "/ExpansieManager/Dashboard-exp.jsf";
		}
		else{
			return null;
		}
		
	}
	
	/**
	 * Methode om de gebruiker correct uit te loggen.
	 * 
	 * */
	public void logoutFunction() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	    ec.invalidateSession();
	    try {
			ec.redirect(ec.getRequestContextPath() + "/index.faces");
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	/**
	 * Methode om de twee ingevoerde wachtwoorden van een gebruiker te controleren.
	 * @param event Een event.
	 * 
	 * */
	public void checkPasswordUser(ComponentSystemEvent event){
		  validatePassword(event);    
	}
	
	/**
	 * Methode die indien de twee ingevulde wachtwoorden gelijk zijn de het wachtwoord avn de gebruiker ook effectief gaat aanpassen.
	 * 
	 *@return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 *@see userEJB
	 * */
	public String changePasswordUser(){
		String newPasswordHashed = userEJB.hash(newPassword);
		loggedInUser = findLoggedOnUser();
		loggedInUser.setPassword(newPasswordHashed);
		userEJB.changeGebruiker(loggedInUser);
		newPassword=null;
		return "Dashboard-reg.faces?faces-redirect=true";  
	}
	
	/**
	 * Methode die zowel het wachtwoord als de gebruikersnaam controleerd voor en nieuwe gebruiker.
	 * 
	 * @param event Een event.
	 * 
	 * */
	public void validatePasswordNewUser(ComponentSystemEvent event){
		validatePassword(event);
		validateUsername(event);
	}
	
	/**
	 * Methode die de controle van de twee ingevoerde wachtwoorden effectief controlleerd.
	 * @param event Een event.
	 * 
	 * @see FacesContext
	 * @see UIComponent
	 * @see UIInput
	 * @see FacesMessage
	 * */
	public void validatePassword(ComponentSystemEvent event) {

		  FacesContext fc = FacesContext.getCurrentInstance();

		  UIComponent components = event.getComponent();

		  /*get wachtwoord*/
		  UIInput uiInputPassword = (UIInput) components.findComponent("userPassword");
		  String password = uiInputPassword.getLocalValue() == null ? ""
			: uiInputPassword.getLocalValue().toString();
		  String passwordId = uiInputPassword.getClientId();

		  /*get controle wachtwoord*/
		  UIInput uiInputConfirmPassword = (UIInput) components.findComponent("userPasswordValidation");
		  String confirmPassword = uiInputConfirmPassword.getLocalValue() == null ? ""
			: uiInputConfirmPassword.getLocalValue().toString();
		  
		  if (password.isEmpty() || confirmPassword.isEmpty()) {
			return;
		  }

		  if (!password.equals(confirmPassword)) {

			FacesMessage msg = new FacesMessage("Paswoorden komen niet overeen");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(passwordId, msg);
			fc.renderResponse();
				
		  }
		}
	
	/**
	 * Methode die de controlleerd indien de ingevoerde username reeds bestaat.
	 * 
	 * @param event Een event.
	 * @see userEJB
	 * @see FacesContext
	 * @see UIComponent
	 * @see UIInput
	 * @see FacesMessage
	 * */
	public void validateUsername(ComponentSystemEvent event){
		FacesContext fc = FacesContext.getCurrentInstance();

		  UIComponent components = event.getComponent();
		//get new username
		  UIInput uiInputUsername = (UIInput) components.findComponent("username");
		  String username = uiInputUsername.getLocalValue() == null ? ""
			: uiInputUsername.getLocalValue().toString();
		  String usernameId = uiInputUsername.getClientId();

		  Set<String> users = new HashSet<>();
		  for(User u : userEJB.findAllUsers()){
			  users.add(u.getUsername());
		  }
		  if(username.isEmpty()){
			  return;
		  }
		  
		  if(users.contains(username)){
			  FacesMessage msg = new FacesMessage("Username bestaal al");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				fc.addMessage(usernameId, msg);
				fc.renderResponse();
		  }
	}
	
	/**
	 * Methode die alle regiomanagers van een bedrijf zoekt.
	 * 
	 * @return List Deze methode geeft een lijst met objecten van het type {@link User} terug van een bedrijf.
	 * @see userEJB
	 * */
	public List<User> findAllRegioManagersBedrijf(){
		String uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		bedrijfId = userEJB.findPerson(uname).getBedrijfId();
		return userEJB.findAllRegioManagersBedrijf(bedrijfId,1);
	}
	
	/**
	 * Methode die de huidige ingelogde gebruiker zoekt.
	 *
	 * @return User Deze methode geeft een User terug.
	 * @see userEJB
	 * */
	public User findLoggedOnUser(){
		String uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		loggedInUser = userEJB.findPerson(uname);
		return loggedInUser;
	}
	
	/**
	 * Methode die alle usernames teruggeeft.
	 * 
	 * @return List lijst met Strings van alle usernames.
	 * @see userEJB
	 * */
	public List<String> findAllUsersNames(){
		List<User> temp = userEJB.findAllUsers();
		List<String> str = new ArrayList<>();
		for(User u : temp){
			str.add(u.getUsername());
		}
		return str;
	}
	
	/**
	 * Methode die een bestaand object van het type {@link User} verwijdert.
	 * 
	 * @param userId Het userId van de gebruiker.
	 * @return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 * @see userEJB
	 * */
	public String verwijderGebruiker(int userId){
		
		userEJB.verwijderGebruiker(userId);
		if(findLoggedOnUser().getGroupId() == 2){
			return "Dashboard-admin.faces?faces-redirect=true";
		}else if(findLoggedOnUser().getGroupId() == 3){
			return "Dashboard-exp.faces?faces-redirect=true";
		}else{
			return null;
		}
	}
	
	/**
	 * Methode die een bestaand object van het type {@link User} verwijdert.
	 * 
	 * @param userId Het userId van de gebruiker.
	 * @return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 * @see userEJB
	 * */
	public String verwijderAdmin(int userId){
		
		userEJB.verwijderGebruiker(userId);
		return "Dashboard-admin.faces?faces-redirect=true";
	}
	
	/**
	 * Methode die alle admins uit de database teruggeeft als objecten van het type
	 * {@link User}.
	 * 
	 * @return List Lijst met objecten van het type {@link User}.
	 * @see userEJB
	 * */
	public List<User> findAllAdmins(){
		return userEJB.findAllAdmins();
	}
	
	/*Getters and setters*/
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		
	}
	
	public Bedrijf getBedrijf() {
		return bedrijf;
	}


	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf;
	}
	
	public int getBedrijfId() {
		return bedrijfId;
	}

	public void setBedrijfId(int bedrijfId) {
		this.bedrijfId = bedrijfId;
	}
	
	public int getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(int userGroup) {
		this.userGroup = userGroup;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public User getLoggedInUser() {
		return loggedInUser;
	}


	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
}

