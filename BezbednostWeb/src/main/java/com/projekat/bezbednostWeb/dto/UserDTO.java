package com.projekat.bezbednostWeb.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.projekat.bezbednostWeb.entity.ImagePackage;
import com.projekat.bezbednostWeb.entity.User;

public class UserDTO {

	private Integer id;
	private String email;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	private List<Integer> imagePackages;
	
	//private String certificate;
	private Boolean active;
	private Boolean admin;
	
	
	public UserDTO() {}
	
	public UserDTO(User user) {
		id = user.getId();
		email = user.getEmail();
		imagePackages = new ArrayList<>();
		for(ImagePackage imagePackage: user.getImagePackages()) {
			imagePackages.add(imagePackage.getId());
		}
		active = user.getActive();
		admin = user.checkRole("ROLE_ADMIN");
	}
	
	
	public static List<UserDTO> parseList(List<User> list){
		List<UserDTO> listDTO = new ArrayList<UserDTO>();
		for(User user: list) {
			listDTO.add(new UserDTO(user));
		}
		return listDTO;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Integer> getImagePackages() {
		return imagePackages;
	}

	public void setImagePackages(List<Integer> imagePackages) {
		this.imagePackages = imagePackages;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
}
