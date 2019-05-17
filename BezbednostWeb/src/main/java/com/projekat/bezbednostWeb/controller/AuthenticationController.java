package com.projekat.bezbednostWeb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.security.TokenHelper;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	TokenHelper tokenHelper;

	@Autowired
	private AuthenticationManager authenticationManager;


	@PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> createAuthenticationToken(@RequestParam("email") String email, @RequestParam("password") String password) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (AuthenticationException e) {
			return new ResponseEntity<String>("Wrong email/password.", HttpStatus.FORBIDDEN);
		}
		// Ubaci email + password u kontext
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Kreiraj token
		User user = (User) authentication.getPrincipal();
		String jws = tokenHelper.generateToken(user.getUsername());

		// Vrati token kao odgovor na uspesno autentifikaciju
		return new ResponseEntity<String>(jws, HttpStatus.OK);
	}
}