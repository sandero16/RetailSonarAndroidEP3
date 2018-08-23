package jsf.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import data.FiliaalGegevens;
import data.Taak;
import data.User;
import data.Bedrijf;
import data.Customeigenschap;
import data.Customeigenschapingevuld;
import data.Eigenschappenbedrijf;
import data.Eigenschappenbedrijfingevuld;
import data.Afstand;
import ejb.BedrijfManagementEJBLocal;
import ejb.CustomEigenschapIngevuldManagementEJBLocal;
import ejb.CustomEigenschapManagementEJBLocal;
import ejb.EigenschappenBedrijfIngevuldManagementEJBLocal;
import ejb.EigenschappenBedrijfManagementEJBLocal;
import ejb.FiliaalManagementEJBLocal;
import ejb.GebruikerManagementEJBLocal;
import ejb.TakenManagementEJBLocal;
import ejb.AfstandManagementEJBLocal;
import ejb.AfstandenBedrijfManagementEJBLocal;

/**
 * Klasse die alle verwerkingen in verband met een filiaal uitvoert.
 * */
@ManagedBean(name="FiliaalController")
@SessionScoped
public class FiliaalController implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/*Ojecten*/
	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;
	
	@EJB
	private GebruikerManagementEJBLocal userEJB;
	
	@EJB
	private TakenManagementEJBLocal takenEJB;
	
	@EJB
	private EigenschappenBedrijfManagementEJBLocal eigenschapBedrijfEJB;
	
	@EJB
	private EigenschappenBedrijfIngevuldManagementEJBLocal eigenschapBedrijfIngevuldEJB;
	
	@EJB
	private BedrijfManagementEJBLocal bedrijfEJB;
	
	@EJB
	private CustomEigenschapManagementEJBLocal customEigenschapEJB;
	
	@EJB
	private  CustomEigenschapIngevuldManagementEJBLocal customEigenschapIngevuldEJB;
	
	@EJB
	private AfstandenBedrijfManagementEJBLocal afstandenBedrijfEJB;
	
	/*Variabelen*/
	private String uname = "";
	
	private User loggedOnUser = new User();
	
	private int bedrijfid = 1;
	
	private int filiaalId;
	
	private Taak taak = new Taak();

	private FiliaalGegevens filiaalGegevens = new FiliaalGegevens();
	
	private FiliaalGegevens filiaalGegevensNieuw = new FiliaalGegevens();
	
	private FiliaalGegevens huidigFiliaal = new FiliaalGegevens();
	
	private List<String> zichtbaarheden;
	
	private String bedrijfNaam="";
	
	private Map<Integer, Boolean> checked = new HashMap<>();
	
	private Map<Integer, Boolean> checkedStatus = new HashMap<>();
	
	private static final String DASHADMIN = "Dashboard-exp.faces?faces-redirect=true"; 

	/*Methoden*/
	
	/**
	 * Methode die een nieuw object van het type {@link FiliaalGegevens} aan de database toevoegd.
	 * 
	 * @return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 * 
	 * @see filiaalEJBvoid
	 * @see userEJB
	 * */
	public String maakFiliaal(){
		uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		int filiaalid=filiaalEJB.invoegenGegevens(filiaalGegevensNieuw, uname);
	
		loggedOnUser = userEJB.findPerson(uname);
		if(loggedOnUser.getGroupId() == 1){
			return "Dashboard-client2.faces?faces-redirect=true";
		}else if(loggedOnUser.getGroupId() == 3){
			return DASHADMIN; 
		}
		return null ;
	}
	
	/**
	 * Methode die een filiaal verwijdert.
	 * 
	 * @param filiaalId Het it van het te verwijderen filiaal
	 * @return String De gebruiker wordt doorverwezen naar een web pagina
	 * 
	 * @see filiaalEJB
	 * */
	public String verwijderFiliaal(int filiaalId){
		filiaalEJB.verwijderFiliaal(filiaalId);
		return DASHADMIN;
	}
	
	/**
	 * Methode die eigenschappen aan een bedrijf toevoegt
	 * 
	 * @param f Het filiaal waaraan de eigenschap wordt toegevoegd
	 * 
	 * @see FiliaalGegevens
	 * @see eigenschapBedrijfEJB
	 * @see eigenschapBedrijfIngevuldEJB
	 * */
	public void voegEigenschappenBedrijfToe(FiliaalGegevens f){
		List<Eigenschappenbedrijf> eigBedr = eigenschapBedrijfEJB.findAllEigenschappenBedrijf(filiaalEJB.findFiliaalGegevens(f.getFiliaalid()).getBedrijf().getBedrijfId());
		List<Eigenschappenbedrijfingevuld> eigBedrIng = eigenschapBedrijfIngevuldEJB.findAllEigenschappen(f.getFiliaalid());
		Set<Integer> eigBedrSet = new HashSet<>();
		if(!eigBedrIng.isEmpty()){
			for(Eigenschappenbedrijfingevuld ebi : eigBedrIng){
				eigBedrSet.add(ebi.getEigenschapId());
			}
			for(Eigenschappenbedrijf eb : eigBedr){
				if(!eigBedrSet.contains(eb.getEigenschapId())){
					eigenschapBedrijfIngevuldEJB.createEigenschapIngevuld(eb, f);
				}
			}
		}else{
			for(Eigenschappenbedrijf eb : eigBedr){
				eigenschapBedrijfIngevuldEJB.createEigenschapIngevuld(eb, f);
			}
		}
	}
	
	/**
	 * Methode die een custom eigenschap aan een filiaal toevoegt.
	 * 
	 * @param f Het filiaal waaraan de eigenschap dient toegevoegd te worden
	 * @see FiliaalGegevens
	 * @see customEigenschapEJB
	 * @see customEigenschapIngevuldEJB
	 * */
	public void voegCustomEigenschappenBedrijfToe(FiliaalGegevens f){
		
		List<Customeigenschap> temp = customEigenschapEJB.findAllCustomEigenschappenBedrijf(filiaalEJB.findFiliaalGegevens(f.getFiliaalid()).getBedrijf().getBedrijfId());
		List<Customeigenschapingevuld> custEigIng = customEigenschapIngevuldEJB.findAllEigenschappen(f.getFiliaalid());
		Set<Integer> custEigIngSet = new HashSet<>();
		
		if(!custEigIng.isEmpty()){
			for(Customeigenschapingevuld cei : custEigIng){
				custEigIngSet.add(cei.getCustomeigenschapId());
			}
			for(Customeigenschap ce : temp){
				if(!custEigIngSet.contains(ce.getCustomEigenschap_id())){
					customEigenschapIngevuldEJB.createCustomEigenschapIngevuld(ce, f);
				}
			}
		}else{
			for(Customeigenschap ce : temp){
				customEigenschapIngevuldEJB.createCustomEigenschapIngevuld(ce, f);
			}
		}
		
	}

	/**
	 * Methode die alle filialen van een bepaald bedrijf teruggeeft.
	 * 
	 * @return List lijst met objecten van het type {@link FiliaalGegevens} die voldoen aan de voorwaarden.
	 * @see filiaalEJB
	 * */
	public List<FiliaalGegevens> findAllFilialenBedrijf(){
		return filiaalEJB.findAllFilialenBedrijf(getLoggedOnUser().getBedrijfId());
		
	}
	
	/**
	 * Methode die de gebruiker correct uitlogt.
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
	 * Methode die een gebruiker zoekt en zijn gebruikersnaam teruggeeft.
	 * 
	 * @return String De username van de gebruiker.
	 * */
	public String findUser(){
		return getLoggedOnUser().getUsername();
	}
	
	/**
	 * Methode die een filiaal zoekt.
	 * 
	 * @see filiaalEJB
	 * */
	public void findFiliaalGegevens(){
		filiaalGegevens = filiaalEJB.findFiliaalGegevens(filiaalId);
		voegEigenschappenBedrijfToe(filiaalGegevens);
		voegCustomEigenschappenBedrijfToe(filiaalGegevens);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link FiliaalGegevens} wijzigt.
	 * 
	 * @return String de gebruiker wordt doorverwezen naar een jsf pagina.
	 * @see filiaalEJB
	 * */
	public String wijzigFiliaal(){
		filiaalEJB.wijzigFiliaal(filiaalGegevens);
		if(getLoggedOnUser().getGroupId() == 1){
			return "Dashboard-reg.faces?faces-redirect=true";
		}else if(getLoggedOnUser().getGroupId() == 3){
			return DASHADMIN;
		}else{
			return null;
		}
		
	}
	
	/**
	 * Methode die gebruikers na het inloggen op basis van het groupId doorverwijst naar de juiste jsf pagina.
	 * 
	 * @return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 * */
	public String redirect(){
		getLoggedOnUser();
		if(loggedOnUser.getGroupId() == 1){
			return "WachtwoordWijzigen-reg.faces?faces-redirect=true";
		}else if(loggedOnUser.getGroupId() == 3){
			return "WachtwoordWijzigen-exp.faces?faces-redirect=true";
		}else{
			return "WachtwoordWijzigen.faces?faces-redirect=true";
		}
		
	}
	
	public String redirectGegevens(){
		getLoggedOnUser();
		if(loggedOnUser.getGroupId() == 1){
			return "Gegevens.faces?faces-redirect=true";
		}else if(loggedOnUser.getGroupId() == 3){
			return "Gegevens.faces?faces-redirect=true";
		}else{
			return "Gegevens.faces?faces-redirect=true";
		}
		
	}

	/**
	 * Meethode die alle filialen van een regiomanager opvraagt en teruggeeft als een lijst.
	 * 
	 * @return List Lijst van objecten van het type {@link FiliaalGegevens} die voldoen aan de parameters.
	 * @see filiaalEJB
	 * */
	public List<FiliaalGegevens> findAllFilialenRegioManager(){
		return filiaalEJB.findAllFilialenRegioManager(getLoggedOnUser().getUserId());
	}
	
	/**
	 * Methode die een regiomanager zoekt en de username teruggeeft.
	 * 
	 * @return String Username van de gezohte regiomanager.
	 * @see userEJB
	 * */
	public String findRegioManager(){
		return userEJB.findPerson(huidigFiliaal.getUserId()).getUsername();
	}
	
	/**
	 * Methode die de naam van een bedrijf zoekt en teruggeeft.
	 * 
	 * @see bedrijfEJB
	 * */
	public void findBedrijfNaam(){
		filiaalGegevens = filiaalEJB.findFiliaalGegevens(filiaalId);
		bedrijfNaam = filiaalGegevens.getBedrijf().getNaam();
	}
	
	/**
	 * Methode die de locatiegegevens van alle filialen van een gebruiker teruggeeft als een komma seperated string.
	 * 
	 * @return String Een komma seperated string.
	 * @see filiaalEJB
	 * */
	public String getLocationInfoFilialen(){
		List<FiliaalGegevens> temp = filiaalEJB.findAllFilialenRegioManager(loggedOnUser.getUserId());
		StringBuilder toBeReturned = new StringBuilder();
		int i = 1;
		for(FiliaalGegevens f : temp){
			toBeReturned.append(f.getStraat());
			toBeReturned.append(",");
			toBeReturned.append(f.getNummer());
			toBeReturned.append(",");
			toBeReturned.append(f.getGemeente());
			if(i<temp.size()){
				toBeReturned.append(",");
			}
			i++;
		}
		return toBeReturned.toString();
	}
	
	/**
	 * Methode die locatiegegevens van alle filialen van een bedrijf teruggeeft als een komma seperated string.
	 * 
	 * @return String Een komma seperated string.
	 * @see filiaalEJB
	 * */
	public String getLocationInfoAllFilialen(){
		List<FiliaalGegevens> temp = filiaalEJB.findAllFilialenBedrijf(getLoggedOnUser().getBedrijfId());
		StringBuilder toBeReturned = new StringBuilder();
		int i = 1;
		for(FiliaalGegevens f : temp){
			toBeReturned.append(f.getStraat());
			toBeReturned.append(",");
			toBeReturned.append(f.getNummer());
			toBeReturned.append(",");
			toBeReturned.append(f.getGemeente());
			if(i<temp.size()){
				toBeReturned.append(",");
			}
			i++;
		}
		return toBeReturned.toString();
	}
	
	/**
	 * Methode die alle taken van een filiaal teruggeeft als een lijst.
	 * 
	 * @return List Lijst met objecten van het type {@link Taak} die voldoen aan de paramaters.
	 * @see takenEJB
	 * */
	public List<Taak> findAllTasksFiliaal(){
		
		List<Taak> temp = takenEJB.findAllTakenFiliaal(huidigFiliaal.getFiliaalid());
		for(Taak t: temp){
			checked.put(t.getTaakId(), Boolean.FALSE);
			checkedStatus.put(t.getTaakId(), Boolean.FALSE);
		}
		return temp;
	}
	
	/**
	 * Methode die alle taken van een regiomanager teruggeeft.
	 * 
	 * @return List Lijst met objecten van het type {@link Taak} die voldoen aan de paramaters.
	 * @see takenEJB
	 * */
	public List<Taak> findAllTasksFiliaalRegioManager(){
		List<Taak> temp = takenEJB.findAllTakenFiliaal(huidigFiliaal.getFiliaalid());
		boolean taakBoolean;
		for(Taak t : temp){
			if(t.getStatus() == 0){
				taakBoolean=false;
			}else{
				taakBoolean=true;
			}
			checked.put(t.getTaakId(), Boolean.FALSE);
			checkedStatus.put(t.getTaakId(), taakBoolean);
		}
		Collections.sort(temp, (a, b) -> a.getStatus() < b.getStatus() ? -1 : a.getStatus() == b.getStatus() ? 0 : 1);
		return temp;
	}
	
	/**
	 * Methode die een nieuwe object van het type {@link Taak} toevoegt.
	 * 
	 * @see takenEJB
	 * */
	public void voegTakenToe(){
		taak.setFiliaal(huidigFiliaal);
		
		takenEJB.voegTakenToe(taak);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Taak} verwijdert.
	 * 
	 * @see takenEJB
	 * */
	public void verwijderTaken() {
	   
	    for (Entry<Integer,Boolean> entry : checked.entrySet()) {
	        if (entry.getValue()) {
	        	takenEJB.verwijderTaak((int)entry.getKey()); 
	        }           
	    }
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Taak} wijzigt.
	 * 
	 * @see takenEJB
	 * */
	public void updateTaken(){
		Taak t;
		for(Entry<Integer,Boolean> entry : checkedStatus.entrySet()){
			t = takenEJB.findTaak(entry.getKey());
			if(entry.getValue()){
				t.setStatus(1);
						
			}else{
				t.setStatus(0);
			}
			takenEJB.updateTaken(t);
		}
	}
	
	/**
	 * Methode die de ingelogde gebuiker als een object van het type {@link User} teruggeeft.
	 * 
	 * @return User Object van het type {@link User} die de attributen van de ingelogde user bevat.
	 * 
	 * @see userEJB
	 * */
	public User getLoggedOnUser(){
		uname = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		loggedOnUser = userEJB.findPerson(uname);
		return loggedOnUser;
	}
	
	/*Getters en setters*/
	public Map<Integer, Boolean> getCheckedStatus() {
		return checkedStatus;
	}
	
	public void setCheckedStatus(Map<Integer, Boolean> checkedStatus) {
		this.checkedStatus = checkedStatus;
	}
	
	public Map<Integer, Boolean> getChecked() {
		return checked;
	}

	public void setChecked(Map<Integer, Boolean> checked) {
		this.checked = checked;
	}
	
	public String getBedrijfNaam() {
		return bedrijfNaam;
	}

	public void setBedrijfNaam(String bedrijfNaam) {
		this.bedrijfNaam = bedrijfNaam;
	}
	
	public FiliaalGegevens getFiliaalGegevens() {
		return filiaalGegevens;
	}

	public void setFiliaalGegevens(FiliaalGegevens filiaalGegevens) {
		this.filiaalGegevens = filiaalGegevens;
	}
	
	public int getUserid() {
		return bedrijfid;
	}

	public void setUserid(int userid) {
		this.bedrijfid = userid;
	}
	
	public Taak getTaak() {
		return taak;
	}

	public void setTaak(Taak taak) {
		this.taak = taak;
	}

	public int getFiliaalId() {
		return filiaalId;
	}
	
	public void setFiliaalId(int filiaalId) {
		this.filiaalId = filiaalId;
	}
	
	public FiliaalGegevens getHuidigFiliaal() {
		return huidigFiliaal;
	}

	public void setHuidigFiliaal(FiliaalGegevens huidigFiliaal) {
		this.huidigFiliaal = huidigFiliaal;
	}
	
	public List<String> getZichtbaarheden() {
		return zichtbaarheden;
	}

	public void setZichtbaarheden(List<String> zichtbaarheden) {
		this.zichtbaarheden = zichtbaarheden;
	}
	
	public FiliaalGegevens getFiliaalGegevensNieuw() {
		return filiaalGegevensNieuw;
	}

	public void setFiliaalGegevensNieuw(FiliaalGegevens filiaalGegevensNieuw) {
		this.filiaalGegevensNieuw = filiaalGegevensNieuw;
	}

}