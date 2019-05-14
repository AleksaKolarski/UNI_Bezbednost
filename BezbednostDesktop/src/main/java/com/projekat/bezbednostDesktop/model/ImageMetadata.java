package com.projekat.bezbednostDesktop.model;

public class ImageMetadata {
	
	private String name;
	private Integer size;
	private String hash;
	
	
	public ImageMetadata(String name, Integer size, String hash) {
		this.name = name;
		this.size = size;
		this.hash = hash;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
