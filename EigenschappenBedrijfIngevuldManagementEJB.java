package ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Eigenschappen;
import data.Eigenschappenbedrijf;
import data.Eigenschappenbedrijfingevuld;
import data.FiliaalGegevens;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor ingevulde bedrijfseigenschappen verwerkt. 
 * */
@Stateless
public class EigenschappenBedrijfIngevuldManagementEJB implements EigenschappenBedrijfIngevuldManagementEJBLocal{

	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;
	
	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	private static final String FILIAALIDQUERYPARAM = "filiaalId";

	
	/**
	 * Methode die alle objecten van het type {@link Eigenschappenbedrijfingevuld} van een filiaal opvraagt ut de databsae op basis van het filiaal id.
	 * 
	 * @param	filiaalId	Het id van het filiaal waarvan de objecten worden opgevraagd
	 * @return	List	Lijst met alle objecten die voldoen aan de query
	 * @see		Eigenschappenbedrijfingevuld					
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Eigenschappenbedrijfingevuld> findAllEigenschappenCategorie(int filiaalId,int catId){
		Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijfingevuld e WHERE e.filiaal.filiaalid =:filiaalId AND e.categorieId =:catId");
		q.setParameter(FILIAALIDQUERYPARAM, filiaalId);
		q.setParameter("catId", catId);
		return q.getResultList();
	}
	
	/**
	 * Methode die alle eigenschappen van aan filiaal teruggeeft.
	 * 
	 * @param filiaalId Het id van het filiaal.
	 * @return List Lijst met objecten van het type {@link Eigenschappenbedrijfingevuld}.
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Eigenschappenbedrijfingevuld> findAllEigenschappen(int filiaalId){
		Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijfingevuld e WHERE e.filiaal.filiaalid =:filiaalId");
		q.setParameter(FILIAALIDQUERYPARAM, filiaalId);
		return q.getResultList();
	}
	
	/**
	 * Methode die een nieuw object van het type {@link Eigenschappenbedrijfingevuld} aanmaakt en aan de database toevoegd.
	 * 
	 * @param	e	 object van
	 * het type {@link Eigenschappen} dat wordt toegevoegd
	 * @param	f	Het filiaal waartoe de eigenschap behoort
	 * 
	 * @see		Eigenschappenbedrijf
	 * @see		Eigenschappenbedrijfingevuld
	 * */
	@Override
	public void createEigenschapIngevuld(Eigenschappen e, FiliaalGegevens f){
			Eigenschappenbedrijfingevuld temp = new Eigenschappenbedrijfingevuld();
			temp.setEigenschapId(e.getEigenschappenId());
			temp.setNaam(e.getNaam());
			temp.setFiliaal(f);
			temp.setCategorieId(e.getCategorieId());
			temp.setEenheid(e.getEenheid());
			temp.setMenustring(e.getMenustring());
			em.persist(temp);
	}
	
	/**
	 * Methode die een nieuw object van het type {@link Eigenschappenbedrijfingevuld} aanmaakt en aan de database toevoegd.
	 * 
	 * @param	eb	 Object van
	 * het type {@link Eigenschappen} dat wordt toegevoegd
	 * @param	f	Het filiaal waartoe de eigenschap behoort
	 * 
	 * @see		Eigenschappenbedrijf
	 * @see		Eigenschappenbedrijfingevuld
	 * */
	@Override
	public void createEigenschapIngevuld(Eigenschappenbedrijf eb, FiliaalGegevens f){
		Eigenschappenbedrijfingevuld temp = new Eigenschappenbedrijfingevuld();
		temp.setEigenschapId(eb.getEigenschapId());
		temp.setNaam(eb.getNaam());
		temp.setFiliaal(f);
		temp.setCategorieId(eb.getCategorieId());
		temp.setEenheid(eb.getEenheid());
		temp.setMenustring(eb.getMenustring());
		em.persist(temp);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Eigenschappenbedrijfingevuld} in de database wijzigt.
	 * 
	 * @param	ebi 	Het object dat moet worden gewijzigd
	 * 
	 * @see		Eigenschappenbedrijfingevuld
	 * */
	@Override
	public void wijzigEigenschap(Eigenschappenbedrijfingevuld ebi){
		em.merge(ebi);
	}
	
