package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the filiaal database table.
 * 
 */
@Entity
@Table(name="filiaal")
@NamedQuery(name="Filiaal.findAll", query="SELECT f FROM FiliaalGegevens f")
public class FiliaalGegevens implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int filiaalid;

	private String gemeente;

	private String naamfiliaal;

	private String nummer;

	private String postcode;

	private String straat;

	@Column(name="user_id")
	private int userId;
	
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	//bi-directional many-to-one association to Bedrijven
	@ManyToOne
	private Bedrijf bedrijven;

	public FiliaalGegevens() {
	}

	public int getFiliaalid() {
		return this.filiaalid;
	}

	public void setFiliaalid(int filiaalid) {
		this.filiaalid = filiaalid;
	}

	public String getGemeente() {
		return this.gemeente;
	}

	public void setGemeente(String gemeente) {
		this.gemeente = gemeente;
	}

	public String getNaamFiliaal() {
		return this.naamfiliaal;
	}

	public void setNaamFiliaal(String naamfiliaal) {
		this.naamfiliaal = naamfiliaal;
	}

	public String getNummer() {
		return this.nummer;
	}

	public void setNummer(String nummer) {
		this.nummer = nummer;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStraat() {
		return this.straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	public Bedrijf getBedrijf() {
		return this.bedrijven;
	}

	public void setBedrijf(Bedrijf bedrijven) {
		this.bedrijven = bedrijven;
	}

}