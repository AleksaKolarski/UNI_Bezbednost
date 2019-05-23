package com.projekat.bezbednostWeb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projekat.bezbednostWeb.zip.ZipChecker;

@RestController
@RequestMapping("/image")
public class ImageController {
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	//@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<String> submit(@RequestParam("file") MultipartFile file) {
		
		ZipChecker.check(file);
		
		/*
		try {
			Path rootPath = Paths.get("data/img");
			Path path = rootPath.resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), path);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
