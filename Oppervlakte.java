package data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the bedrijven database table.
 * 
 */
@Entity
@Table(name="oppervlakte")
@NamedQuery(name="Oppervlakte.findAll", query="SELECT o FROM Oppervlakte o")
public class Oppervlakte implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idoppervlakte;
	
	private int waarde;
	
	@Column(name="filiaal_filiaalid")
	private int filiaalId;
	
	
	public Oppervlakte(){
		
	}
	public Oppervlakte(int id){
		this.filiaalId=id;
	}
	public Oppervlakte(int id, int waarde){
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
