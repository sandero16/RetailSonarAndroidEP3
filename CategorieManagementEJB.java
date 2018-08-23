package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Categorie;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor categorieën verwerkt. 
 * */
@Stateless
public class CategorieManagementEJB implements CategorieManagementEJBLocal{

	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die alle objecten van het type {@link Categorie} teruggeeft die in de database zitten.
	 * 
	 * 
	 * @return List	Lijst van alle objecten die in de databse zitten.
	 * @see 	Categorie
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Categorie> findAllCategorie(){
		Query q = em.createQuery("SELECT c FROM Categorie c");
		return q.getResultList();
	}
	
	/**
	 * Methode die een nieuw object van het type {@link Categorie} toevoegd aan de database.
	 * 
	 * @param	categorie	Het object dat moet worden toegevoegd aan de database.
	 * 
	 * @see	Categorie
	 * */
	@Override
	public void voegCategorieToe(Categorie categorie){
		em.persist(categorie);
	}
	
	/**
	 * Methode die een object van het type {@link Categorie} opvraagt uit de database op basis van de het id en het verwijderd.
	 * 
	 * @param	catId	het id van het te verwijderen object.
	 * @see		Categorie 	
	 * */
	@Override
	public void verwijderCategorie(int catId){
		Categorie cat = em.find(Categorie.class, catId);
		em.remove(cat);
	}
	
	@Override
	public Categorie zoekCategorie(int catId){
		Categorie c = em.find(Categorie.class, catId);
		return c;
	}
}
