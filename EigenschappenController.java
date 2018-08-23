package jsf.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import data.Afstand;
import data.Categorie;
import data.Customeigenschap;
import data.Customeigenschapingevuld;
import data.Eigenschappen;
import data.Eigenschappenbedrijf;
import data.Eigenschappenbedrijfingevuld;
import data.FiliaalGegevens;
import data.Oppervlakte;
import data.OppervlaktenBedrijf;
import data.AfstandenBedrijf;
import ejb.AfstandManagementEJBLocal;
import ejb.BedrijfManagementEJBLocal;
import ejb.CategorieManagementEJBLocal;
import ejb.CustomEigenschapIngevuldManagementEJBLocal;
import ejb.CustomEigenschapManagementEJBLocal;
import ejb.EigenschappenBedrijfIngevuldManagementEJBLocal;
import ejb.EigenschappenBedrijfManagementEJBLocal;
import ejb.EigenschappenManagementEJBLocal;
import ejb.FiliaalManagementEJBLocal;
import ejb.OppervlakteManagementEJBLocal;
import ejb.OppervlaktenBedrijfManagementEJBLocal;
import ejb.AfstandenBedrijfManagementEJBLocal;

/**
 * Klasse die alle verwerkingen die te maken hebben eigenschappen of custom eigenschappen.
 * */

@ManagedBean(name = "EigenschappenController")
@SessionScoped
public class EigenschappenController implements Serializable {
	private static final long serialVersionUID = 1L;

	

	/* Objecten */
	@EJB
	private CustomEigenschapManagementEJBLocal customEigenschapEJB;

	@EJB
	private CustomEigenschapIngevuldManagementEJBLocal customEigenschapIngevuldEJB;

	@EJB
	private BedrijfManagementEJBLocal bedrijfEJB;

	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;

	@EJB
	private EigenschappenManagementEJBLocal eigenschapEJB;

	@EJB
	private EigenschappenBedrijfManagementEJBLocal eigenschapBedrijfEJB;

	@EJB
	private EigenschappenBedrijfIngevuldManagementEJBLocal eigenschapBedrijfIngevuldEJB;

	@EJB
	private CategorieManagementEJBLocal categorieEJB;
	
	@EJB
	private AfstandManagementEJBLocal afstandEJB;
	
	@EJB
	private OppervlakteManagementEJBLocal oppervlakEJB;
	
	@EJB
	private AfstandenBedrijfManagementEJBLocal afstandenBedrijfEJB;
	
	@EJB
	private OppervlaktenBedrijfManagementEJBLocal oppervlaktenBedrijfEJB;
	

	/* Variabelen */
	private Customeigenschap customEigenschap = new Customeigenschap();

	private Eigenschappen eigenschap = new Eigenschappen();

	private Eigenschappenbedrijf eigenschapBedrijf = new Eigenschappenbedrijf();

	private Map<Integer, Boolean> checked = new HashMap<>();

	private Map<Integer, Boolean> checkedForDeletion = new HashMap<>();

	private Map<Integer, Boolean> checkedForCustomDeletion = new HashMap<>();

	private Eigenschappen eigenschapDropdownMenu = new Eigenschappen();

	private Customeigenschap customEigenschapDropdownMenu = new Customeigenschap();

	private Customeigenschapingevuld customEigenschapIngevuld = new Customeigenschapingevuld();

	private int bedrijfId;

	private int filiaalId;

	private Categorie categorie = new Categorie();

	private List<Customeigenschapingevuld> customLijst = new ArrayList<>();

	private List<Eigenschappenbedrijfingevuld> eigenschappenLijst = new ArrayList<>();

	private List<Eigenschappen> eigenschappen = new ArrayList<>();

	private String menuListOption1 = "";

	private String menuListOption2 = "";

	private String menuListOption3 = "";

	private String menuListOption4 = "";

	private String menuListOption5 = "";

	private String menuListCustomOption1 = "";

	private String menuListCustomOption2 = "";

	private String menuListCustomOption3 = "";

	private String menuListCustomOption4 = "";

	private String menuListCustomOption5 = "";

	private Map<Integer, List<Eigenschappenbedrijfingevuld>> eigenschappenLijsten = new HashMap<>();

	private Map<Integer, List<Customeigenschapingevuld>> customEigenschappenLijsten = new HashMap<>();

	/* Methoden */

