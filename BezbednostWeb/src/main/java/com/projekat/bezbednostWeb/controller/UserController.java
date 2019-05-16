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
	@RequestMapping(value = "/activate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> activate(@RequestParam("userId") Integer id){
		
		return null;
	}
	
	// Deactivate
	@RequestMapping(value = "/deactivate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> deactivate(@RequestParam("userId") Integer id){
		
		return null;
	}
	
	// Nije u zahtevu projekta
	/*
	@RequestMapping(value = "/edit", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserDTO> edit(@RequestBody UserDTO userDTO){
		User currentUser = util.getCurrentUser();
		if(currentUser == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(userDTO.getId() != currentUser.getId() && !currentUser.checkRole("ROLE_ADMIN")) {
			// menja tudji profil a nije admin
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		String firstname = userDTO.getFirstname();
		String lastname = userDTO.getLastname();
		String username = userDTO.getUsername();
		boolean isAdmin = userDTO.getIsAdmin();
		
		if(firstname == null || firstname.length() < 5 || firstname.length() > 30) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(lastname == null || lastname.length() < 5 || lastname.length() > 30) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(username == null || username.length() < 5 || username.length() > 10) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		User editUser = userService.findById(userDTO.getId());
		
		if(editUser == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		editUser.setFirstname(firstname);
		editUser.setLastname(lastname);
		editUser.setUsername(username);
		if(isAdmin == true) {
			editUser.getRoles().add(roleService.findByName("ROLE_ADMIN"));
		}
		else {
			editUser.getRoles().remove(roleService.findByName("ROLE_ADMIN"));
		}
		
		editUser = userService.save(editUser);
		
		if(editUser == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(new UserDTO(editUser), HttpStatus.OK);
	}
	*/
	
	// Nije u zahtevu projekta
	/*
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> delete(@RequestParam("userId") Integer userId){
		User currentUser = util.getCurrentUser();
		if(currentUser == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}		
		
		if(userId != currentUser.getId() && !currentUser.checkRole("ROLE_ADMIN")) {
			// brise tudji profil a nije admin
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		User removeUser = userService.findById(userId);
		
		if(removeUser == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		userService.remove(removeUser);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	*/
	
	// Nije u zahtevu projekta
	/*
	@RequestMapping(value = "/change-password", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> change_password(@RequestParam("userId") Integer userId, @RequestParam("password") String password){
		User userEdit = userService.findById(userId);
		if(userEdit == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		User currentUser = util.getCurrentUser();
		if(currentUser == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(userEdit.getId() != currentUser.getId() && currentUser.getIsAdmin() == false) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		userEdit.setPassword(bCryptPasswordEncoder.encode(password));
		userService.save(userEdit);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	*/
}
