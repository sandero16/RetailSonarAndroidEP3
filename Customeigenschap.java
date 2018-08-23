package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the customeigenschap database table.
 * 
 */
@Entity
@Table(name="CUSTOMEIGENSCHAP")
@NamedQuery(name="Customeigenschap.findAll", query="SELECT c FROM Customeigenschap c")
public class Customeigenschap implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int customEigenschap_id;

	@ManyToOne
	private Bedrijf bedrijf;

	private String eenheid;
	
	private String naam;

	private String type;
	
	private String menustring;
	
	@Column(name="categorie_id")
	private int categorieId;
	
	public Customeigenschap() {
	}
	
	public String getMenuString(){
		return menustring;
	}
	
	public void setMenuString(String s){
		menustring = s;
	}
	
	public String getEenheid() {
		return this.eenheid;
	}

	public void setEenheid(String eenheid) {
		this.eenheid = eenheid;
	}
	
	public Bedrijf getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf;
	}
	
	public int getCategorieId() {
		return categorieId;
	}

	public void setCategorieId(int categorieId) {
		this.categorieId = categorieId;
	}

	public int getCustomEigenschap_id(){
		return this.customEigenschap_id;
	}

	public void setCustomEigenschap_id(int customEigenschap_id) {
		this.customEigenschap_id = customEigenschap_id;
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

}