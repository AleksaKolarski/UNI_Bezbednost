package com.projekat.bezbednostWeb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projekat.bezbednostWeb.entity.Role;
import com.projekat.bezbednostWeb.repository.RoleRepository;

@Service
public class RoleService implements RoleServiceInterface {
	
	@Autowired
	private RoleRepository roleRepository;

	
	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public Role findByName(String name) {
		return roleRepository.findByName(name);
	}

	@Override
	public Role findById(Integer id) {
		Optional<Role> optional = roleRepository.findById(id);
		if(optional.isPresent() == false)
			return null;
		return optional.get();
	}
	
	@Override
	public List<Role> findByUserId(Integer userId){
		return roleRepository.findByUsers_Id(userId);
	}

	@Override
	public Role save(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public void remove(Role role) {
		roleRepository.deleteById(role.getId());
	}
}
