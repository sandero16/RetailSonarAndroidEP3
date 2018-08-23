package jsf.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

//nog toe te voegen 
//import com.sun.media.jfxmedia.logging.Logger;

import data.Bedrijf;
import data.User;
import ejb.BedrijfManagementEJBLocal;
import ejb.GebruikerManagementEJBLocal;

/**
 * Klasse die alle verwerkingen in verband met objecten van het type Bedrijf uitvoert.
 *
 */

@Named
@ViewScoped
public class BedrijfController implements Serializable {

	private static final long serialVersionUID = 1L;

	/*Objecten*/
	@EJB
	private BedrijfManagementEJBLocal bedrijfEJB;
	@EJB
	private GebruikerManagementEJBLocal gebruikerEJB;
	
	/*Variabelen*/
	private Bedrijf bedrijf = new Bedrijf();
	private User user = new User();
	private int bedrijfId;

	/*Methoden*/
	
	/**
	 * Methode die een nieuw bedrijf aanmaakt.
	 * 
	 * @return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 * @see bedrijfEJB
	 * */
	public String maakBedrijf(){
		bedrijfEJB.maakBedrijf(bedrijf);
		return "Dashboard-admin.faces?faces-redirect=true";
	}
	
	/**
	 * Methode die alle gebruikers van een bedrijf zoekt.
	 * 
	 * @return List lijst met objecten van het type {@link User}.
	 * @see gebruikerEJB
	 * */
	public List<User> findAllUsersBedrijf(){
		return gebruikerEJB.findAllUsersBedrijf(user.getBedrijfId());
	}
	
	/**
	 * Methode die alle bedrijven zoekt.
	 * 
	 * @return List Lijst met objecten van het type {@link Bedrijf}.
	 * @see bedrijfEJB
	 * */
	public List<Bedrijf> findAllBedrijven(){
		//functie voor het zoeken van alle bedrijven
		return bedrijfEJB.findAllBedrijven();
		
	}
	
	/**
	 * Methode die de gebruiker correct uitlogt.
	 * @see ExternalContext
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
	 * Methode die de paramaters van een object van het type {@link Bedrijf} doorgeeft aan de database.
	 * 
	 * @return String De gebruiker wordt doorverwezen naar een jsf pagina.
	 * @see bedrijfEJB
	 * */
	public String wijzigBedrijf(){
		bedrijfEJB.wijzigBedrijf(bedrijf);
		return "Dashboard-admin.faces?faces-redirect=true";
	}
	
	/**
	 * Methode die een bedrijf zoekt op basis van een bedrijfId.
	 * 
	 * @see bedrijfEJB
	 * */
	public void findBedrijf(){
		bedrijf = bedrijfEJB.findBedrijf(bedrijf.getBedrijfId());		
	}
	
	/*Getters en Setters*/
	public int getBedrijfId() {
		return bedrijfId;
	}

	public void setBedrijfId(int bedrijfId) {
		this.bedrijfId = bedrijfId;
	}

	public Bedrijf getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf;
	}
}