	/**
	 * Methode die voor een nieuwe custom eigenschap het bijhorende bedrijf
	 * instelt en de eigenschap toevoegt.
	 * 
	 * @see Customeigenschap
	 * @see customEigenschapEJB
	 * @see customEigenschapIngevuldEJB
	 */
	public void voegCustomEigenschapToe() {
		customEigenschap.setBedrijf(bedrijfEJB.findBedrijf(bedrijfId));
		customEigenschapEJB.voegCustomEigenschapToe(customEigenschap);
		System.out.println("customeigenschap: "+customEigenschap.getCustomEigenschap_id());
		for (FiliaalGegevens f : filiaalEJB.findAllFilialenBedrijf(bedrijfId)) {
			customEigenschapIngevuldEJB.createCustomEigenschapIngevuld(customEigenschap, f);
		}
	}

	/**
	 * Methode die alle custom eigenschappen van een bedrijf zoekt en teruggeeft
	 * in de vorm van een lijst.
	 * 
	 * @return List Een lijst met objecten van de klasse
	 *         {@link Customeigenschap}.
	 *         
	 * @see Customeigenschap
	 * @see customEigenschapEJB
	 */
	public List<Customeigenschap> findAllCustomEigenschappenBedrijf() {
		List<Customeigenschap> temp = customEigenschapEJB.findAllCustomEigenschappenBedrijf(bedrijfId);
		for (Customeigenschap c : temp) {
			checkedForCustomDeletion.put(c.getCustomEigenschap_id(), Boolean.FALSE);
		}
		return temp;
	}

	/**
	 * Methode die een bestaande custom eigenschap verwijdert.
	 * 
	 */
	public void verwijderCustomEigenschap() {
		List<Integer> toRemove = new ArrayList<>();
		for (Entry<Integer, Boolean> entry : checkedForCustomDeletion.entrySet()) {
			if (entry.getValue()) {
				customEigenschapEJB.verwijderEigenschap(entry.getKey(), bedrijfId);
				customEigenschapIngevuldEJB.verwijderCustomEigenschap(entry.getKey(), bedrijfId);
				toRemove.add(entry.getKey());
			}
		}
		for (Integer i : toRemove) {
			checkedForCustomDeletion.remove(i);
		}
	}

	/**
	 * Methode die alle custom eigenschappen van een filiaal zoekt en teruggeeft
	 * in de vorm van een lijst.
	 * 
	 * @param catId
	 *            Id van de categorie.
	 * @return List Een lijst met objecten van het type
	 *         {@link Customeigenschapingevuld} van alle custom eigenschappen
	 *         van een filiaal
	 * @see customEigenschapIngevuldEJB
	 * @see Customeigenschapingevuld
	 */
	public List<Customeigenschapingevuld> findAllCustomEigenschappenFiliaal(int catId) {
		customLijst = customEigenschapIngevuldEJB.findAllEigenschappen(filiaalId, catId);
		customEigenschappenLijsten.put(catId, customLijst);
		Collections.sort(customLijst, (a, b) -> a.getCategorieId() < b.getCategorieId() ? -1
				: a.getCategorieId() == b.getCategorieId() ? 0 : 1);
		return customEigenschappenLijsten.get(catId);
	}
	
	public Afstand checkVoorAfstand(){
		Afstand afstand=afstandEJB.findAfstand(filiaalId);
		return afstand;
	}
	
	public Oppervlakte checkVoorOppervlak(){
		Oppervlakte oppervlak=oppervlakEJB.findoppervlakte(filiaalId);
		return oppervlak;
	}
	
	public boolean checkVoorAanwezigAfstand(){
		Boolean b=afstandenBedrijfEJB.findAanwezigheid(bedrijfId);
		return b;
	}
	public boolean checkVoorAanwezigOppervlakFiliaal(){
		int idb=filiaalEJB.findIdBedrijf(filiaalId);
		System.out.println("bedrijf id oppervlakte: "+idb);
		Boolean b=oppervlaktenBedrijfEJB.findAanwezigheid(idb);
		if(b){
			System.out.println("jaejaj");
		}
		return b;
	}
	
