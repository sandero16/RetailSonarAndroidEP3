package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Customeigenschap;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor custom eigenschappen verwerkt. 
 * */
@Stateless
public class CustomEigenschapManagementEJB implements CustomEigenschapManagementEJBLocal{
	
	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die een object van het type {@link Customeigenschap} toevoegd aan de database.
	 * 
	 * @param customEigenschap	Een object van het type {@link Customeigenschap} dat moet toegevoegd worden aan de database
	 * 
	 * @see						Customeigenschap
	 * */
	@Override
	public void voegCustomEigenschapToe(Customeigenschap customEigenschap){
		em.persist(customEigenschap);
	}
	
	/**
	 * Methode die alle objecten van het type {@link Customeigenschap} van een bedrijf uit database opvraagt aan de hand van een bedrijf id.
	 * 
	 * @param	bedrijfId	Het id van het bedrijf waarvan de objecten worden opgevraagd
	 * @return	List	Een lijst met alle objecten van het type {@link Customeigenschap} die voldoen aan de query
	 * @see								Customeigenschap
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customeigenschap> findAllCustomEigenschappenBedrijf(int bedrijfId){
		Query q = em.createQuery("SELECT c from Customeigenschap c WHERE c.bedrijf.bedrijfId =:bedrijfId");
		q.setParameter("bedrijfId", bedrijfId);
		return q.getResultList();
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Customeigenschap} in de database verwijderd op basis van het eigenschap id en het bedrijf id.
	 * 
	 * @param	customEigenschapId	Het id van de eigenschap
	 * @param	bedrijfId			Het id van het bedrijf waavan de eigenschap verwijderd moet worden
	 * @see							Customeigenschap
	 * */
	@Override
	public void verwijderEigenschap(int customEigenschapId, int bedrijfId){
		Query q = em.createQuery("SELECT c FROM Customeigenschap c WHERE c.customEigenschap_id =:customEigenschapId AND c.bedrijf.bedrijfId =:bedrijfId");
		q.setParameter("customEigenschapId", customEigenschapId);
		q.setParameter("bedrijfId", bedrijfId);
		Customeigenschap c = (Customeigenschap) q.getResultList().get(0);
		em.remove(c);
	}
}
