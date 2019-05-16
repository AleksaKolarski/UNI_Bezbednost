package com.projekat.bezbednostWeb.service;

import java.util.List;

import com.projekat.bezbednostWeb.entity.User;

public interface UserServiceInterface {
	
	User getCurrentUser();
	
	List<User> findAll();
	
	User findByEmail(String email);
	
	User findByEmailAndPassword(String email, String password);
	
	User findById(Integer userId);
	
	User save(User user);
	
	void remove(User user);
	
}
