package rest;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import com.sun.security.auth.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class JWTSecurityContext implements SecurityContext{

	private String role;
	private Principal principal;
	private boolean isSecure;
	
	public JWTSecurityContext(Jws<Claims> claims) {
		super();
		
		principal = new UserPrincipal(claims.getBody().getId());
		role = claims.getBody().getSubject();
		isSecure = true;
	}


	@Override
	public Principal getUserPrincipal() {
		return principal;
	}

	@Override
	public boolean isUserInRole(String role) {
		return this.role.equals(role);
	}

	@Override
	public boolean isSecure() {
		return isSecure;
	}

	@Override
	public String getAuthenticationScheme() {
		return "OAUTH_BEARER";
	}


}
