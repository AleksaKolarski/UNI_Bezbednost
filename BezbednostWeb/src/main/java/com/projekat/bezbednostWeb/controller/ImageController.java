package com.projekat.bezbednostWeb.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projekat.bezbednostWeb.entity.ImagePackage;
import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.service.ImagePackageService;
import com.projekat.bezbednostWeb.service.UserService;
import com.projekat.bezbednostWeb.zip.ZipChecker;

@RestController
@RequestMapping("/image")
public class ImageController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImagePackageService imagePackageService;
	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	//@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<String> submit(@RequestParam("file") MultipartFile file) {
		
		String email;
		User user;
		ZipChecker zipChecker;
		ImagePackage imagePackage;
		
		zipChecker = new ZipChecker(file);
		if( ! zipChecker.check()) {
			System.out.println("Bad file.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		System.out.println("Good file");
		
		email = zipChecker.getEmail();
		user = userService.findByEmail(email);
		
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		imagePackage = new ImagePackage();
		imagePackage.setUser(user);
				
		imagePackage = imagePackageService.save(imagePackage);
		
		try {
			Path rootPath = Paths.get("data/img");
			Path path = rootPath.resolve(imagePackage.getId() + ".zip");
			Files.copy(file.getInputStream(), path);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// skidanje celog zipa
	@RequestMapping(value = "/download/{id}")
	@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<String> download(@PathParam("id") Integer id){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// get images by ImagePackage ID
	@RequestMapping(value = "/show/{id}")
	public ResponseEntity<List<String>> showAllFromImagePackage(){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
