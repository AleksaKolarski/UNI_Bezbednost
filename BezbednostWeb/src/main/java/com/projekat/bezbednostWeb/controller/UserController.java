package com.projekat.bezbednostWeb.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projekat.bezbednostWeb.certificate.JKS;
import com.projekat.bezbednostWeb.dto.UserDTO;
import com.projekat.bezbednostWeb.entity.Role;
import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.service.RoleService;
import com.projekat.bezbednostWeb.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private JKS jks;
	
	
	@GetMapping("/currentUser")
	public ResponseEntity<UserDTO> getCurrentUser() {
		User user = userService.getCurrentUser();
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDTO>> getAllUsers(){
		return new ResponseEntity<>(UserDTO.parseList(userService.findAll()), HttpStatus.OK);
	}
	
	// register
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<UserDTO> register(@RequestParam("email") String email, @RequestParam("password") String password) {
		
		if(email == null || email.length() < 5 || email.length() > 30) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		if(password == null || password.length() < 5) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		if(userService.findByEmail(email) != null) {
			return new ResponseEntity<>(null, HttpStatus.CONFLICT);
		}
		
		User user = new User();
		user.setEmail(email);
		user.setPassword(bCryptPasswordEncoder.encode(password));
		user.setActive(false);
		user.setCertificate(email + ".jks");
		Set<Role> roles = new HashSet<Role>();
		roles.add(roleService.findByName("ROLE_REGULAR"));
		user.setRoles(roles);
		
		user = userService.save(user);
		
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		// Generate certificate		
		jks.generateSignedJKS(user.getId(), email, password);
		
		return new ResponseEntity<UserDTO>(new UserDTO(user), HttpStatus.CREATED);
	}
	
	// Set active
	@RequestMapping(value = "/setActive", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> setActive(@RequestParam("userId") Integer userId, @RequestParam("active") Boolean active){
		
		User user = userService.findById(userId);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		user.setActive(active);
		userService.save(user);
		
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
	
	// Set admin
	@RequestMapping(value = "/setAdmin", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> setAdmin(@RequestParam("userId") Integer userId, @RequestParam("admin") Boolean admin){
		
		User user = userService.findById(userId);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if(admin == true && user.getIsAdmin() == false) {
			Role roleAdmin = roleService.findByName("ROLE_ADMIN");
			user.getRoles().add(roleAdmin);
			user = userService.save(user);
		}
		if(admin == false && user.getIsAdmin() == true) {
			Role roleAdmin = roleService.findByName("ROLE_ADMIN");
			user.getRoles().remove(roleAdmin);
			user = userService.save(user);
		}
		
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
}
