package com.projekat.bezbednostWeb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projekat.bezbednostWeb.entity.Zip;
import com.projekat.bezbednostWeb.repository.ZipRepository;

@Service
public class ZipService implements ZipServiceInterface {

	@Autowired
	ZipRepository imagePackageRepository;
	
	
	@Override
	public Zip findById(Integer id) {
		return imagePackageRepository.findById(id).orElse(null);
	}

	@Override
	public Zip save(Zip imagePackage) {
		return imagePackageRepository.save(imagePackage);
	}

	@Override
	public void remove(Zip imagePackage) {
		imagePackageRepository.delete(imagePackage);
	}

}
