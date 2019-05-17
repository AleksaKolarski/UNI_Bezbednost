package com.projekat.bezbednostWeb.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Filter koji ce presretati svaki zahtev klijenta ka serveru
//Sem nad putanjama navedenim u WebSecurityConfig.configure(WebSecurity web)
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenHelper tokenHelper;

    private UserDetailsService userDetailsService;

    public TokenAuthenticationFilter(TokenHelper tokenHelper, UserDetailsService userDetailsService) {
        this.tokenHelper = tokenHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String username;
        String authToken = tokenHelper.getToken(request);

        if (authToken != null) {
            //uzmi username iz tokena
            username = tokenHelper.getUsernameFromToken(authToken);
            if (username != null) {
                // uzmi user-a na osnovu username-a
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(userDetails != null) {
                	//proveri da li je prosledjeni token validan
                	if (tokenHelper.validateToken(authToken, userDetails)) {
                		// kreiraj autentifikaciju
                		TokenBasedAuthentication authentication = new TokenBasedAuthentication(authToken, userDetails);
                		SecurityContextHolder.getContext().setAuthentication(authentication);
                	}
                	else {
                	}
                }
                else {
                }
            }
            else{
            }
        }
        else {
        }
        chain.doFilter(request, response);
    }
}