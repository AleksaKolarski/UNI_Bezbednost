package com.projekat.bezbednostWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projekat.bezbednostWeb.entity.ImagePackage;

@Repository
public interface ImagePackageRepository extends JpaRepository<ImagePackage, Integer> {

}
