package rest;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import data.Afbeeldingen;
import data.User;
import ejb.GebruikerManagementEJBLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("/authentication")
@RequestScoped
public class AuthenticationService {
	
	@EJB
	private GebruikerManagementEJBLocal userEJB;
	
	/**
	 * Deze methode implementeerd de user-authenticatie van de rest service.
	 * 
	 * @param 	ontvangenToken	 	het Json web token dat in de header van het ontvangen http-bericht zit.
	 * */
    @GET
    @JWTTokenNeeded
    @Path("/login/")
    public Response authenticateUser(@HeaderParam("Authorization") String ontvangenToken) {
    	String login = "";
    	String wachtwoord = "";
    	
    	//JWT-Token ontcijferen
    	ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
    	try {
            // Validate the token
            Key key = ApplicationConfig.JWT_KEY;
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
            login = (String) claims.getBody().get("username");
            wachtwoord = (String) claims.getBody().get("wachtwoord");

        } catch (Exception e) {
        	e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    	
    	try {
        	User user = null;
            if(userEJB.loginGegevensCorrect(login, wachtwoord)){
            	user = userEJB.findPerson(login);
            }           
            String token = issueToken(user);
            return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
           
        } catch (Exception e) {
        	e.printStackTrace();
            return Response.status(UNAUTHORIZED).build();
        }
    }

    /**
	 * Deze maakt een token voor de gebruiker die meegegeven wordt.
	 * 
	 * @param 	user	 	De gebruiker waarvoor het token moet worden gemaakt
	 * */
    private String issueToken(User user) {
    	Date curDate = new Date();
    	Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);
        
        Key key = ApplicationConfig.JWT_KEY;
        
        return  Jwts.builder()
                .setId((user.getUserId()+""))
                .setSubject(user.getUsername())
                .setIssuer("http://localhost:8080/RetailSonarRest/restService/")
                .setIssuedAt(curDate)
                .setExpiration(cal.getTime())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }
}
