package data;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the bedrijven database table.
 * 
 */
@Entity
@Table(name="oppervlakteLeeg")
@NamedQuery(name="OppervlaktenBedrijf.findAll", query="SELECT o FROM OppervlaktenBedrijf o")
public class OppervlaktenBedrijf {

	@Id
	private int idoppervlakteLeeg;
	
	@ManyToOne
	private Bedrijf bedrijf;
	
	public OppervlaktenBedrijf(){
		
	}
	public OppervlaktenBedrijf(Bedrijf bedrijf){
		this.bedrijf=bedrijf;
	}

	
	
}