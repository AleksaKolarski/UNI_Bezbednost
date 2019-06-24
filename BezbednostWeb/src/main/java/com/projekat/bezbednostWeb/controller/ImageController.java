package com.projekat.bezbednostWeb.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.bouncycastle.crypto.CryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projekat.bezbednostWeb.dto.ZipDTO;
import com.projekat.bezbednostWeb.entity.Zip;
import com.projekat.bezbednostWeb.entity.User;
import com.projekat.bezbednostWeb.service.ZipService;
import com.projekat.bezbednostWeb.service.UserService;
import com.projekat.bezbednostWeb.zip.CryptoHelperAES;
import com.projekat.bezbednostWeb.zip.ZipChecker;

@RestController
@RequestMapping("/image")
public class ImageController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ZipService zipService;
	
	@Autowired
	private CryptoHelperAES cryptoHelperAES;
	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	//@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<String> submit(@RequestParam("file") MultipartFile file) {
		
		String email;
		User user;
		ZipChecker zipChecker;
		Zip zip;
		
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
		
		zip = new Zip();
		zip.setUser(user);
		zip.setDate(zipChecker.getDate());
		zip = zipService.save(zip);
		
		try {
			Path rootPath = Paths.get("data/img");
			Path path = rootPath.resolve(zip.getId() + ".zip");
			
			byte[] fileBytes = file.getBytes();
			byte[] encryptedFileBytes = cryptoHelperAES.encrypt(fileBytes);
			
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(encryptedFileBytes);
			
			Files.copy(byteInputStream, path);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException | CryptoException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// get all zips for current user
	@RequestMapping(value = "/allFromCurrentUser")
	@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<List<ZipDTO>> allZipsCurrentUser(){
		
		User currentUser = userService.getCurrentUser();
		
		List<Zip> list = currentUser.getZipList();
		List<ZipDTO> listDTO = ZipDTO.parseList(list);
		
		return new ResponseEntity<>(listDTO, HttpStatus.OK);
	}
	
	// skidanje celog zipa
	@RequestMapping(value = "/download/{id}")
	@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<byte[]> download(@PathVariable("id") Integer id){
		
		User currentUser = userService.getCurrentUser();
		Zip zip = zipService.findById(id);
		
		if(zip == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if( ! currentUser.getEmail().equals(zip.getUser().getEmail())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		File file;
		byte[] bFile;
		byte[] decryptedFileBytes;
		try {
			file = new File("data/img/" + id + ".zip");
			bFile = Files.readAllBytes(file.toPath());
			decryptedFileBytes = cryptoHelperAES.decrypt(bFile);
		} catch (IOException | CryptoException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
		headers.add("filename", file.getName());
		return ResponseEntity.ok().headers(headers).body(decryptedFileBytes);
	}
	
	// get images by ImagePackage ID
	/*
	@RequestMapping(value = "/show/{id}")
	public ResponseEntity<List<byte[]>> showAllFromImagePackage(){
		
		// decrypt zip file
		
		// load images one by one
		ZipFile zipFile = null;
		
		
		// put image bytes in array
		
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	*/
}
