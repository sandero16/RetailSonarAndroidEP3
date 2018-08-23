package rest;

import java.security.Key;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;

import data.Bedrijf;
import data.Customeigenschap;
import data.Customeigenschapingevuld;
import data.Eigenschappenbedrijf;
import data.Eigenschappenbedrijfingevuld;
import data.FiliaalGegevens;
import ejb.BedrijfManagementEJBLocal;
import ejb.CustomEigenschapIngevuldManagementEJBLocal;
import ejb.CustomEigenschapManagementEJBLocal;
import ejb.EigenschappenBedrijfIngevuldManagementEJBLocal;
import ejb.EigenschappenBedrijfManagementEJBLocal;
import ejb.EigenschappenManagementEJBLocal;
import ejb.FiliaalManagementEJBLocal;
import ejb.GebruikerManagementEJBLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import rest.androidObjecten.AndroidCustomEigenschapIngevuld;
import rest.androidObjecten.AndroidEigenschappenIngevuld;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;


@Path("/eigenschappen")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RequestScoped
public class EigenschappenRestService {

	@EJB
	private CustomEigenschapManagementEJBLocal customEigenschapEJB;
	
	@EJB
	private CustomEigenschapIngevuldManagementEJBLocal customEigenschapIngevuldEJB;
	
	@EJB
	private BedrijfManagementEJBLocal bedrijfEJB;
	
	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;
	
	@EJB
	private EigenschappenManagementEJBLocal eigenschapEJB;
	
	@EJB 
	private EigenschappenBedrijfManagementEJBLocal eigenschapBedrijfEJB;
	
	@EJB
	private EigenschappenBedrijfIngevuldManagementEJBLocal eigenschapBedrijfIngevuldEJB;
	
	@EJB
	private GebruikerManagementEJBLocal userEJB;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext ctx;

	@POST
	@JWTTokenNeeded
	@Path("/maak/")
	public Response maakEigenschap(Bedrijf bedrijf) {
		return null;
	}
	
	@GET
	@JWTTokenNeeded
	@Path("/get/")
	public Response getEigenschappen(@HeaderParam("Authorization") String ontvangenToken) {
		int id=0;
		//JWT-Token ontcijferen
		Jws<Claims> claims = null;
    	ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
    	try {
            // Validate the token
            Key key = ApplicationConfig.JWT_KEY;
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
            id = Character.getNumericValue(claims.getBody().getId().toCharArray()[0]);

        } catch (Exception e) {
        	e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    	
    	List<Eigenschappenbedrijfingevuld> eigenschappenIngevuldLijst = findAllEigenschappenFiliaal(id);
    	List<Customeigenschapingevuld> customEigenschappenIngevuld = findAllCustomEigenschappenFiliaal(id);
    	
    	List<AndroidEigenschappenIngevuld> androidEigenschappenIngevuldLijst = new LinkedList<>();
    	List<AndroidCustomEigenschapIngevuld> androidcustomEigenschappenIngevuld = new LinkedList<>();
    	
    	for(Eigenschappenbedrijfingevuld e : eigenschappenIngevuldLijst){
    		androidEigenschappenIngevuldLijst.add(new AndroidEigenschappenIngevuld(e));
    	}
    	for(Customeigenschapingevuld e : customEigenschappenIngevuld){
    		androidcustomEigenschappenIngevuld.add(new AndroidCustomEigenschapIngevuld(e));
    	}
    	
    	Map<String,Object> teVerzendenClaims = new HashMap<>();
    	
    	teVerzendenClaims.put("eigenschappenIngevuld", androidEigenschappenIngevuldLijst);
    	teVerzendenClaims.put("customEigenschappenIngevuld", androidcustomEigenschappenIngevuld);
		
		//JWT-token opstellen
		Date curDate = new Date();
    	Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);
        
        Key key = ApplicationConfig.JWT_KEY;
        String jwtToken = Jwts.builder()
                .setIssuer("http://localhost:8080/RetailSonarRest/restService/")
                .setIssuedAt(curDate)
                .setClaims(teVerzendenClaims)
                .setExpiration(cal.getTime())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

     
		return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer ").build();
	}
	
	@POST
	@JWTTokenNeeded
	@Path("/set/")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response postEigenschappen(String ontvangenToken) {
		//JWT-Token ontcijferen
    	try {
            // Validate the token
        } catch (Exception e) {
        	e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    	
    	
		Date curDate = new Date();
    	Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);
        
        Key key = ApplicationConfig.JWT_KEY;
        String jwtToken = Jwts.builder()
                .setIssuer("http://localhost:8080/RetailSonarRest/restService/")
                .setIssuedAt(curDate)
                .setExpiration(cal.getTime())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

     
		return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer ").build();
	}
	
	private List<Eigenschappenbedrijfingevuld> findAllEigenschappenFiliaal(int filiaalId){
		List<Eigenschappenbedrijfingevuld> eigenschappenLijst;
		FiliaalGegevens filiaal = filiaalEJB.findProject(filiaalId);
		List<Eigenschappenbedrijf> tempEig = eigenschapBedrijfEJB.findAllEigenschappenBedrijf(filiaal.getBedrijf().getBedrijfId());
		List<Eigenschappenbedrijfingevuld> tempEigIn = eigenschapBedrijfIngevuldEJB.findAllEigenschappen(filiaalId);
		List<Eigenschappenbedrijf> toRemove = new ArrayList<>();
		for(Eigenschappenbedrijf eb : tempEig){
			for(Eigenschappenbedrijfingevuld ebi : tempEigIn){
				if(ebi.getEigenschapId() == eb.getEigenschapId()){
					toRemove.add(eb);
				}
			}
		}
		tempEig.removeAll(toRemove);
		//eigenschapBedrijfIngevuldEJB.createEigenschapIngevuld(tempEig,filiaal);
		eigenschappenLijst = eigenschapBedrijfIngevuldEJB.findAllEigenschappen(filiaalId);
		Collections.sort(eigenschappenLijst, (a, b) -> a.getCategorieId() < b.getCategorieId() ? -1 : a.getCategorieId() == b.getCategorieId() ? 0 : 1);
		return eigenschappenLijst;
	}
	
	private List<Customeigenschapingevuld> findAllCustomEigenschappenFiliaal(int filiaalId){
		List<Customeigenschapingevuld> customLijst;
		FiliaalGegevens filiaal = filiaalEJB.findProject(filiaalId);
		List<Customeigenschap> tempEig = customEigenschapEJB.findAllCustomEigenschappenBedrijf(filiaal.getBedrijf().getBedrijfId());
		List<Customeigenschapingevuld> tempEigIn  = customEigenschapIngevuldEJB.findAllEigenschappen(filiaalId);
		List<Customeigenschap> toRemove = new ArrayList<>();
		
		for(Customeigenschap c : tempEig){
			for(Customeigenschapingevuld cei : tempEigIn){
				if(cei.getCustomeigenschapId() == c.getCustomEigenschap_id()){
					toRemove.add(c);
				}
			}
			
		}
		tempEig.removeAll(toRemove);
		//customEigenschapIngevuldEJB.createCustomEigenschapIngevuld(tempEig,filiaal);
		customLijst = customEigenschapIngevuldEJB.findAllEigenschappen(filiaalId);
		return customLijst;
	}
}
