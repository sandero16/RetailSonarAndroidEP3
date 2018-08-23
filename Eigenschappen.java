package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the eigenschappen database table.
 * 
 */
@Entity
@NamedQuery(name="Eigenschappen.findAll", query="SELECT e FROM Eigenschappen e")
public class Eigenschappen implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="eigenschappen_id")
	private int eigenschappenId;

	@Column(name="categorie_id")
	private int categorieId;
	
	private String naam;
	
	private String eenheid;

	private String type;
	
	private String menustring;

	public Eigenschappen() {
	}
	
	public String getEenheid() {
		return this.eenheid;
	}

	public void setEenheid(String eenheid) {
		this.eenheid = eenheid;
	}

	public int getEigenschappenId() {
		return this.eigenschappenId;
	}

	public void setEigenschappenId(int eigenschappenId) {
		this.eigenschappenId = eigenschappenId;
	}
	
	public int getCategorieId() {
		return this.categorieId;
	}

	public void setCategorieId(int categorieId) {
		this.categorieId = categorieId;
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