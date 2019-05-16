package com.projekat.bezbednostWeb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.repository.UserRepository;

@Service
public class UserService implements UserServiceInterface {
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public User getCurrentUser() {
		String email;
		Authentication currentUserAuth;
		currentUserAuth = SecurityContextHolder.getContext().getAuthentication();
		if(currentUserAuth != null) {
			email = currentUserAuth.getName();
			if(email != null) {
				return findByEmail(email);
			}
			return null;
		}
		return null;
	}
	
	@Override
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User findByEmailAndPassword(String email, String password) {
		return userRepository.findByEmailAndPassword(email, password);
	}
	
	@Override
	public User findById(Integer userId) {
		Optional<User> optional = userRepository.findById(userId);
		if(optional.isPresent() == false)
			return null;
		return optional.get();
	}
	
	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public void remove(User user) {
		userRepository.deleteById(user.getId());
	}
}
