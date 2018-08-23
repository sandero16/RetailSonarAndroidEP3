package ejb;


import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Oppervlakte;
import data.User;
import data.Bedrijf;
import data.Customeigenschapingevuld;
import data.FiliaalGegevens;

@Stateless
public class OppervlakteManagementEJB implements OppervlakteManagementEJBLocal{
	
	@PersistenceContext(unitName = "RetailSonarJPA")
	private EntityManager em;

	@Override
	public void invoegenGegevens(FiliaalGegevens gebruikerGegevens, String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FiliaalGegevens> findAllFilialenBedrijf(int bedrijfId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FiliaalGegevens findFiliaalGegevens(int filiaalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void wijzigFiliaal(FiliaalGegevens filiaalGegevens) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FiliaalGegevens> findAllFilialenRegioManager(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FiliaalGegevens findProject(int filiaalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void verwijderFiliaal(int filiaalId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> findAlleFilialen(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findAddressenVanAlleFilialen(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findIdVanAlleFilialen(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Oppervlakte findoppervlakte(int filiaalId) {
		Query q = em.createQuery("SELECT o FROM Oppervlakte o WHERE o.filiaalId =:filiaalId");
		q.setParameter("filiaalId", filiaalId);
		Oppervlakte oppervlak=(Oppervlakte)q.getResultList().get(0);
		return oppervlak ;
	}
	
	public void nieuwFiliaal(Oppervlakte oppervlak){
		em.persist(oppervlak);
	}
	public void mergeNieuweOppervlakte(Oppervlakte oppervlak){
		Oppervlakte upTeDatenOppervlak;
		System.out.println("ejb filiaal id: "+oppervlak.getfiliaalId());
		Query q = em.createQuery("SELECT o FROM Oppervlakte o WHERE o.filiaalId =:filiaalId");
		q.setParameter("filiaalId", oppervlak.getfiliaalId());
		upTeDatenOppervlak = (Oppervlakte) q.getSingleResult();
		upTeDatenOppervlak.setWaarde(oppervlak.getWaarde());
		em.merge(upTeDatenOppervlak);
	}



}