	public boolean checkVoorAanwezigOppervlak(){
		Boolean b=oppervlaktenBedrijfEJB.findAanwezigheid(bedrijfId);
		return b;
	}
	public boolean checkVoorAanwezigAfstandFiliaal(){
		int idb=filiaalEJB.findIdBedrijf(filiaalId);
		Boolean b=afstandenBedrijfEJB.findAanwezigheid(idb);
		return b;
	}
	/**
	 * Methode die alle eigenschapen van een filiaal zoekt op basis van een
	 * gegeven categorieId.
	 * 
	 * @param catId
	 *            Id van de categorie waarvan de eigenschappen gevraagd worden.
	 * @return List Lijst met objecten van het type
	 *         {@link Eigenschappenbedrijfingevuld} die voldoen aan de gegeven
	 *         parameters.
	 *         
	 * @see eigenschapBedrijfIngevuldEJB
	 * @see Eigenschappenbedrijfingevuld
	 */
	public List<Eigenschappenbedrijfingevuld> findAllEigenschappenFiliaal(int catId) {

		eigenschappenLijst = eigenschapBedrijfIngevuldEJB.findAllEigenschappenCategorie(filiaalId, catId);
		eigenschappenLijsten.put(catId, eigenschappenLijst);
		Collections.sort(eigenschappenLijst, (a, b) -> a.getCategorieId() < b.getCategorieId() ? -1
				: a.getCategorieId() == b.getCategorieId() ? 0 : 1);
		return eigenschappenLijsten.get(catId);
	}

	/**
	 * Methode die de gegevens ingebracht door de gebruiker gaat toepassen voor
	 * de custom eigenschap.
	 * 
	 * @return String de gebruiker wordt doorverwezen naar een webpagina
	 * @see eigenschapBedrijfIngevuldEJB
	 * @see customEigenschapIngevuldEJB
	 */
	public String wijzigEigenschappen() {
		for (Entry<Integer, List<Eigenschappenbedrijfingevuld>> entry : eigenschappenLijsten.entrySet()) {
			for (Eigenschappenbedrijfingevuld ebi : entry.getValue()) {
				eigenschapBedrijfIngevuldEJB.wijzigEigenschap(ebi);
			}
		}
		for (Entry<Integer, List<Customeigenschapingevuld>> entry : customEigenschappenLijsten.entrySet()) {
			for (Customeigenschapingevuld cei : entry.getValue()) {
				customEigenschapIngevuldEJB.wijzigEigenschap(cei);
			}
		}
		return "Dashboard-reg.faces?faces-redirect=true";
	}

	/**
	 * Methode die een nieuwe eigenschap toevoegt.
	 * 
	 * @see eigenschapEJB
	 */
	public void voegEigenschapToe() {
		eigenschapEJB.voegEigenschapToe(eigenschap);
	}

	/**
	 * Methode die een nieuwe eigenschap met een drop down menu toevoegt.
	 * 
	 * @see eigenschapEJB
	 */
	public void voegEigenschapDropdownMenuToe() {
		eigenschapDropdownMenu.setMenustring(createMenuString());
		eigenschapEJB.voegEigenschapToe(eigenschapDropdownMenu);
	}

	/**
	 * Methode die een nieuwe custom eigenschap met een drop down menu toevoegt.
	 * 
	 * @see customEigenschapEJB
	 * @see customEigenschapIngevuldEJB
	 */
	public void voegCustomEigenschapDropdownMenuToe() {
		customEigenschapDropdownMenu.setBedrijf(bedrijfEJB.findBedrijf(bedrijfId));
		customEigenschapDropdownMenu.setMenuString(createCustomMenuString());
		customEigenschapEJB.voegCustomEigenschapToe(customEigenschapDropdownMenu);
		for (FiliaalGegevens f : filiaalEJB.findAllFilialenBedrijf(bedrijfId)) {
			customEigenschapIngevuldEJB.createCustomEigenschapIngevuld(customEigenschapDropdownMenu, f);
		}
	}

	/**
	 * Methode die alle eigenschappen van een bedrijf die voldoen aan het
	 * megegeven categorieId zoekt.
	 * 
	 * @param categorieId
	 *            Het id van de categorie waaraan de eigenschappen moeten
	 *            voldoen.
	 * @return List Lijst met objecten van het type {@link Eigenschappen} die
	 *         voldoen aan de paramaters.
	 *         
	 * @see eigenschapEJB
	 * @see Eigenschappen
	 */
	public List<Eigenschappen> findAllEigenschappen(int categorieId) {
		eigenschappen.clear();
		eigenschappen = eigenschapEJB.findAllEigenschappenCategorie(categorieId);
		Set<Integer> eigId = new HashSet<>();
		for (Eigenschappenbedrijf eb : eigenschapBedrijfEJB.findAllEigenschappenBedrijf(bedrijfId)) {
			eigId.add(eb.getEigenschapId());
		}
		for (Eigenschappen e : eigenschappen) {
			boolean eig = false;
			if (eigId.contains(e.getEigenschappenId())) {
				eig = true;
			}

			checked.put(e.getEigenschappenId(), eig);
			checkedForDeletion.put(e.getEigenschappenId(), Boolean.FALSE);
		}
		return eigenschappen;
	}

