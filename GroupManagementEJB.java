package ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import data.Group;

/**
 * Klasse die de verwerking van requests uit de web en de rest voor groups verwerkt. 
 * */
@Stateless
public class GroupManagementEJB implements GroupManagementEJBLocal{
	
	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die een object van het type {@link Group} teruggeeft op basis van het id.
	 * 
	 * @param	id	Het id van het opgevraagde object
	 * @return	Group	Object van het type {@link Group} dat voldoet aan de query
	 * @see 	Group
	 * */
	@Override
	public Group findGroup(int id){
		Query q = em.createQuery("SELECT g FROM Group g WHERE g.groupId=:id");
		q.setParameter("id", id);
		return (Group)q.getSingleResult();
	}
}
