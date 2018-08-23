package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the eigenschappenbedrijfingevuld database table.
 * 
 */
@Entity
@NamedQuery(name="Eigenschappenbedrijfingevuld.findAll", query="SELECT e FROM Eigenschappenbedrijfingevuld e")
public class Eigenschappenbedrijfingevuld implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="eigenschappenbedrijfingevuld_id")
	private int eigenschappenbedrijfingevuldId;

	@ManyToOne
	private FiliaalGegevens filiaal;
	
	@Column(name="categorie_id")
	private int categorieId;
	
	@Column(name="eigenschap_id")
	private int eigenschapId;

	@Lob
	private String inhoud;
	
	private String eenheid;

	private String naam;

	private String type;
	
	private String menustring;

	public Eigenschappenbedrijfingevuld() {
	}
	
	public Eigenschappenbedrijfingevuld(int eigenschappenBedrijfIngevuldId, FiliaalGegevens filiaal, int categorieId, String inhoud, String eenheid, String naam, String type) {
        this.eigenschappenbedrijfingevuldId = eigenschappenBedrijfIngevuldId;
        this.filiaal = filiaal;
        this.categorieId = categorieId;
        this.inhoud = inhoud;
        this.eenheid = eenheid;
        this.naam = naam;
        this.type = type;
    }

	
	public String getEenheid() {
		return this.eenheid;
	}

	public void setEenheid(String eenheid) {
		this.eenheid = eenheid;
	}
	
	public int getEigenschappenbedrijfingevuldId() {
		return this.eigenschappenbedrijfingevuldId;
	}

	public void setEigenschappenbedrijfingevuldId(int eigenschappenbedrijfingevuldId) {
		this.eigenschappenbedrijfingevuldId = eigenschappenbedrijfingevuldId;
	}
	
	public int getCategorieId() {
		return this.categorieId;
	}

	public void setCategorieId(int categorieId) {
		this.categorieId = categorieId;
	}

	public int getEigenschapId() {
		return this.eigenschapId;
	}

	public void setEigenschapId(int eigenschapId) {
		this.eigenschapId = eigenschapId;
	}

	public FiliaalGegevens getFiliaal() {
		return filiaal;
	}

	public void setFiliaal(FiliaalGegevens filiaal) {
		this.filiaal = filiaal;
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
	
	public String getMenustring() {
		return menustring;
	}

	public void setMenustring(String menustring) {
		this.menustring = menustring;
	}

}