	/**
	 * Methode de een bestaand object van het type {@link Eigenschappenbedrijfingevuld} voor een bedrijf verwijderd uit de database.
	 * Eerst worden alle filialen van het bedrijf opgezocht, om dan voor elk van deze filialen de eigenschappen te verwijderen.
	 * 
	 * @param eigenschapId	Het eigenschapId van het object
	 * @param	bedrijfId	Het bedrijfId van het object
	 *
	 * @see		FiliaalGegevens
	 * @see 	Eigenschappenbedrijfingevuld
	 * */
	@Override
	public void verwijderEigenschapBedrijfIngevuld(int eigenschapId, int bedrijfId){
		List<FiliaalGegevens> temp = filiaalEJB.findAllFilialenBedrijf(bedrijfId);
		if(!temp.isEmpty()){
			for(FiliaalGegevens f : temp){
				int filiaalId = f.getFiliaalid();
				Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijfingevuld e WHERE e.eigenschapId =:eigenschapId AND e.filiaal.filiaalid =:filiaalId");
				q.setParameter("eigenschapId", eigenschapId);
				q.setParameter(FILIAALIDQUERYPARAM, filiaalId);
				if(!q.getResultList().isEmpty()){
					Eigenschappenbedrijfingevuld ebi =(Eigenschappenbedrijfingevuld) q.getResultList().get(0);
					em.remove(ebi);
				}
				
				
			}
		}
		
		
	}
	
	/**
	 * Methode die een reeks van het type {@link Eigenschappenbedrijfingevuld} aanmaakt en doorstuurt naar de database.
	 * De Lijst met objecten van het type {@link Eigenschappenbedrijf} wordt doorlopen en er wordt een voor een object
	 * van het type {@link Eigenschappenbedrijfingevuld} aangemaakt, daarna worden de attributen van dit object geset
	 * en word het naar de database gestuurd.
	 * 
	 * @param Eigenschappenlijst	de lijst met eigenschappen die in de database moet.
	 * @param	filiaal				het filiaal waartot deze eigenschappen behoren.
	 *
	 * @see		Eigenschappenbedrijf
	 * @see 	Eigenschappenbedrijfingevuld
	 * */
	@Override
	public void createEigenschapIngevuld(List<Eigenschappenbedrijf> list, FiliaalGegevens filiaal){
		for(Eigenschappenbedrijf eb : list){
			Eigenschappenbedrijfingevuld temp = new Eigenschappenbedrijfingevuld();
			temp.setEigenschapId(eb.getEigenschapId());
			temp.setNaam(eb.getNaam());
			temp.setType(eb.getType());
			temp.setFiliaal(filiaal);
			temp.setCategorieId(eb.getCategorieId());
			temp.setEenheid(eb.getEenheid());
			em.persist(temp);
		}
	}
	
	/**
	 * Deze methode update de inhoud-attribuut van objecten van het type {@link Eigenschappenbedrijfingevuld}.
	 * In een loop wordt elk object in de lijst van eigenschappen doorgelopen.
	 * In deze loop wordt elke eigenschap opgevreaagd uit de database, aangepast en vervolgens gewijzigd in de database
	 * met de functie {@link #wijzigEigenschap()}.
	 * 
	 * @param 	eigenschappen		de lijst met eigenschappen die moet worden aangepast.
	 * @param	filiaal				het filiaal waartot deze eigenschappen behoren.
	 *
	 * @see		Eigenschappenbedrijf}
	 * @see		#wijzigEigenschap()
	 * */

	@Override
	public void mergeEigenschappen(List<Eigenschappenbedrijfingevuld> eigenschappen){
		Eigenschappenbedrijfingevuld upTeDatenEigenschap;
		for(Eigenschappenbedrijfingevuld e : eigenschappen){
			if(e.getInhoud()!=""){
			System.out.println("extra:"+e.getInhoud());
			System.out.println("extra id: "+ e.getEigenschappenbedrijfingevuldId());
			Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijfingevuld e WHERE e.eigenschappenbedrijfingevuldId =:eigenschapId");
			//Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijfingevuld e WHERE e.eigenschapId =:eigenschapId");
		
			System.out.println(e.getInhoud());
			q.setParameter("eigenschapId", e.getEigenschappenbedrijfingevuldId());
			upTeDatenEigenschap = (Eigenschappenbedrijfingevuld) q.getResultList().get(0);
			
			upTeDatenEigenschap.setInhoud(e.getInhoud());
			wijzigEigenschap(upTeDatenEigenschap);
			}
		}
	}
	

}
