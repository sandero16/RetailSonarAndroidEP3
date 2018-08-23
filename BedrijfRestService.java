package rest;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.BadRequestException;
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

import data.User;
import data.Afstand;
import data.Bedrijf;
import data.Customeigenschap;
import data.Customeigenschapingevuld;
import data.Eigenschappenbedrijf;
import data.Eigenschappenbedrijfingevuld;
import data.FiliaalGegevens;
import data.Oppervlakte;
import data.Taak;
import ejb.AfstandManagementEJBLocal;
import ejb.BedrijfManagementEJBLocal;
import ejb.CategorieManagementEJBLocal;
import ejb.CustomEigenschapIngevuldManagementEJBLocal;
import ejb.CustomEigenschapManagementEJBLocal;
import ejb.EigenschappenBedrijfIngevuldManagementEJBLocal;
import ejb.EigenschappenBedrijfManagementEJBLocal;
import ejb.EigenschappenManagementEJBLocal;
import ejb.FiliaalManagementEJBLocal;
import ejb.GebruikerManagementEJBLocal;
import ejb.OppervlakteManagementEJBLocal;
import ejb.TakenManagementEJBLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import rest.androidObjecten.AndroidCustomEigenschapIngevuld;
import rest.androidObjecten.AndroidEigenschappenIngevuld;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

//@javax.enterprise.context.RequestScoped
@Path("/bedrijf")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
@RequestScoped
public class BedrijfRestService {

	@EJB
	private BedrijfManagementEJBLocal bedrijfEJB;

	@EJB
	private FiliaalManagementEJBLocal filiaalEJB;

	@EJB
	private GebruikerManagementEJBLocal userEJB;

	@EJB
	private CategorieManagementEJBLocal categorieEJB;

	@EJB
	private CustomEigenschapManagementEJBLocal customEigenschapEJB;

	@EJB
	private CustomEigenschapIngevuldManagementEJBLocal customEigenschapIngevuldEJB;

	@EJB
	private EigenschappenManagementEJBLocal eigenschapEJB;

	@EJB
	private EigenschappenBedrijfManagementEJBLocal eigenschapBedrijfEJB;

	@EJB
	private EigenschappenBedrijfIngevuldManagementEJBLocal eigenschapBedrijfIngevuldEJB;
	
	@EJB
	private AfstandManagementEJBLocal afstandEJB;
	
	@EJB
	private OppervlakteManagementEJBLocal oppervlakteEJB;

	@EJB
	private TakenManagementEJBLocal taakEJB;
	
	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext ctx;

	/**
	 * Methode die een bedrijf aannmaakt uit een http-request
	 * 
	 * @param bedrijf
	 *            Het bedrijf dat moet worden aangemaakt
	 */
	@POST
	@JWTTokenNeeded
	@Path("/maak/")
	public Response maakBedrijf(Bedrijf bedrijf) {
		if (bedrijf == null)
			throw new BadRequestException();

		bedrijfEJB.maakBedrijf(bedrijf);
		URI bedrijfUri = uriInfo.getAbsolutePathBuilder().path((bedrijf.getBedrijfId() + "").toString()).build();
		return Response.created(bedrijfUri).build();
	}

