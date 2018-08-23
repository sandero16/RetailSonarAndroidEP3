package ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Customeigenschap;
import data.Customeigenschapingevuld;
import data.FiliaalGegevens;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor custom ingevulde eigenschappen verwerkt. 
 * */

@Stateless
public class CustomEigenschapIngevuldManagementEJB implements CustomEigenschapIngevuldManagementEJBLocal {

	@PersistenceContext(unitName = "RetailSonarJPA")
	private EntityManager em;

	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;

	private static final String FILIAALIDQUERYPARAM = "filiaalId";
	
	/**
	 * Methode die alle objecten van het type {@link Customeigenschapingevuld}
	 * van een filiaal opvraagt op basis van het id en teruggeeft in een lijst.
	 * 
	 * @param filiaalId
	 *            Het id van het opgevraagde filiaal
	 * @return List Een list met alle objecten van het
	 *         type {@link Customeigenschapingevuld} die voldoen aan de query
	 * @see Customeigenschapingevuld
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customeigenschapingevuld> findAllEigenschappen(int filiaalId,int catId) {
		Query q = em.createQuery("SELECT c FROM Customeigenschapingevuld c WHERE c.filiaal.filiaalid =:filiaalId AND c.categorieId =:catId");
		q.setParameter(FILIAALIDQUERYPARAM, filiaalId);
		q.setParameter("catId", catId);
		return q.getResultList();
	}
	
	/**
	 * Methode alle objecten van het type {@link Customeigenschapingevuld} van een filiaal teruggeeft.
	 * 
	 * @param filiaalId Het id van het gegeven filiaal
	 * @return List Een lijst met objecten van het type {@link Customeigenschapingevuld}
	 * */
	@Override
	public List<Customeigenschapingevuld> findAllEigenschappen(int filiaalId){
		Query q = em.createQuery("SELECT c FROM Customeigenschapingevuld c WHERE c.filiaal.filiaalid =:filiaalId ");
		q.setParameter(FILIAALIDQUERYPARAM, filiaalId);
		return q.getResultList();
	}

	/**
	 * Methode die een object(en) van het type {@link Customeigenschapingevuld}
	 * toevoegd aan de database. Deze objecten komen binnen als een type
	 * {@link Customeigenschap} en worden elk eerst gekopieerd naar een object
	 * van het type {@link Customeigenschapingevuld} om daarna in de database
	 * gestoken te worden.
	 * 
	 * @param newEig	Lijst met objecten van het type {@link Customeigenschap} die
	 *            voor een filiaal aangemaakt moeten worden als een type
	 *            {@link Customeigenschapingevuld}
	 * 
	 * @see 			Customeigenschap
	 * @see				Customeigenschapingevuld
	 */
	@Override
	public void createCustomEigenschapIngevuld(Customeigenschap newEig, FiliaalGegevens filiaal) {
		
			Customeigenschapingevuld temp = new Customeigenschapingevuld();
			temp.setFiliaal(filiaal);
			temp.setNaam(newEig.getNaam());
			temp.setEenheid(newEig.getEenheid());
			temp.setCustomeigenschapId(1);
			temp.setMenuString(newEig.getMenuString());
			temp.setCategorieId(newEig.getCategorieId());
			em.persist(temp);
		
	}

	/**
	 * Methode die een bestaand object van het type
	 * {@link Customeigenschapingevuld} in de database wijzigt.
	 * 
	 * @param c
	 *            Object van het type {@link Customeigenschapingevuld} dat
	 *            gewijzigd moet worden
	 * 
	 * @see Customeigenschapingevuld
	 */
	@Override
	public void wijzigEigenschap(Customeigenschapingevuld c) {
		em.merge(c);
	}

	/**
	 * Methode die een bestaand object van het type
	 * {@link Customeigenschapingevuld} gaat verwijdereen uit de database op
	 * basis van een customEigenschapId en een bedrijfId.
	 * 
	 * @param customEigenschapId
	 *            Het id van de eigenschap in de database
	 * @param bedrijfId
	 *            Het id van het bedrijf in de ddatabase
	 * 
	 * @see Customeigenschapingevuld
	 */
	@Override
	public void verwijderCustomEigenschap(int customEigenschapId, int bedrijfId) {
		List<FiliaalGegevens> temp = filiaalEJB.findAllFilialenBedrijf(bedrijfId);
		if (!temp.isEmpty()) {
			for (FiliaalGegevens f : temp) {
				int filiaalId = f.getFiliaalid();
				Query q = em.createQuery(
						"SELECT c FROM Customeigenschapingevuld c WHERE c.customEigenschapIngevuld_id =:eigenschapId AND c.filiaal.filiaalid =:filiaalId");
				q.setParameter("eigenschapId", customEigenschapId);
				q.setParameter(FILIAALIDQUERYPARAM, filiaalId);
				if (!q.getResultList().isEmpty()) {
					Customeigenschapingevuld cei = (Customeigenschapingevuld) q.getResultList().get(0);
					em.remove(cei);
				}

			}
		}
	}
	
	/**
	 * Methode die objecten van het type {@link Customeigenschapingevuld} maakt uit een lijst van hetzelfde type voor een
	 * bepaald filiaal.
	 *
	 * 
	 * @param newEig
	 *            de nieuwe eigenschappen die moten worden aangemaakt
	 * 
	 * @see Customeigenschapingevuld
	 */
	@Override
	public void createCustomEigenschapIngevuld(List<Customeigenschap> newEig, FiliaalGegevens filiaal){
		for(Customeigenschap c : newEig){
			Customeigenschapingevuld temp = new Customeigenschapingevuld();
			temp.setFiliaal(filiaal);
			temp.setNaam(c.getNaam());
			temp.setType(c.getType());
			temp.setCategorieId(c.getCategorieId());
			temp.setEenheid(c.getEenheid());
			temp.setCustomeigenschapId(c.getCustomEigenschap_id());
			em.persist(temp);
		}
	}
	
	/**
	 * Methode die objecten van het type {@link Customeigenschapingevuld} uit de database haalt, de inhoud aanpast en
	 * vervolgens die aanpassing doorgeeft aan de database.
	 *
	 * 
	 * @param eigenschappen
	 *            de eigenschappen die moeten worden geupdated
	 * 
	 * @see Customeigenschapingevuld
	 */
	@Override
	public void mergeEigenschappen(List<Customeigenschapingevuld> eigenschappen){
		Customeigenschapingevuld upTeDatenEigenschap;
		for(Customeigenschapingevuld e : eigenschappen){
			System.out.println("extra:"+e.getInhoud());
			System.out.println("extra id: "+ e.getCustomEigenschapIngevuld_id());
			Query q = em.createQuery("SELECT e FROM Customeigenschapingevuld e WHERE e.customEigenschapIngevuld_id =:eigenschapId");
			
			q.setParameter("eigenschapId", e.getCustomEigenschapIngevuld_id());
			upTeDatenEigenschap = (Customeigenschapingevuld) q.getResultList().get(0);
			
			upTeDatenEigenschap.setInhoud(e.getInhoud());
			em.merge(upTeDatenEigenschap);
		}
	}

}
