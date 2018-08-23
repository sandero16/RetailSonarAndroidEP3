package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the eigenschappenbedrijf database table.
 * 
 */
@Entity
@NamedQuery(name="Eigenschappenbedrijf.findAll", query="SELECT e FROM Eigenschappenbedrijf e")
public class Eigenschappenbedrijf implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="eigenschappenbedrijf_id")
	private int eigenschappenbedrijfId;

	@Column(name="categorie_id")
	private int categorieId;
	
	@ManyToOne
	private Bedrijf bedrijf;
	
	@Column(name="eigenschap_id")
	private int eigenschapId;

	private String naam;
	
	private String eenheid;
	
	private String menustring;

	private String type;

	public Eigenschappenbedrijf() {
	}
	
	public String getEenheid() {
		return this.eenheid;
	}

	public void setEenheid(String eenheid) {
		this.eenheid = eenheid;
	}
	
	public int getEigenschappenbedrijfId() {
		return this.eigenschappenbedrijfId;
	}

	public void setEigenschappenbedrijfId(int eigenschappenbedrijfId) {
		this.eigenschappenbedrijfId = eigenschappenbedrijfId;
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

	public Bedrijf getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf;
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