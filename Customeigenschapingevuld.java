package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the customeigenschapingevuld database table.
 * 
 */
@Entity
@NamedQuery(name="Customeigenschapingevuld.findAll", query="SELECT c FROM Customeigenschapingevuld c")
public class Customeigenschapingevuld implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int customEigenschapIngevuld_id;
	
	@Column(name="categorie_id")
	private int categorieId;
	
	@ManyToOne
	private FiliaalGegevens filiaal;
	
	@Column(name="customEigenschap_id")
	private int customeigenschapId;

	@Lob
	private String inhoud;
	
	private String eenheid;

	private String naam;

	private String type;
	
	private String menustring;

	public Customeigenschapingevuld() {
	}
	
	public Customeigenschapingevuld(int customEigenschapIngevuld_id, int categorieId, FiliaalGegevens filiaal, String inhoud, String eenheid, String naam, String type){
		this.customEigenschapIngevuld_id = customEigenschapIngevuld_id;
		this.categorieId = categorieId;
		this.filiaal = filiaal;
		this.inhoud = inhoud;
		this.eenheid = eenheid;
		this.naam = naam;
		this.type = type;
	}

	
	public String getMenuString(){
		return menustring;
	}
	
	public void setMenuString(String s){
		menustring = s;
	}
	
	public int getCategorieId() {
		return this.categorieId;
	}

	public void setCategorieId(int categorieId) {
		this.categorieId = categorieId;
	}
	
	public String getEenheid() {
		return eenheid;
	}

	public void setEenheid(String eenheid) {
		this.eenheid = eenheid;
	}
	
	public int getCustomeigenschapId() {
		return this.customeigenschapId;
	}

	public void setCustomeigenschapId(int customeigenschapId) {
		this.customeigenschapId = customeigenschapId;
	}

	public int getCustomEigenschapIngevuld_id() {
		return this.customEigenschapIngevuld_id;
	}

	public void setCustomEigenschapIngevuld_id(int customEigenschapIngevuld_id) {
		this.customEigenschapIngevuld_id = customEigenschapIngevuld_id;
	}

	public String getInhoud() {
		return this.inhoud;
	}

	public void setInhoud(String inhoud) {
		this.inhoud = inhoud;
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public FiliaalGegevens getFiliaal() {
		return filiaal;
	}

	public void setFiliaal(FiliaalGegevens filiaal) {
		this.filiaal = filiaal;
	}

}