	/**
	 * Deze methode maakt een jwt token aan met informatie over een bedrijf in.
	 * De headerparam "ontvangenToken" bevat de username van de gebruiker die de
	 * informatie opvraagt. De methode geeft een respone terug met daarin een
	 * jwt-token die de filiaalnamen, addressen en id's in de database bevat van
	 * de filialen di gelinkt zijn aan de gebruiker in kwestie.
	 * 
	 * @param ontvangenToken
	 *            het Json web token dat in de header van het ontvangen
	 *            http-bericht zit.
	 */
	@JWTTokenNeeded
	@GET
	@Path("/get/")
	public Response getBedrijf(@HeaderParam("Authorization") String ontvangenToken) {
		String username = "";
		// JWT-Token ontcijferen
		ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
		try {
			// Validate the token
			Key key = ApplicationConfig.JWT_KEY;
			Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
			username = (String) claims.getBody().get("username");

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if (username.equals(""))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		User user = userEJB.findPerson(username);

		// JWT-token opstellen
		Date curDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.DATE, 2);

		Key key = ApplicationConfig.JWT_KEY;
		List<String> filiaalNamen = filiaalEJB.findAlleFilialen(username);
		List<String> addressen = filiaalEJB.findAddressenVanAlleFilialen(username);
		List<String> ids = filiaalEJB.findIdVanAlleFilialen(username);

		Map<String, Object> claims = new HashMap<>();
		int i = 0;
		// We steken elk filiaal in de claims met tag "Filiaal" met een nummer
		// erachter
		for (String s : filiaalNamen) {
			i++;
			claims.put("Filiaal" + i, s);
		}
		i = 0;
		// Zelfde voor de addressen
		for (String s : addressen) {
			i++;
			claims.put("Addres" + i, s);
		}
		i = 0;
		// Zelfde voor de id's
		for (String s : ids) {
			i++;
			claims.put("Id" + i, s);
		}

		String jwtToken = Jwts.builder().setId((user.getUserId() + "")).setSubject(user.getUsername()).setClaims(claims)
				.setIssuer("http://localhost:8080/RetailSonarRest/restService/").setIssuedAt(curDate)
				.setExpiration(cal.getTime()).signWith(SignatureAlgorithm.HS512, key).compact();

		return Response.ok().header(AUTHORIZATION, "Bearer " + jwtToken).build();
	}

	/**
	 * Deze methode maakt een jwt token aan met de eigenschappen van een bepaald
	 * filiaal. De headerparam "ontvangenToken" bevat de username van de
	 * gebruiker die de informatie opvraagt en de id van het filiaal waarvoor de
	 * eigenschappen nodig zijn. De methode geeft een respone terug met daarin
	 * een jwt-token met daarin de vaste en custom eigenschappen van het filiaal
	 * in kwestie alsnog de taken die met dat filiaal gelinkt zijn.
	 * 
	 * @param ontvangenToken
	 *            het Json web token dat in de header van het ontvangen
	 *            http-bericht zit.
	 */
	@GET
	@JWTTokenNeeded
	@Path("/eigenschappen/get/")
	public Response getEigenschappen(@HeaderParam("Authorization") String ontvangenToken) {
		int id = 0;
		// JWT-Token ontcijferen
		Jws<Claims> claims = null;
		ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
		try {
			// Validate the token
			Key key = ApplicationConfig.JWT_KEY;
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
			id = Integer.parseInt(claims.getBody().getId());
			id++;

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		System.out.println("id: " + id + "===============================");
		List<Eigenschappenbedrijfingevuld> eigenschappenIngevuldLijst = findAllEigenschappenFiliaal(id);
		List<Customeigenschapingevuld> customEigenschappenIngevuld = findAllCustomEigenschappenFiliaal(id);
		List<Taak> taken = taakEJB.findAllTakenFiliaal(id);

		List<AndroidEigenschappenIngevuld> androidEigenschappenIngevuldLijst = new LinkedList<>();
		List<AndroidCustomEigenschapIngevuld> androidcustomEigenschappenIngevuld = new LinkedList<>();
		List<String> takenLijst = new LinkedList<>();

		for (Taak t : taken) {
			takenLijst.add(t.getBeschrijving());
		}

		for (Eigenschappenbedrijfingevuld e : eigenschappenIngevuldLijst) {
			System.out.println("ne keer kijken: "+e.getEigenschappenbedrijfingevuldId());
			androidEigenschappenIngevuldLijst.add(new AndroidEigenschappenIngevuld(e));
		}
		for (Customeigenschapingevuld e : customEigenschappenIngevuld) {
			System.out.println("ne keer kijken custom: "+e.getCustomEigenschapIngevuld_id());
			androidcustomEigenschappenIngevuld.add(new AndroidCustomEigenschapIngevuld(e));
		}

		Map<String, Object> teVerzendenClaims = new HashMap<>();

		teVerzendenClaims.put("eigenschappenIngevuld", androidEigenschappenIngevuldLijst);
		teVerzendenClaims.put("customEigenschappenIngevuld", androidcustomEigenschappenIngevuld);
		teVerzendenClaims.put("taken", takenLijst);

		// JWT-token opstellen
		Date curDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.DATE, 2);

		Key key = ApplicationConfig.JWT_KEY;
		String jwtToken = Jwts.builder().setIssuer("http://localhost:8080/RetailSonarRest/restService/")
				.setIssuedAt(curDate).setClaims(teVerzendenClaims).setExpiration(cal.getTime())
				.signWith(SignatureAlgorithm.HS512, key).compact();

		return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer ").build();
	}

