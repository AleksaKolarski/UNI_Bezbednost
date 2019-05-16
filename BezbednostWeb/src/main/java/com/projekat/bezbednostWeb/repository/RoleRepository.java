package com.projekat.bezbednostWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projekat.bezbednostWeb.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	Role findByName(String name);
	
	List<Role> findByUsers_Id(Integer userId);
	
}
