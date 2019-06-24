package com.projekat.bezbednostWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projekat.bezbednostWeb.entity.Zip;

@Repository
public interface ZipRepository extends JpaRepository<Zip, Integer> {

}