	/**
	 * Deze methode ontvangt een jwt token in de header voor authorisatie en een
	 * in de body. het token in de body bevat de inhou van bepaalde
	 * eigenschappen, deze eigenschappen moeten worden bijgewerkt, dit gebeurt
	 * met de functie mergeEigenschappen.
	 * 
	 * @param ontvangenToken
	 *            het Json web token dat in de header van het ontvangen
	 *            http-bericht zit.
	 */
	@POST
	@JWTTokenNeeded
	@Path("/eigenschappen/set/")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response postEigenschappen(String jsonInput, @HeaderParam("Authorization") String ontvangenToken) {
		// JWT-Token ontcijferen
		Jws<Claims> claims = null;
		Jws<Claims> claimsBody = null;

		jsonInput = jsonInput.substring("Bearer".length()).trim();
		ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
		try {
			// Validate the token
			Key key = ApplicationConfig.JWT_KEY;
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
			claimsBody = Jwts.parser().setSigningKey(key).parseClaimsJws(jsonInput);

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		if(claimsBody.getBody().get("eigenschappenIngevuld")!=null){
			System.out.println("gelukt");
		}
		else{
			System.out.println("niet");
		}

		List<LinkedHashMap<String, Object>> customEigenschappen = (List<LinkedHashMap<String, Object>>) claimsBody
				.getBody().get("customEigenschappenIngevuld");
		List<LinkedHashMap<String, Object>> eigenschappen = (List<LinkedHashMap<String, Object>>) claimsBody.getBody()
				.get("eigenschappenIngevuld");

		List<Eigenschappenbedrijfingevuld> eigenschappenIngevuldLijst = hashMapNaarEigenschapBedrijf(eigenschappen);
		List<Customeigenschapingevuld> customEigenschappenIngevuld = hashMapNaarCustomEigenschap(customEigenschappen);
		List<Taak> taken = null;

		if (claimsBody.getBody().get("ingevuld").equals("y"))
			taken = taakEJB.findAllTakenFiliaal(Integer.parseInt(claims.getBody().getId()));
		taakEJB.setIngevuld(taken);
		customEigenschapIngevuldEJB.mergeEigenschappen(customEigenschappenIngevuld);
		eigenschapBedrijfIngevuldEJB.mergeEigenschappen(eigenschappenIngevuldLijst);
		

		Date curDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.DATE, 2);
		

		Key key = ApplicationConfig.JWT_KEY;
		String jwtToken = Jwts.builder().setIssuer("http://localhost:8080/RetailSonarRest/restService/")
				.setIssuedAt(curDate).setExpiration(cal.getTime()).signWith(SignatureAlgorithm.HS512, key).compact();

		return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer ").build();
	}

	/**
	 * Deze methode returnt een lijst met objecten van het type
	 * {@link Eigenschappenbedrijfingevuld} die bij het meegegeven filiaalId
	 * hoort
	 * 
	 * @param filiaalId
	 *            de id van het filiaal in kwestie
	 * @return een lijst met objecten van het type
	 *         {@link Eigenschappenbedrijfingevuld}
	 * 
	 * @see Eigenschappenbedrijfingevuld
	 */
	private List<Eigenschappenbedrijfingevuld> findAllEigenschappenFiliaal(int filiaalId) {
		List<Eigenschappenbedrijfingevuld> eigenschappenLijst;
		FiliaalGegevens filiaal = filiaalEJB.findProject(filiaalId);
		List<Eigenschappenbedrijf> tempEig = eigenschapBedrijfEJB
				.findAllEigenschappenBedrijf(filiaal.getBedrijf().getBedrijfId());
		List<Eigenschappenbedrijfingevuld> tempEigIn = eigenschapBedrijfIngevuldEJB.findAllEigenschappen(filiaalId);
		List<Eigenschappenbedrijf> toRemove = new ArrayList<>();
		for (Eigenschappenbedrijf eb : tempEig) {
			for (Eigenschappenbedrijfingevuld ebi : tempEigIn) {
				if (ebi.getEigenschapId() == eb.getEigenschapId()) {
					toRemove.add(eb);
				}
			}
		}
		tempEig.removeAll(toRemove);
		// eigenschapBedrijfIngevuldEJB.createEigenschapIngevuld(tempEig,filiaal);
		eigenschappenLijst = eigenschapBedrijfIngevuldEJB.findAllEigenschappen(filiaalId);

		for (Eigenschappenbedrijfingevuld e : eigenschappenLijst) {
			System.out.println(e.getNaam());
		}

		for (Eigenschappenbedrijfingevuld e : eigenschappenLijst) {
			e.setType(categorieEJB.zoekCategorie(e.getCategorieId()).getNaam());
		}

		Collections.sort(eigenschappenLijst, (a, b) -> a.getCategorieId() < b.getCategorieId() ? -1
				: a.getCategorieId() == b.getCategorieId() ? 0 : 1);
		return eigenschappenLijst;
	}

