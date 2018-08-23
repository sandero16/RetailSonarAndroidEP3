package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Taak;
import data.FiliaalGegevens;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor taken verwerkt. 
 * */
@Named
@Stateless
public class TakenManagementEJB implements TakenManagementEJBLocal{

	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die alle objecten van het type {@link Taak} teruggeeft die als attibuut de meegegeven paramater hebben.
	 * 
	 * @param	filiaalId	Het id an het filiaal waartoe de objecten moeten behoren.
	 * @return	List	Lijst met objecten die voldoen aan de query.
	 * @see	Taak
	 * @see FiliaalGegevens
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Taak> findAllTakenFiliaal(int filiaalId){
		Query q = em.createQuery("SELECT t FROM Taak t WHERE t.filiaal.filiaalid =:filiaalId");
		q.setParameter("filiaalId",filiaalId);
		return q.getResultList();
	}

	
	/**
	 * Methode die een object van het type {@link Taak} toevoegd aan de database.
	 * 
	 * @param 	taak	Het toe te voegen object.
	 * 
	 * @see Taak
	 * */
	@Override
	public void voegTakenToe(Taak taak){
		em.persist(taak);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Taak} verijderd uit de database.
	 * 
	 * @param	taakId	Het id van het te verwijderen object.
	 * 
	 * @see 	Taak
	 * */
	@Override
	public void verwijderTaak(int taakId){
		Taak t = em.find(Taak.class, taakId);
		em.remove(t);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Taak} uit de database wijzigt.
	 * 
	 * @param	t	Het de wijzigen object.
	 * @see 	Taak
	 * */
	@Override
	public void updateTaken(Taak t){
		em.merge(t);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Taak} teruggeeft op basis van het taakId.
	 * 
	 * @param	taakId	Het id van het gevraagde object.
	 * @return	Taak	Het object dat voldoet aan de query.
	 * @see 	Taak
	 * */
	@Override
	public Taak findTaak(int taakId){
		Query q = em.createQuery("SELECT t FROM Taak t WHERE t.taakId =:taakId");
		q.setParameter("taakId",taakId);
		return (Taak) q.getResultList().get(0);
	}
	
	/**
	 * methode die de status-attribuut van een ingegeven lijst van taken instelt om 1 (voor "gemaakt")
	 * 
	 * @param	taken een lijst van taken die moeten worden aangepast.
	 * @see 	Taak
	 * */
	@Override
	public void setIngevuld(List<Taak> taken){
		if(taken==null)return;
		for(Taak t : taken){
			t.setStatus(1);
			em.merge(t);
		}
	}

}
