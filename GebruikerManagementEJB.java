package ejb;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Base64;
import java.util.List; 


import javax.persistence.Query; 
import data.User;

/**
 * Klasse verwerking van requests uit de web en de rest voor gebruikers verwerkt. 
 * */
@Stateless
public class GebruikerManagementEJB implements GebruikerManagementEJBLocal {
	@PersistenceContext(unitName="RetailSonarJPA")
	private EntityManager em;
	
	/**
	 * Methode die een nieuw object van het type {@link User} toevoegd aan de database.
	 * 
	 * @param 	gebruiker	Het object dat wordt toegvoegd aan de database.
	 * 
	 * @see 	User
	 * */
	@Override
	 public void maakGebruiker(User gebruiker){ 
	    em.persist(gebruiker); 
	  }
	
	/**
	 * Methode die een object van het type {@link User} teruuggeeft op basis van een gebruikersnaam.
	 * 
	 * @param naam	De gebruikersnaam van de gebruiker.
	 * @return	User	Object van het type {@link User}.
	 * @see 	User
	 * */
	/*@SuppressWarnings("unchecked")
	@Override
	public User zoekGebruiker(String naam){ 
	    Query q = em.createNamedQuery("Gebruiker.findAll"); 
	    q = q.setParameter("naam", naam); 
	    List<Object> gl = q.getResultList(); 
	    return (User) gl.get(0); 
	   
	}*/
	
	/**
	 * Methode die alle objecten van het type {@link User} uit de database opvraagt en teruggeeft.
	 * 
	 * 
	 * @return	list Lijst met alle objecten van het type {@link User} uit de database.
	 * @see 	User
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllUsers(){
		Query q = em.createQuery("SELECT u FROM User u");
		return q.getResultList();
	}
	
	/**
	 * Methode die alle objecten van het type {@link User} met een bepaald bedrijfId terguggeeft.
	 * 
	 * @param 	bedrijfId	Het id van het gevraagde bedrijf.
	 * @return	List	Lijst van alle objecten die voldoen aan de query.
	 * @see 	User
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllUsersBedrijf(int bedrijfId){
		Query q = em.createQuery("SELECT u FROM User u WHERE u.bedrijfId = :bedrijfid");
		q.setParameter("bedrijfid", bedrijfId);
		return q.getResultList();
	}
	
	/**
	 * Methode die een object van het type {@link User} teruggeeft die voldoet aan de meegegeven paramater
	 * 
	 * @param username	De gerbuikersnaam van de gevraagde gebruiker.
	 * @return	User	Object van het type {@link User}.
	 * @see 	User
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public User findPerson(String username){
		Query q = em.createQuery("SELECT u FROM User u WHERE u.username = :username");
		q.setParameter("username", username);
		List<User> users = q.getResultList();
		if(users.size()==1)
			return users.get(0);
		else return null;
	}
	
	/**
	 * Methode die controlleerd indien de ingegevens login gegevens correct zijn.
	 * 
	 * @param	login	De ingegeven gebruikersnaam.
	 * @param	wachtwoord	Het ingegevens wachtwoord.
	 * @return 	boolean	True of False bepaald door de correctheid van meegegeven parameters.
	 * @see		User
	 * */
	public boolean loginGegevensCorrect(String login, String wachtwoord){
		System.out.println("Username: " + login);
		User gebruiker = findPerson(login);
		System.out.println("gebruiker name: " + gebruiker.getUsername() + " " + gebruiker.getUserId());
		
		return gebruiker.getPassword().equals(hash(wachtwoord));
	}
	
	/**
	 * Methode die een meegegeven String hashed met een SHA-256 encryptie en de String gaat encodeeren naar Base64.
	 * 
	 * @param	wachtwoord	Het te hashen wachtwoord.
	 * @return String het gehashte wachtwoord.
	 * @see	MessageDigest
	 * @see Base64
	 * */
	@Override
	public String hash(String wachtwoord){
		MessageDigest hasher;
		String base="";
		try {
			hasher = MessageDigest.getInstance("SHA-256");
			byte[] hPassword = hasher.digest(wachtwoord.getBytes(StandardCharsets.UTF_8));
			base = Base64.getEncoder().encodeToString(hPassword);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return base;
	}
	
	/**
	 * Methode die een bestaand object van het type {@link User} opvraagt uit de database en wijzigt.
	 * 
	 * @param loggedInUser	Object van het type {@link User} dat gewijzigd moet worden.
	 * 
	 * @see 	User
	 * */
	@Override
	public void changeGebruiker(User loggedInUser){
		em.merge(loggedInUser);
	}
	
	/**
	 * Methode die alle objecten van het type {@link User} teruggeeft die voldoen aan de meegegven parameters.
	 * 
	 * @param 	bedrijfId	Het id van het bedrijf waarvan de objecten worden opgevraagd.
	 * @param 	groupId		het id van de group waartoe de objecten behoren.
	 * @return 	List	Lijst van objecten van het type {@link User} die voldoen aan de query.
	 * @see User
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllRegioManagersBedrijf(int bedrijfId, int groupId){
		Query q = em.createQuery("SELECT u FROM User u WHERE u.bedrijfId = :bedrijfid AND u.groupId = :groupid");
		q.setParameter("bedrijfid",bedrijfId);
		q.setParameter("groupid", groupId);
		return q.getResultList();
	}
	
	/**
	 * Methode die een object van het type {@link User} uit de database opvraagt op basis van het userId
	 * 
	 * @param userId	Het id van het gevraagde object.
	 * @return	User	Object van het type {@link User}.
	 * @see	User
	 * */
	@Override
	public User findPerson(int userId){
		Query q = em.createQuery("SELECT u FROM User u WHERE u.userId = :userid");
		q.setParameter("userid",userId);
		return (User) q.getResultList().get(0);
	}
	
	/**
	 * Methode die een bestaand object van het type {@link User} opvraagt uit de database op basis van het userId en verwijderd.
	 * 
	 * @param userId	Het id van het te verwijderen object.
	 *
	 * @see User
	 * */
	@Override
	public void verwijderGebruiker(int userId){
		User u = em.find(User.class, userId);
		em.remove(u);
	}
	
	/**
	 * Methode die alle admins uit de database teruggeeft.
	 * 
	 * @return List Lijst van objecten van het type {@link User}.
	 * @see User
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllAdmins(){
		Query q = em.createQuery("SELECT u FROM User u WHERE u.groupId = 2 AND u.bedrijfId > -1");
		return q.getResultList();
	}
}