	/**
	 * Deze methode returnt een lijst met objecten van het type
	 * {@link Customeigenschapingevuld} die bij het meegegeven filiaalId hoort
	 * 
	 * @param filiaalId
	 *            de id van het filiaal in kwestie
	 * @return een lijst met objecten van het type
	 *         {@link Customeigenschapingevuld}
	 * 
	 * @see Customeigenschapingevuld
	 */
	private List<Customeigenschapingevuld> findAllCustomEigenschappenFiliaal(int filiaalId) {
		List<Customeigenschapingevuld> customLijst;
		FiliaalGegevens filiaal = filiaalEJB.findProject(filiaalId);
		List<Customeigenschap> tempEig = customEigenschapEJB
				.findAllCustomEigenschappenBedrijf(filiaal.getBedrijf().getBedrijfId());
		List<Customeigenschapingevuld> tempEigIn = customEigenschapIngevuldEJB.findAllEigenschappen(filiaalId);
		List<Customeigenschap> toRemove = new ArrayList<>();

		for (Customeigenschap c : tempEig) {
			for (Customeigenschapingevuld cei : tempEigIn) {
				if (cei.getCustomeigenschapId() == c.getCustomEigenschap_id()) {
					toRemove.add(c);
				}
			}

		}
		tempEig.removeAll(toRemove);
		// customEigenschapIngevuldEJB.createCustomEigenschapIngevuld(tempEig,filiaal);
		customLijst = customEigenschapIngevuldEJB.findAllEigenschappen(filiaalId);

		if (!(customLijst == null || customLijst.isEmpty())) {
			for (Customeigenschapingevuld e : customLijst) {
				if (e.getCategorieId() != 0)
					e.setType(categorieEJB.zoekCategorie(e.getCategorieId()).getNaam());
			}
		}
		return customLijst;
	}

	/**
	 * Deze methode zet een hashmap uit een json web token om naar een lijst met
	 * objecten van het type {@link Customeigenschapingevuld}
	 * 
	 * @param hashMapList
	 *            de hashmap uit het web token
	 * @return een lijst met objecten van het type
	 *         {@link Customeigenschapingevuld}
	 * 
	 * @see Customeigenschapingevuld
	 */
	private List<Customeigenschapingevuld> hashMapNaarCustomEigenschap(
			List<LinkedHashMap<String, Object>> hashMapList) {
		if (hashMapList.isEmpty())
			return new LinkedList<>();

		List<Customeigenschapingevuld> result = new LinkedList<>();
		Customeigenschapingevuld e;

		int customEigenscahpIngevuld_id;
		int categorieId;
		int filiaalId;
		String inhoud;
		String eenheid;
		String naam;
		String type;

		for (LinkedHashMap<String, Object> hashMap : hashMapList) {
			customEigenscahpIngevuld_id = (int) hashMap.get("customEigenscahpIngevuld_id");
			categorieId = (int) hashMap.get("categorieId");
			filiaalId = (int) hashMap.get("filiaalId");
			inhoud = (String) hashMap.get("inhoud");
			eenheid = (String) hashMap.get("eenheid");
			naam = (String) hashMap.get("naam");
			type = (String) hashMap.get("type");

			e = new Customeigenschapingevuld(customEigenscahpIngevuld_id, categorieId,
					filiaalEJB.findFiliaalGegevens(filiaalId), inhoud, eenheid, naam, type);

			result.add(e);
		}
		return result;
	}

