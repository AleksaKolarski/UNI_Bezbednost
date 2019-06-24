package com.projekat.bezbednostWeb.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.service.UserService;

@RestController
@RequestMapping("/certificate")
public class CertificateController {
	
	@Autowired
	private UserService userService;

	
	@GetMapping("/download")
	@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<byte[]> download(){
		
		User user = userService.getCurrentUser();
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		File file;
		byte[] bFile;
		try {
			file = new File("data/" + user.getCertificate());
			bFile = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
		headers.add("filename", file.getName());
		return ResponseEntity.ok().headers(headers).body(bFile);
	}
	
}