	/**
	 * Methode die een object van het type {@link Eigenschappen} aan een bedrijf
	 * toevoegt.
	 * 
	 * @see eigenschapBedrijfEJB
	 */
	public void voegEigenschapBedrijfToe() {
		List<Eigenschappenbedrijf> eigBedr = eigenschapBedrijfEJB.findAllEigenschappenBedrijf(bedrijfId);
		Set<Integer> tempSet = new HashSet<>();
		for (Eigenschappenbedrijf eb : eigBedr) {
			tempSet.add(eb.getEigenschapId());
		}
		for (Entry<Integer, Boolean> entry : checked.entrySet()) {
			if (entry.getValue()) {
				if (!tempSet.contains(entry.getKey())) {
					
					eigenschapBedrijfEJB.voegEigenschapToe(eigenschapEJB.findEigenschap(entry.getKey()),
							bedrijfEJB.findBedrijf(bedrijfId));
					for (FiliaalGegevens f : filiaalEJB.findAllFilialenBedrijf(bedrijfId)) {
						eigenschapBedrijfIngevuldEJB
								.createEigenschapIngevuld(eigenschapEJB.findEigenschap(entry.getKey()), f);
					}
				}
			} else if (!entry.getValue() && tempSet.contains(entry.getKey())) {

				eigenschapBedrijfEJB.verwijderEigenschapBedrijf(entry.getKey(), bedrijfId);
				eigenschapBedrijfIngevuldEJB.verwijderEigenschapBedrijfIngevuld(entry.getKey(), bedrijfId);
			}
		}
	}

	/**
	 * Methode die alle categorie�n teruggeeft.
	 * 
	 * @return List Lijst van objecten van het type {@link Categorie}.
	 * 
	 * @see categorieEJB
	 */
	public List<Categorie> findAllCategorie() {
		return categorieEJB.findAllCategorie();
	}

	/**
	 * Methode die een object van het type {@link Eigenschappen} verwijdert.
	 * @see eigenschapEJB
	 * @see eigenschapBedrijfEJB
	 * @see eigenschapBedrijfIngevuldEJB
	 */
	public void verwijderEigenschap() {
		List<Integer> toRemove = new ArrayList<>();
		for (Entry<Integer, Boolean> entry : checkedForDeletion.entrySet()) {
			if (entry.getValue()) {
				
				eigenschapEJB.verwijderEigenschap(entry.getKey());
				eigenschapBedrijfEJB.verwijderEigenschapBedrijf(entry.getKey(), bedrijfId);
				eigenschapBedrijfIngevuldEJB.verwijderEigenschapBedrijfIngevuld(entry.getKey(), bedrijfId);
				toRemove.add(entry.getKey());
			}
		}
		for (Integer i : toRemove) {
			checkedForDeletion.remove(i);
		}
	}

	/**
	 * Methode die een nieuwe categorie toevoegt.
	 * 
	 * @see categorieEJB
	 */
	public void voegCategorieToe() {
		categorieEJB.voegCategorieToe(categorie);
	}

	/**
	 * Methode die een categorie verwijderd.
	 * 
	 * @param catId
	 *            Id van de categorie.
	 *            
	 * @see categorieEJB
	 */
	public void verwijderCategorie(int catId) {
		categorieEJB.verwijderCategorie(catId);
	}

	/**
	 * Menu die menu opties omzet naar een komma seperated string.
	 * 
	 * @return String Komma seperated string.
	 */
	public String createMenuString() {
		StringBuilder sb = new StringBuilder();
		if (!menuListOption1.isEmpty()) {

			sb.append(menuListOption1);
		}
		if (!menuListOption2.isEmpty()) {
			sb.append(",");
			sb.append(menuListOption2);
		}
		if (!menuListOption3.isEmpty()) {
			sb.append(",");
			sb.append(menuListOption3);
		}
		if (!menuListOption4.isEmpty()) {
			sb.append(",");
			sb.append(menuListOption4);
		}
		if (!menuListOption5.isEmpty()) {
			sb.append(",");
			sb.append(menuListOption5);
		}

		return sb.toString();

	}