	/**
	 * Deze methode zet een hashmap uit een json web token om naar een lijst met
	 * objecten van het type {@link Eigenschappenbedrijfingevuld}
	 * 
	 * @param hashMapList
	 *            de hashmap uit het web token
	 * @return een lijst met objecten van het type
	 *         {@link Eigenschappenbedrijfingevuld}
	 * 
	 * @see Eigenschappenbedrijfingevuld
	 */
	private List<Eigenschappenbedrijfingevuld> hashMapNaarEigenschapBedrijf(
			List<LinkedHashMap<String, Object>> hashMapList) {
		if (hashMapList == null || hashMapList.isEmpty())
			return new LinkedList<>();

		List<Eigenschappenbedrijfingevuld> result = new LinkedList<>();
		Eigenschappenbedrijfingevuld e;

		int eigenschappenBedrijfIngevuldId;
		int filiaalId;
		int categorieId;
		String inhoud;
		String eenheid;
		String naam;
		String type;

		for (LinkedHashMap<String, Object> hashMap : hashMapList) {
			eigenschappenBedrijfIngevuldId = (int) hashMap.get("eigenschappenBedrijfIngevuldId");
			filiaalId = (int) hashMap.get("filiaalId");
			categorieId = (int) hashMap.get("categorieId");
			inhoud = (String) hashMap.get("inhoud");
			eenheid = (String) hashMap.get("eenheid");
			naam = (String) hashMap.get("naam");
			type = (String) hashMap.get("type");

			e = new Eigenschappenbedrijfingevuld(eigenschappenBedrijfIngevuldId,
					filiaalEJB.findFiliaalGegevens(filiaalId), categorieId, inhoud, eenheid, naam, type);

			result.add(e);
		}
		return result;
	}
	public Afstand hashMapNaarAfstand(LinkedHashMap<String,Object> hashMapList){
		double waarde;
		int filiaalid;
		Set<String>set=hashMapList.keySet();
		Iterator itr=set.iterator();
		System.out.println(set.size());
		while(itr.hasNext()){
			System.out.println(itr.next());
		}
		
		waarde= (double)hashMapList.get("afstand");
		System.out.println("afstand is: "+waarde);
		//System.out.println("filiaalid is:"+filiaalid);
		Afstand a=new Afstand();
		return a;
	}
	
	@POST
	@JWTTokenNeeded
	@Path("/afstandx/set/")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response postAfstand(String jsonInput, @HeaderParam("Authorization") String ontvangenToken) {
		// JWT-Token ontcijferen
		Jws<Claims> claims = null;
		Jws<Claims> claimsBody = null;

		jsonInput = jsonInput.substring("Bearer".length()).trim();
		ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
		try {
			// Validate the token
			Key key = ApplicationConfig.JWT_KEY;
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
			claimsBody = Jwts.parser().setSigningKey(key).parseClaimsJws(jsonInput);

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		//LinkedHashMap<String, Object> afstandenLijst = (LinkedHashMap<String, Object>) claimsBody
		//		.getBody().get("afstands");
		double d=(double)claimsBody.getBody().get("afstands");
		System.out.println("afstand"+d);
		
		int i=(int)claimsBody.getBody().get("filiaalid");
		System.out.println("filiaal"+i);
		
		Afstand nieuwafstand=new Afstand(i,(int)d);
		nieuwafstand.getfiliaalId();
		afstandEJB.mergeNieuweAfstand(nieuwafstand);
		
		//LinkedHashMap<String, Object> filiaalIdLijst = (LinkedHashMap<String, Object>) claimsBody
			//	.getBody().get("filiaalid");
		
		/*
		List<LinkedHashMap<String, Object>> eigenschappen = (List<LinkedHashMap<String, Object>>) claimsBody.getBody()
				.get("eigenschappenIngevuld");*/
		
		//Afstand ontvangen=hashMapNaarAfstand(afstandenLijst);
		//Afstand ontvangen2=hashMapNaarAfstand(filiaalIdLijst);

		//List<Eigenschappenbedrijfingevuld> eigenschappenIngevuldLijst = hashMapNaarEigenschapBedrijf(eigenschappen);
		//List<Customeigenschapingevuld> customEigenschappenIngevuld = hashMapNaarCustomEigenschap(customEigenschappen);
		//List<Taak> taken = null;

		/*if (claimsBody.getBody().get("ingevuld").equals("y"))
			taken = taakEJB.findAllTakenFiliaal(Integer.parseInt(claims.getBody().getId()));
		taakEJB.setIngevuld(taken);
		customEigenschapIngevuldEJB.mergeEigenschappen(customEigenschappenIngevuld);
		eigenschapBedrijfIngevuldEJB.mergeEigenschappen(eigenschappenIngevuldLijst);*/
		

		Date curDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.DATE, 2);

		Key key = ApplicationConfig.JWT_KEY;
		String jwtToken = Jwts.builder().setIssuer("http://localhost:8080/RetailSonarRest/restService/")
				.setIssuedAt(curDate).setExpiration(cal.getTime()).signWith(SignatureAlgorithm.HS512, key).compact();

		return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer ").build();
	}
	
