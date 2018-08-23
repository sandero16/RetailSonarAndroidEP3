package ejb;

import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.FiliaalGegevens;
import data.User;
import data.Bedrijf;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor filialen verwerkt. 
 * */
@Stateless
public class FiliaalManagementEJB implements FiliaalManagementEJBLocal {

	@PersistenceContext(unitName = "RetailSonarJPA")
	private EntityManager em;

	@EJB
	private GebruikerManagementEJBLocal userEJB;
	
	private static final String FILIAALIDQUERY = "SELECT f FROM FiliaalGegevens f WHERE f.userId = :user_id";
	
	private static final String USERIDQUERYPARAM = "user_id";

	/**
	 * Methode die een nieuw object van het type {@link FiliaalGegevens}
	 * toevoegd aan de database.
	 * 
	 * @param filiaalGegevens
	 *            Het object dat wordt toegeveogd aan de database.
	 * @param userName
	 *            De naam van de gebruiker die het object toevoegd, dit is nodig
	 *            voor het bedrijg te kunnen toevoegen aan het filiaal.
	 * 
	 * @see {@link FiliaalGegevens}
	 * @see {@link User}
	 * @see {@link bedrijf}
	 */
	@Override
	public int invoegenGegevens(FiliaalGegevens filiaalGegevens, String userName) {
		User user = userEJB.findPerson(userName);
		filiaalGegevens.setBedrijf(user.getBedrijf());
		em.joinTransaction();
		em.persist(filiaalGegevens);
		Query q=em.createQuery("SELECT max(f.filiaalid) AS high FROM FiliaalGegevens f");
		int lastid=(int)q.getSingleResult();
		return lastid;
	}

	/**
	 * Methode die een filiaal uit de database verwijderd.
	 * 
	 * @param filiaalId
	 *            Het id van het te verwijderen filiaal
	 */
	@Override
	public void verwijderFiliaal(int filiaalId) {
		FiliaalGegevens f = em.find(FiliaalGegevens.class, filiaalId);
		em.remove(f);
	}

	/**
	 * Methode die een object van het type {@link FiliaalGegevens} opvraagt uit
	 * de database op basis van een filiaal id.
	 * 
	 * @param filiaalId
	 *            Het id van het opgevraagde object.
	 * @return FiliaalGegevens De methode geeft een object van het type
	 *         {@link FiliaalGegevens} terug.
	 * @see {@link FiliaalGegevens}
	 */
	@Override
	public FiliaalGegevens findProject(int filiaalId) {
		return em.find(FiliaalGegevens.class, filiaalId);
	}

	/**
	 * Methode die alle objecten van het type {@link FiliaalGegevens} opvraagt
	 * uit de database die het meegegeven bedrijfId bevatten.
	 * 
	 * @param bedrijfId
	 *            Het id van het meegegeven bedrijf.
	 * @return List Lijst met alle objecten van het type {@link FiliaalGegevens}
	 *         die voldoen aan de query.
	 * @see {@link FiliaalGegevens}
	 * @see {@link Bedrijf}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FiliaalGegevens> findAllFilialenBedrijf(int bedrijfId) {
		Query q = em.createQuery("SELECT f FROM FiliaalGegevens f WHERE f.bedrijven.bedrijfId = :bedrijfid");
		q.setParameter("bedrijfid", bedrijfId);
		return q.getResultList();
	}

	/**
	 * Methode die een object van het type {@link FiliaalGegevens} teruggeeft
	 * die voldoet aan het filiaalId.
	 * 
	 * @param filiaalId
	 *            Het id van het gevraagde object.
	 * @return FiliaalGegevens Een object van het type {@link FiliaalGegevens}.
	 * @see {@link FiliaalGegevens}
	 */
	@Override
	public FiliaalGegevens findFiliaalGegevens(int filiaalId) {
		Query q = em.createQuery("SELECT f FROM FiliaalGegevens f WHERE f.filiaalid = :filiaalid");
		q.setParameter("filiaalid", filiaalId);
		return (FiliaalGegevens) q.getResultList().get(0);
	}
	