	/**
	 * Menu die custom menu opties omzet naar een komma seperated string.
	 * 
	 * @return String Komma seprated string.
	 */
	public String createCustomMenuString() {
		StringBuilder sb = new StringBuilder();
		if (!menuListCustomOption1.isEmpty()) {

			sb.append(menuListCustomOption1);
		}
		if (!menuListCustomOption2.isEmpty()) {
			sb.append(",");
			sb.append(menuListCustomOption2);
		}
		if (!menuListCustomOption3.isEmpty()) {
			sb.append(",");
			sb.append(menuListCustomOption3);
		}
		if (!menuListCustomOption4.isEmpty()) {
			sb.append(",");
			sb.append(menuListCustomOption4);
		}
		if (!menuListCustomOption5.isEmpty()) {
			sb.append(",");
			sb.append(menuListCustomOption5);
		}

		return sb.toString();
	}

	/**
	 * Methode die een komma seperated string omzet naar een lijst met strings.
	 * 
	 * @param s
	 *            De om te zetten komma seperated string.
	 * @return List Lijst met strings.
	 */
	public List<String> createMenuList(String s) {
		if (!s.isEmpty()) {
			return Arrays.asList(s.split("\\s*,\\s*"));
		}
		return Collections.emptyList();
	}

	/**
	 * Methode die een gegeven string teruggeeft.
	 * 
	 * @param s Meegegeven string
	 * @return String Geeft de meegegeven string terug
	 * */
	public String toString(String s) {
		return s;
	}
	
	public void voegAfstandToe(){
		AfstandenBedrijf afstandenbedrijf=new AfstandenBedrijf(bedrijfEJB.findBedrijf(bedrijfId));
		afstandenBedrijfEJB.voegAfstandToe(afstandenbedrijf);
		
		for (FiliaalGegevens f : filiaalEJB.findAllFilialenBedrijf(bedrijfId)) {
			Afstand afstand=new Afstand(f.getFiliaalid());
			afstandEJB.nieuwFiliaal(afstand);
			}
			
		}
	public void voegOppervlakteToe(){
		OppervlaktenBedrijf oppervlaktenbedrijf=new OppervlaktenBedrijf(bedrijfEJB.findBedrijf(bedrijfId));
		oppervlaktenBedrijfEJB.voegOppervlakToe(oppervlaktenbedrijf);
		
		for (FiliaalGegevens f : filiaalEJB.findAllFilialenBedrijf(bedrijfId)) {
			Oppervlakte oppervlak=new Oppervlakte(f.getFiliaalid());
			oppervlakEJB.nieuwFiliaal(oppervlak);
			}
			
		}
	

	/* Getters en setters */
	public Customeigenschap getCustomEigenschapDropdownMenu() {
		return customEigenschapDropdownMenu;
	}

	public void setCustomEigenschapDropdownMenu(Customeigenschap customEigenschapDropdownMenu) {
		this.customEigenschapDropdownMenu = customEigenschapDropdownMenu;
	}

	public Eigenschappen getEigenschapDropdownMenu() {
		return eigenschapDropdownMenu;
	}

	public void setEigenschapDropdownMenu(Eigenschappen eigenschapDropdownMenu) {
		this.eigenschapDropdownMenu = eigenschapDropdownMenu;
	}

	public void setCheckedForCustomDeletion(Map<Integer, Boolean> checkedForCustomDeletion) {
		this.checkedForCustomDeletion = checkedForCustomDeletion;
	}

	public Map<Integer, Boolean> getCheckedForDeletion() {
		return checkedForDeletion;
	}

	public void setCheckedForDeletion(Map<Integer, Boolean> checkedForDeletion) {
		this.checkedForDeletion = checkedForDeletion;
	}

	public Map<Integer, Boolean> getChecked() {
		return checked;
	}

	public void setChecked(Map<Integer, Boolean> checked) {
		this.checked = checked;
	}

	public Eigenschappenbedrijf getEigenschapBedrijf() {
		return eigenschapBedrijf;
	}

