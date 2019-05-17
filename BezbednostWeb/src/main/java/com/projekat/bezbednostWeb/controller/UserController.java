package com.projekat.bezbednostWeb.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	
	@GetMapping("/currentUser")
	public ResponseEntity<UserDTO> getCurrentUser() {
		User user = userService.getCurrentUser();
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserDTO> getById(@RequestParam("userId") Integer userId) {
		
		User currentUser = userService.getCurrentUser();
		if(currentUser.getIsAdmin() == false && currentUser.getId() != userId) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		User user = userService.findById(userId);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDTO>> getAllUsers(){
		return new ResponseEntity<>(UserDTO.parseList(userService.findAll()), HttpStatus.OK);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
		if(userDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
				
		String email = userDTO.getEmail();
		String password = userDTO.getPassword();
		
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
		System.out.println(user.getPassword());
		user.setActive(false);
		user.setCertificate("cert");
		Set<Role> roles = new HashSet<Role>();
		roles.add(roleService.findByName("ROLE_REGULAR"));
		user.setRoles(roles);
		
		user = userService.save(user);
		
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<UserDTO>(new UserDTO(user), HttpStatus.CREATED);
	}
	
	// Activate
	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> activate(@RequestParam("userId") Integer userId){
		
		User user = userService.findById(userId);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		user.setActive(true);
		userService.save(user);
		
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
	
	// Deactivate
	@RequestMapping(value = "/deactivate", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> deactivate(@RequestParam("userId") Integer userId){
		
		User user = userService.findById(userId);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		user.setActive(false);
		userService.save(user);
		
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}
}
