package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Bedrijf;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor bedrijven verwerkt. 
 * */
@Named
@Stateless
public class BedrijfManagementEJB implements BedrijfManagementEJBLocal {

	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die een object van het type {@link Bedrijf} toegvoegd aan de database.
	 * 
	 * @param 	bedrijf Een object van het type {@link Bedrijf}.
	 *
	 * @see 	Bedrijf
	 * */
	@Override
	public void maakBedrijf(Bedrijf bedrijf){
		em.persist(bedrijf);
	}
	
	/**
	 * Methode die alle objecten van het type {@link Bedrijf} uit de database opvraagt en teruggeeft in een List.
	 * 
	 * 
	 * @return 	List Een List met alle objecten van het type {@link Bedrijf} uit de database.
	 * @see 	Bedrijf
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Bedrijf> findAllBedrijven(){
		Query q = em.createQuery("SELECT b FROM Bedrijf b WHERE b.bedrijfId > 0");
		return q.getResultList();
	}
	
	/**
	 * Methode die op basis van een id het bijhorende object {@link Bedrijf} teruggeeft.
	 * 
	 * @param 	id	 	Het id van het gevraagde bedrijf.
	 * @return 	Bedrijf Object van het type {@link Bedrijf} dat overeenkomt met het id.
	 * @see 			Bedrijf
	 * */
	@Override
	public Bedrijf findBedrijf(int id){
		return em.find(Bedrijf.class, id);
	}
	
	/**
	 * Methode die een bedrijf zoekt op basis van een naam en het bijhorende object van het type {@link Bedrijf} teruggeeft.
	 * 
	 * @param 	naam 		De naam van het bedrijf.
	 * @return 	Bedrijf 	Object van het type {@link Bedrijf} dat voldoet aan de naam.
	 * @see					Bedrijf
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public Bedrijf findBedrijf(String naam){
		Query q = em.createQuery("SELECT b FROM Bedrijf b WHERE b.naam=:n");
		q.setParameter("n", naam);
		List<Object> result = q.getResultList();
		if(!result.isEmpty()) return (Bedrijf)result.get(0);
		else return null;
	}

	/**
	 * Methode die een bestaand object van het type {@link Bedrijf} in de databank aanpast.
	 * 
	 * @param 	bedrijf Een object van het type {@link Bedrijf}.
	 * 
	 * @see				Bedrijf
	 * */
	@Override
	public void wijzigBedrijf(Bedrijf bedrijf){
		em.merge(bedrijf);
	}
	
}