	@POST
	@JWTTokenNeeded
	@Path("/oppervlakte/set/")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response postOppervlakte(String jsonInput, @HeaderParam("Authorization") String ontvangenToken) {
		// JWT-Token ontcijferen
		Jws<Claims> claims = null;
		Jws<Claims> claimsBody = null;

		jsonInput = jsonInput.substring("Bearer".length()).trim();
		ontvangenToken = ontvangenToken.substring("Bearer".length()).trim();
		try {
			// Validate the token
			Key key = ApplicationConfig.JWT_KEY;
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(ontvangenToken);
			claimsBody = Jwts.parser().setSigningKey(key).parseClaimsJws(jsonInput);

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		
		//LinkedHashMap<String, Object> afstandenLijst = (LinkedHashMap<String, Object>) claimsBody
		//		.getBody().get("afstands");
		double waarde=(double)claimsBody.getBody().get("oppervlak");
		
		int i=(int)claimsBody.getBody().get("filiaalid");
		
		Oppervlakte nieuwoppervlak=new Oppervlakte(i,(int)waarde);
		nieuwoppervlak.getfiliaalId();
		oppervlakteEJB.mergeNieuweOppervlakte(nieuwoppervlak);
		
		
		
		//LinkedHashMap<String, Object> filiaalIdLijst = (LinkedHashMap<String, Object>) claimsBody
			//	.getBody().get("filiaalid");
		
		/*
		List<LinkedHashMap<String, Object>> eigenschappen = (List<LinkedHashMap<String, Object>>) claimsBody.getBody()
				.get("eigenschappenIngevuld");*/
		
		//Afstand ontvangen=hashMapNaarAfstand(afstandenLijst);
		//Afstand ontvangen2=hashMapNaarAfstand(filiaalIdLijst);

		//List<Eigenschappenbedrijfingevuld> eigenschappenIngevuldLijst = hashMapNaarEigenschapBedrijf(eigenschappen);
		//List<Customeigenschapingevuld> customEigenschappenIngevuld = hashMapNaarCustomEigenschap(customEigenschappen);
		//List<Taak> taken = null;

		/*if (claimsBody.getBody().get("ingevuld").equals("y"))
			taken = taakEJB.findAllTakenFiliaal(Integer.parseInt(claims.getBody().getId()));
		taakEJB.setIngevuld(taken);
		customEigenschapIngevuldEJB.mergeEigenschappen(customEigenschappenIngevuld);
		eigenschapBedrijfIngevuldEJB.mergeEigenschappen(eigenschappenIngevuldLijst);*/
		

		Date curDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.DATE, 2);

		Key key = ApplicationConfig.JWT_KEY;
		String jwtToken = Jwts.builder().setIssuer("http://localhost:8080/RetailSonarRest/restService/")
				.setIssuedAt(curDate).setExpiration(cal.getTime()).signWith(SignatureAlgorithm.HS512, key).compact();

		return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer ").build();
	}
	
}
