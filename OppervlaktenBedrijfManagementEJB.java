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
import data.OppervlaktenBedrijf;
import data.Bedrijf;
import data.FiliaalGegevens;

@Stateless
public class OppervlaktenBedrijfManagementEJB implements OppervlaktenBedrijfManagementEJBLocal{
	
	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	@EJB
	private GebruikerManagementEJBLocal userEJB;
	
	public void voegOppervlakToe(OppervlaktenBedrijf oppervlaktenbedrijf){
		em.persist(oppervlaktenbedrijf);
	}
	
	public void invoegenGegevens(FiliaalGegevens gebruikerGegevens, String userName) {
		// TODO Auto-generated method stub
		
	}
	public boolean findAanwezigheid(int bedrijfid){
		Query q = em.createQuery("SELECT o FROM OppervlaktenBedrijf o WHERE o.bedrijf.bedrijfId =:bedrijfid");
		q.setParameter("bedrijfid", bedrijfid);
		if(q.getResultList().isEmpty()){
			System.out.println("empty");
			return false;
		}
		else{
			
			return true;
		}
	}
	/*public boolean bevatAfstand(String username){
		User user = userEJB.findPerson(username);
		Query q=em.createQuery("SELECT a FROM AfstandenBedrijf a WHERE a.bedrijf:=bedrijf");
		Bedrijf bedrijf=user.getBedrijf();
		q.setParameter("bedrijf",bedrijf);
		if(q.getSingleResult()!=null){
			System.out.println("er zijn er");
		}
		else{
			System.out.println("er zijn er niet");
		}
		return true;
		
	}*/

}
