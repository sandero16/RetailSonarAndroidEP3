package ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Bedrijf;
import data.Eigenschappen;
import data.Eigenschappenbedrijf;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor bedrijfseigenschappen verwerkt. 
 * */
@Stateless
public class EigenschappenBedrijfManagementEJB implements EigenschappenBedrijfManagementEJBLocal{

	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	private Eigenschappenbedrijf eigBedr = new Eigenschappenbedrijf();
	
	/**
	 * Methode die alle objecten van het type {@link Eigenschappenbedrijf} van een bepaald bedrijf opvraagt uit de database op basis van het bedrijf Id.
	 * 
	 * @param bedrijfId	Het id van het bedrijf
	 * @return List	Lijst van objecten van het type {@link Eigenschappenbedrijf} die voldoen aan de query.
	 * @see		Eigenschappenbedrijf
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Eigenschappenbedrijf> findAllEigenschappenBedrijf(int bedrijfId){
		Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijf e WHERE e.bedrijf.bedrijfId =:bedrijfId");
		q.setParameter("bedrijfId", bedrijfId);
		return q.getResultList();
	}
	
	/**
	 * Methode die een nieuw object van het type {@link Eigenschappenbedrijf} toevoegd aan de database op basis van een bestaand object van het type {@link Eigenschappen} en een bedrijf Id.
	 * Er wordt lokaal een kopie genomen van het object van het type {@link Eigenschappen} en de attributen worden toegekend aan het object van het type {@link Eigenschappenbedrijf}.
	 * 
	 * @param e	Bestaand object van het type {@link Eigenschappen}
	 * @param bedrijf	Het bedrijf waaraan de eigenschap wordt toegekend
	 * 
	 * @see		Eigenschappen
	 * @see 	Eigenschappenbedrijf
	 * */
	@Override
	public void voegEigenschapToe(Eigenschappen e, Bedrijf bedrijf){
		eigBedr.setBedrijf(bedrijf);
		eigBedr.setEigenschapId(e.getEigenschappenId());
		eigBedr.setNaam(e.getNaam());
		eigBedr.setCategorieId(e.getCategorieId());
		eigBedr.setEenheid(e.getEenheid());
		eigBedr.setMenustring(e.getMenustring());
		em.persist(eigBedr);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link Eigenschappenbedrijf} opvraagt uit de database op basis van een eigenschapId en een bedrijfId en dan verwijderd.
	 * 
	 * @param	eigenschapId	het eigenschapId van het object
	 * @param	bedrijfId	Het bedrijfId van het object
	 *
	 * @see	Eigenschappenbedrijf
	 * @see Bedrijf
	 * */
	@Override
	public void verwijderEigenschapBedrijf(int eigenschapId, int bedrijfId){
		Query q = em.createQuery("SELECT e FROM Eigenschappenbedrijf e WHERE e.bedrijf.bedrijfId =:bedrijfid AND e.eigenschapId =:eigenschapId");
		q.setParameter("bedrijfid", bedrijfId);
		q.setParameter("eigenschapId", eigenschapId);
		if(!q.getResultList().isEmpty()){
			Eigenschappenbedrijf eB = (Eigenschappenbedrijf) q.getResultList().get(0);
			em.remove(eB);
		}
		
	}
}
