package com.projekat.bezbednostWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projekat.bezbednostWeb.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);
	
	User findByEmailAndPassword(String email, String password);
	
}
