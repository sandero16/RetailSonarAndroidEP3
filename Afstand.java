package data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the bedrijven database table.
 * 
 */
@Entity
@Table(name="afstanden")
@NamedQuery(name="Afstanden.findAll", query="SELECT a FROM Afstand a")
public class Afstand implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idafstand;
	
	private int waarde;
	
	@Column(name="filiaal_filiaalid")
	private int filiaalId;
	
	
	public Afstand(){
		
	}
	public Afstand(int id){
		this.filiaalId=id;
	}
	public Afstand(int id, int waarde){
		this.waarde=waarde;
		this.filiaalId=id;
	}
	public int getWaarde(){
		return waarde;
	}
	public int getfiliaalId(){
		return filiaalId;
	}
	public void setWaarde(int waarde){
		this.waarde=waarde;
	}
	

}
