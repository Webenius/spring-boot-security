package com.sambi.app.rest.security;

import org.springframework.security.core.GrantedAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;

public class AppUser {

	private String username;
	@JsonIgnore
	private String password;
	private String firstName;
	private String lastName;
	private String mailAddress;
	private List<String> roles = new ArrayList<>(1);
	@JsonIgnore
	private List<GrantedAuthority> authorities;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void addAuthority(GrantedAuthority authority) {
        if (this.authorities == null) this.authorities = new ArrayList<>();
        this.authorities.add(authority);
    }

	@Override
	public String toString() {
		return "AppUser [username=" + username + ", password=" + password
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", mailAddress=" + mailAddress + ", roles=" + roles
				+ ", authorities=" + authorities + "]";
	}
}
