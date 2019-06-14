package com.projekat.bezbednostWeb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projekat.bezbednostWeb.entity.ImagePackage;
import com.projekat.bezbednostWeb.repository.ImagePackageRepository;

@Service
public class ImagePackageService implements ImagePackageServiceInterface {

	@Autowired
	ImagePackageRepository imagePackageRepository;
	
	
	@Override
	public ImagePackage findById(Integer id) {
		return imagePackageRepository.findById(id).orElse(null);
	}

	@Override
	public ImagePackage save(ImagePackage imagePackage) {
		return imagePackageRepository.save(imagePackage);
	}

	@Override
	public void remove(ImagePackage imagePackage) {
		imagePackageRepository.delete(imagePackage);
	}

}
