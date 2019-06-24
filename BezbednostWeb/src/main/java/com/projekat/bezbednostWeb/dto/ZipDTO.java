package com.projekat.bezbednostWeb.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.projekat.bezbednostWeb.entity.Zip;

public class ZipDTO {

	private Integer id;
	private String date;
	private String time;
	
	
	public ZipDTO() {}
	
	public ZipDTO(Zip zip) {
		this.id = zip.getId();
		this.date = new SimpleDateFormat("dd.MM.yyyy.").format(zip.getDate());
		this.time = new SimpleDateFormat("HH:mm:ss").format(zip.getDate());
	}
	
	public ZipDTO(Integer id, Date date) {
		this.id = id;
		this.date = new SimpleDateFormat("dd.MM.yyyy.").format(date);
		this.time = new SimpleDateFormat("HH:mm:ss").format(date);
	}
	
	
	public static List<ZipDTO> parseList(List<Zip> list){
		List<ZipDTO> listDTO = new ArrayList<>();
		for(Zip zip: list) {
			listDTO.add(new ZipDTO(zip));
		}
		return listDTO;
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
