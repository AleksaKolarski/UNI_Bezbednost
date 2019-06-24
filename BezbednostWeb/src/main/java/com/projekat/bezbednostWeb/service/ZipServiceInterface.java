package com.projekat.bezbednostWeb.service;

import com.projekat.bezbednostWeb.entity.Zip;

public interface ZipServiceInterface {

	Zip findById(Integer id);
	
	Zip save(Zip imagePackage);
	
	void remove(Zip imagePackage);
	
}
