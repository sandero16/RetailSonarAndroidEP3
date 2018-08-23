package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Afbeeldingen;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor afbeeldingen verwerkt. 
 * */
@Named
@Stateless
public class AfbeeldingManagementEJB implements AfbeeldingManagementEJBLocal{

	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die een nieuw object {@link Afbeeldingen} toevoegd aan de database.
	 * 
	 * @param 	afb	 	Een object van het type {@link Afbeeldingen}
	 * @see				Afbeeldingen
	 * */
	@Override
	public void invoegen(Afbeeldingen afb){
		em.persist(afb);
	}
	
	/**
	 * Methode die alle afbeeldingen van een filiaal zoekt en teruggeeft als een {@link List} met objecten van het type {@link Afbeeldingen}.
	 * 
	 * @param filiaalId				 	Het id van het filiaal waarvan de afbeeldingen opgevraagd worden
	 * @return List		Lijst met alle objecten van het type {@link Afbeeldingen} die voldoen aan de query.
	 * @see 							Afbeeldingen
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Afbeeldingen> findAllAfbeeldingenFiliaal(int filiaalId){
		Query q = em.createQuery("SELECT a FROM Afbeeldingen a WHERE a.filiaalGegevens.filiaalid =:filiaalId");
		q.setParameter("filiaalId",filiaalId);
		return q.getResultList();
	}
}
