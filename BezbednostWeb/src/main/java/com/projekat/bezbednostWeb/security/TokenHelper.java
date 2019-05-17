package com.projekat.bezbednostWeb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TokenHelper {

	@Value("bezbednostWeb")
	private String APP_NAME;

	@Value("somesecret")
	public String SECRET;

	@Value("Authorization")
	private String AUTH_HEADER;

	//HMAC sa SHA-512 hash algoritmom
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	//Metoda koja iz sadrzaja tokena (claims) pronalazi username korisnika
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	//Funkcija koja pri autentifikaciji korisnika kreira novi JWT token
	public String generateToken(String username) {
		return Jwts.builder().setIssuer(APP_NAME).setSubject(username).signWith(SIGNATURE_ALGORITHM, SECRET).compact();
	}

	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}
	
	//Funkcija koja proverava da li je token validan u odnosu na korisnika koji ga je prosledio serveru
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username != null && username.equals(userDetails.getUsername()));
	}

	public String getToken(HttpServletRequest request) {
		/**
		 * Getting the token from Authentication header e.g Bearer your_token
		 */
		String authHeader = request.getHeader(AUTH_HEADER);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
}