	public int findIdBedrijf(int filiaalId){
		Query q = em.createQuery("SELECT f FROM FiliaalGegevens f WHERE f.filiaalid = :filiaalid");
		q.setParameter("filiaalid", filiaalId);
		FiliaalGegevens f= (FiliaalGegevens) q.getResultList().get(0);
		Bedrijf b=f.getBedrijf();
		System.out.println(b.getBedrijfId());
		return (b.getBedrijfId());
	}

	/**
	 * Methode die een bestaand object van het type {@link FiliaalGegevens} in
	 * de databank opvraagt en wijzigt.
	 * 
	 * @param filiaalGegevens
	 *            Een object van het type {@link FiliaalGegevens}.
	 * 
	 * @see {@link FiliaalGegevens}
	 */
	@Override
	public void wijzigFiliaal(FiliaalGegevens filiaalGegevens) {
		em.merge(filiaalGegevens);
	}

	/**
	 * Methode die alle objecten van het type {@link FiliaalGegevens} van een
	 * bepaalde user teruggeeft op basis van het userId.
	 * 
	 * @param userId
	 *            Het id van de meegegeven gebruiker.
	 * @return List Lijst van alle objecten die voldoen aan de query.
	 * @see {@link FiliaalGegevens}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FiliaalGegevens> findAllFilialenRegioManager(int userId) {
		Query q = em.createQuery("SELECT f FROM FiliaalGegevens f WHERE f.userId = :userid");
		q.setParameter("userid", userId);
		return q.getResultList();
	}

	/**
	 * Methode die de lijst van namen van filialen geeft die gelinkt zijn aan een bepaalde gebruiker.
	 * 
	 * @param username
	 *            de naam van de gebruiker in kwestie.
	 *            
	 * @return een {@link List<String>} van de namen van alle filialen die bij deze gebruiker horen.
	 * @see {@link FiliaalGegevens}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAlleFilialen(String username) {
		User user = userEJB.findPerson(username);
		Query q = em.createQuery(FILIAALIDQUERY);
		q.setParameter(USERIDQUERYPARAM, user.getUserId());
		List<FiliaalGegevens> filialen = (List<FiliaalGegevens>) q.getResultList();
		List<String> filiaalNamen = new LinkedList<>();

		for (FiliaalGegevens f : filialen)
			filiaalNamen.add(f.getNaamFiliaal());
		return filiaalNamen;
	}
	/**
	 * Methode die de lijst van addressen van filialen geeft die gelinkt zijn aan een bepaalde gebruiker.
	 * 
	 * @param username
	 *            de naam van de gebruiker in kwestie.
	 *            
	 * @return een {@link List<String>} van de namen van alle addressen van filialen die bij deze gebruiker horen.
	 * @see {@link FiliaalGegevens}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAddressenVanAlleFilialen(String username) {
		User user = userEJB.findPerson(username);
		Query q = em.createQuery(FILIAALIDQUERY);
		q.setParameter(USERIDQUERYPARAM, user.getUserId());
		List<FiliaalGegevens> filialen = (List<FiliaalGegevens>) q.getResultList();
		List<String> addressen = new LinkedList<>();

		for (FiliaalGegevens f : filialen) {
			addressen.add(f.getStraat() + " " + f.getNummer() + ", " + f.getPostcode() + " " + f.getGemeente());
		}
		return addressen;
	}
	/**
	 * Methode die de lijst van id's van filialen geeft die gelinkt zijn aan een bepaalde gebruiker.
	 * 
	 * @param username
	 *            de naam van de gebruiker in kwestie.
	 *            
	 * @return een {@link List<String>} van de id's van alle addressen van filialen die bij deze gebruiker horen.
	 * @see {@link FiliaalGegevens}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findIdVanAlleFilialen(String username) {
		User user = userEJB.findPerson(username);
		Query q = em.createQuery(FILIAALIDQUERY);
		q.setParameter(USERIDQUERYPARAM, user.getUserId());
		List<FiliaalGegevens> filialen = (List<FiliaalGegevens>) q.getResultList();
		List<String> ids = new LinkedList<>();

		for (FiliaalGegevens f : filialen) {
			ids.add(f.getFiliaalid() + "");
		}
		return ids;
	}
	

}
