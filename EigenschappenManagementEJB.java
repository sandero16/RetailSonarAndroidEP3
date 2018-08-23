package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Eigenschappen;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor eigenschappen verwerkt. 
 * */
@Stateless
public class EigenschappenManagementEJB implements EigenschappenManagementEJBLocal{

	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die een nieuw object van hte type {@link Eigenschappen} toevoegd aan de database.
	 * 
	 * @param eig	Object van het type {@link Eigenschappen}.
	 * 
	 * @see 	Eigenschappen
	 * */
	@Override
	public void voegEigenschapToe(Eigenschappen eig){
		em.persist(eig);
	}
	
	/**
	 * Methode die alle objecten van het type {@link Eigenschappen} uit de database teruggeeft.
	 * 
	 * 
	 * @return Listlijst met objecten van het type {@link Eigenschappen}.
	 * @see 	Eigenschappen
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Eigenschappen> findAllEigenschappen(){
		Query q = em.createQuery("SELECT e FROM Eigenschappen e");
		return q.getResultList();
	}
	
	/**
	 * Methode die een object van het type {@link Eigenschappen} opvraagt uit de database op basis van zijn id en dit teruggeeft.
	 * 
	 * @param	id	Het id van het opgevraagde object.
	 * @return	Eigenschappen het object dat voldoet aan de query.
	 * @see 	Eigenschappen
	 * */
	@Override
	public Eigenschappen findEigenschap(int id){
		Query q = em.createQuery("SELECT e FROM Eigenschappen e WHERE e.eigenschappenId =:id");
		q.setParameter("id", id);
		return (Eigenschappen) q.getResultList().get(0);
	}
	
	/**
	 * Methode die alle objecten van het type {@link Eigenschappen} opzoekt die voldoen aan een bepakde categorie op basis van het categori id
	 * 
	 * @param categorieId	Het id van de gevraagde categorie.
	 * @return	List	Een lijst met objecten van het type {@link Eigenschappen} die voldoen aan de query.
	 * @see 	Eigenschappen
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Eigenschappen> findAllEigenschappenCategorie(int categorieId){
		Query q = em.createQuery("SELECT e FROM Eigenschappen e WHERE e.categorieId =:categorieId");
		q.setParameter("categorieId", categorieId);
		return q.getResultList();
	}
	
	/**
	 * Methode die een object van het type {@link Eigenschappen} opvraagt uit de database op basis van het eigenschapId en verwijderd
	 * 
	 * @param eigenschapId	Het id van het object
	 * 
	 * @see	Eigenschappen
	 * */
	@Override
	public void verwijderEigenschap(int eigenschapId){
		Eigenschappen e = em.find(Eigenschappen.class, eigenschapId);
		em.remove(e);
	}
}
