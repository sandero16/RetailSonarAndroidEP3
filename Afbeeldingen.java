package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the afbeeldingen database table.
 * 
 */
@Entity
@NamedQuery(name="Afbeeldingen.findAll", query="SELECT a FROM Afbeeldingen a")
public class Afbeeldingen implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int afbeeldingen_id;

	@ManyToOne
	private FiliaalGegevens filiaalGegevens;

	private String pad;
	
	@Lob
	private byte[] waarde;
	
	public Afbeeldingen() {
	}

	public FiliaalGegevens getFiliaalGegevens() {
		return filiaalGegevens;
	}

	public void setFiliaalGegevens(FiliaalGegevens filiaalGegevens) {
		this.filiaalGegevens = filiaalGegevens;
	}
	
	public int getAfbeeldingen_id() {
		return this.afbeeldingen_id;
	}

	public void setAfbeeldingen_id(int afbeeldingen_id) {
		this.afbeeldingen_id = afbeeldingen_id;
	}

	public String getPad() {
		return this.pad;
	}

	public void setPad(String pad) {
		this.pad = pad;
	}
	
	public byte[] getWaarde() {
		return this.waarde;
	}

	public void setWaarde(byte[] waarde) {
		this.waarde = waarde;
	}

}