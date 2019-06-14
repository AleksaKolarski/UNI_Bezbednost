package com.projekat.bezbednostWeb.service;

import com.projekat.bezbednostWeb.entity.ImagePackage;

public interface ImagePackageServiceInterface {

	ImagePackage findById(Integer id);
	
	ImagePackage save(ImagePackage imagePackage);
	
	void remove(ImagePackage imagePackage);
	
}
