package com.projekat.bezbednostWeb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
        	return null;
        }
        if (user.isEnabled() == false) {
        	return null;
        }
        return user;
    }
}