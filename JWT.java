package be.retailsonar.retailsonar;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWT extends Activity{

    private Key JWT_KEY;
    public JWT(Key key){
        this.JWT_KEY = key;
    }

    public String maakLoginJWT(String username, String wachtwoord){
        Date nu = new Date(System.currentTimeMillis());

        Map<String,Object> claims = new HashMap<String, Object>();
        claims.put("username", username);
        claims.put("wachtwoord", wachtwoord);

        JwtBuilder builder = Jwts.builder().setId( "" + 0)
                .setIssuedAt(nu)
                .setSubject(username)
                .setIssuer("android client")
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, JWT_KEY);

        Date curDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);

        builder.setExpiration(cal.getTime());
        String token = builder.compact();

        return token;
    }
    public String maakGetBedrijfJWT(String login, int id){
        Date nu = new Date(System.currentTimeMillis());

        Map<String,Object> claims = new HashMap<String, Object>();
        claims.put("username", login);

        JwtBuilder builder = Jwts.builder().setId( "" + id)
                .setIssuedAt(nu)
                .setSubject(login)
                .setIssuer("android client")
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, JWT_KEY);

        Date curDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);

        builder.setExpiration(cal.getTime());
        String token = builder.compact();

        return token;
    }

    public String maakJWTMetLoginEnId(String login, int id){
        Date nu = new Date(System.currentTimeMillis());

        Map<String,Object> claims = new HashMap<String, Object>();
        claims.put("username", login);

        JwtBuilder builder = Jwts.builder().setId( "" + id)
                .setIssuedAt(nu)
                .setSubject(login)
                .setIssuer("android client")
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, JWT_KEY);

        Date curDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);

        builder.setExpiration(cal.getTime());
        String token = builder.compact();

        return token;
    }

    public String maakJWTVoorPostEigenschappen(HashMap<String, Object> claims){
        Date nu = new Date(System.currentTimeMillis());

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(nu)
                .setIssuer("android client")
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, JWT_KEY);

        Date curDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, 2);

        builder.setExpiration(cal.getTime());
        String token = builder.compact();

        return token;
    }



}