	public void setEigenschapBedrijf(Eigenschappenbedrijf eigenschapBedrijf) {
		this.eigenschapBedrijf = eigenschapBedrijf;
	}

	public Eigenschappen getEigenschap() {
		return eigenschap;
	}

	public void setEigenschap(Eigenschappen eigenschap) {
		this.eigenschap = eigenschap;
	}

	public Customeigenschapingevuld getCustomEigenschapIngevuld() {
		return customEigenschapIngevuld;
	}

	public void setCustomEigenschapIngevuld(Customeigenschapingevuld customEigenschapIngevuld) {
		this.customEigenschapIngevuld = customEigenschapIngevuld;
	}

	public Customeigenschap getCustomEigenschap() {
		return customEigenschap;
	}

	public void setCustomEigenschap(Customeigenschap customEigenschap) {
		this.customEigenschap = customEigenschap;
	}

	public int getFiliaalId() {
		return filiaalId;
	}

	public void setFiliaalId(int filiaalId) {
		this.filiaalId = filiaalId;
	}

	public int getBedrijfId() {
		return bedrijfId;
	}

	public void setBedrijfId(int bedrijfId) {
		this.bedrijfId = bedrijfId;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public List<Eigenschappenbedrijfingevuld> getEigenschappenLijst() {
		return eigenschappenLijst;
	}

	public void setEigenschappenLijst(List<Eigenschappenbedrijfingevuld> eigenschappenLijst) {
		this.eigenschappenLijst = eigenschappenLijst;
	}

	public String getMenuListOption1() {
		return menuListOption1;
	}

	public void setMenuListOption1(String menuListOption1) {
		this.menuListOption1 = menuListOption1;
	}

	public String getMenuListOption2() {
		return menuListOption2;
	}

	public void setMenuListOption2(String menuListOption2) {
		this.menuListOption2 = menuListOption2;
	}

	public String getMenuListOption3() {
		return menuListOption3;
	}

	public void setMenuListOption3(String menuListOption3) {
		this.menuListOption3 = menuListOption3;
	}

	public String getMenuListOption4() {
		return menuListOption4;
	}

	public void setMenuListOption4(String menuListOption4) {
		this.menuListOption4 = menuListOption4;
	}

	public String getMenuListOption5() {
		return menuListOption5;
	}

	public void setMenuListOption5(String menuListOption5) {
		this.menuListOption5 = menuListOption5;
	}

	public String getMenuListCustomOption1() {
		return menuListCustomOption1;
	}

	public void setMenuListCustomOption1(String menuListCustomOption1) {
		this.menuListCustomOption1 = menuListCustomOption1;
	}

	public String getMenuListCustomOption2() {
		return menuListCustomOption2;
	}

	public void setMenuListCustomOption2(String menuListCustomOption2) {
		this.menuListCustomOption2 = menuListCustomOption2;
	}

	public String getMenuListCustomOption3() {
		return menuListCustomOption3;
	}

	public void setMenuListCustomOption3(String menuListCustomOption3) {
		this.menuListCustomOption3 = menuListCustomOption3;
	}

	public String getMenuListCustomOption4() {
		return menuListCustomOption4;
	}

	public void setMenuListCustomOption4(String menuListCustomOption4) {
		this.menuListCustomOption4 = menuListCustomOption4;
	}

	public String getMenuListCustomOption5() {
		return menuListCustomOption5;
	}

	public void setMenuListCustomOption5(String menuListCustomOption5) {
		this.menuListCustomOption5 = menuListCustomOption5;
	}

	public Map<Integer, List<Eigenschappenbedrijfingevuld>> getEigenschappenLijsten() {
		return eigenschappenLijsten;
	}

	public void setEigenschappenLijsten(Map<Integer, List<Eigenschappenbedrijfingevuld>> eigenschappenLijsten) {
		this.eigenschappenLijsten = eigenschappenLijsten;
	}

	public Map<Integer, List<Customeigenschapingevuld>> getCustomEigenschappenLijsten() {
		return customEigenschappenLijsten;
	}

	public void setCustomEigenschappenLijsten(Map<Integer, List<Customeigenschapingevuld>> customEigenschappenLijsten) {
		this.customEigenschappenLijsten = customEigenschappenLijsten;
	}
	
	public Map<Integer, Boolean> getCheckedForCustomDeletion() {
		return checkedForCustomDeletion;
	}

}
