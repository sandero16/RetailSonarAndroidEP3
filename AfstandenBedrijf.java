package data;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the bedrijven database table.
 * 
 */
@Entity
@Table(name="afstandenLeeg")
@NamedQuery(name="AfstandenBedrijf.findAll", query="SELECT a FROM AfstandenBedrijf a")
public class AfstandenBedrijf {

	@Id
	private int idafstandenLeeg;
	
	@ManyToOne
	private Bedrijf bedrijf;
	
	public AfstandenBedrijf(){
		
	}
	public AfstandenBedrijf(Bedrijf bedrijf){
		this.bedrijf=bedrijf;
	}

	
	
}
