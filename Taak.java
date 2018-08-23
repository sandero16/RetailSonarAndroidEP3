package data;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the taak database table.
 * 
 */
@Entity
@NamedQuery(name="Taak.findAll", query="SELECT t FROM Taak t")
public class Taak implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="taak_id")
	private int taakId;

	public int getTaakId() {
		return taakId;
	}

	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}



	public void setTaakId(int taakId) {
		this.taakId = taakId;
	}

	@Lob
	private String beschrijving;

	
	
	@ManyToOne
	private FiliaalGegevens filiaal;

	public FiliaalGegevens getFiliaal() {
		return filiaal;
	}



	public void setFiliaal(FiliaalGegevens filiaal) {
		this.filiaal = filiaal;
	}



	public Taak() {
	}

	

	public String getBeschrijving() {
		return this.beschrijving;
	}

	public void setBeschrijving(String beschrijving) {
		this.beschrijving = beschrijving;
	}

	

}