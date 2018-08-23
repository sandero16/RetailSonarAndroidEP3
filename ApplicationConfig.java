package rest;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.security.Key;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest_service")
public class ApplicationConfig extends Application {

	private final Set<Class<?>> classes;
	private static final String KEY_DATA = "dkz45KZADH@#!!EF684pm";
	public static final Key JWT_KEY = new SecretKeySpec(KEY_DATA.getBytes(), 0, 16, "AES");

	public ApplicationConfig() {
		HashSet<Class<?>> c = new HashSet<>();
		c.add(BedrijfRestService.class);
		c.add(AuthenticationService.class);
		c.add(JWTTokenNeededFilter.class);

		classes = Collections.unmodifiableSet(c);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

}