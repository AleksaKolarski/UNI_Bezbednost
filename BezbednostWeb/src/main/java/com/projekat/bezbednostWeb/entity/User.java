package com.projekat.bezbednostWeb.entity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	
	@Column(name = "email", unique = true, nullable = false, length = 30)
	private String email;
	
	@Column(name = "password", unique = false, nullable = false, length = 65)
	private String password;
	
	@Column(name = "certificate", unique = true, nullable = false, length = 100)
	private String certificate;
	
	@Column(name = "active", unique = false, nullable = false)
	private Boolean active;
	
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Set<Role> roles;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Zip> zipList;

	
	public User() {}


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
	
	public String getCertificate() {
		return certificate;
	}
	
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public List<Zip> getZipList() {
		return zipList;
	}

	public void setZipList(List<Zip> zipList) {
		this.zipList = zipList;
	}

	public boolean getIsAdmin() {
		for(Role role: roles) {
			if(role.getName().equals("ROLE_ADMIN")) {
				return true;
			}
		}
		return false;
	}

	public boolean checkRole(String roleName) {
		for(Role role: roles) {
			if(role.getName().equals(roleName)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getUsername() {
		return email;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return this.roles;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return active;
	}
	
	
	@Override
	public String toString() {
		return "User ["
				+ "id=" + id + ", "
				+ "email=" + email + ", "
				+ "password=" + password + ", "
				+ "certificate=" + certificate + ", "
				+ "active=" + active + ", "
				+ "]";
	}
}
