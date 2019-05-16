package com.projekat.bezbednostWeb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.service.UserService;

@Component
public class Util {
	
	@Autowired
	private UserService userService;
	
	public User getCurrentUser() {
		String userUsername;
		Authentication currentUserAuth;
		currentUserAuth = SecurityContextHolder.getContext().getAuthentication();
		if(currentUserAuth != null) {
			userUsername = currentUserAuth.getName();
			if(userUsername != null) {
				if(userService != null) {
					return userService.findByEmail(userUsername);
				}
				System.out.println("USER SERVICE = NULL");
			}
			return null;
		}
		return null;
	}
}
