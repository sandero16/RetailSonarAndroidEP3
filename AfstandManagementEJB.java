package ejb;


import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Afstand;
import data.User;
import data.Bedrijf;
import data.Customeigenschapingevuld;
import data.FiliaalGegevens;

@Stateless
public class AfstandManagementEJB implements AfstandManagementEJBLocal{
	
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
	
	public Afstand findAfstand(int filiaalId) {
		Query q = em.createQuery("SELECT a FROM Afstand a WHERE a.filiaalId =:filiaalId");
		q.setParameter("filiaalId", filiaalId);
		Afstand afstand=(Afstand)q.getResultList().get(0);
		return afstand ;
	}
	
	public void nieuwFiliaal(Afstand afstand){
		em.persist(afstand);
	}
	public void mergeNieuweAfstand(Afstand afstand){
		Afstand upTeDatenAfstand;
		System.out.println("ejb filiaal id: "+afstand.getfiliaalId());
		Query q = em.createQuery("SELECT a FROM Afstand a WHERE a.filiaalId =:filiaalId");
		q.setParameter("filiaalId", afstand.getfiliaalId());
		upTeDatenAfstand = (Afstand) q.getSingleResult();
		upTeDatenAfstand.setWaarde(afstand.getWaarde());
		em.merge(upTeDatenAfstand);
	}
	public boolean findAanwezigheid(int filiaalid){
		Query q = em.createQuery("SELECT a FROM Afstand a WHERE a.filiaalId =:filiaalId");
		q.setParameter("filiaalId", filiaalid);
		if(q.getResultList().isEmpty()){
			System.out.println("empty");
			return false;
		}
		else{
			return true;
		}
	}



}
