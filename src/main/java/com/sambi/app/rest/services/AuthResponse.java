package com.sambi.app.rest.services;

import java.util.ArrayList;
import java.util.List;

import jdk.nashorn.api.scripting.AbstractJSObject;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthResponse extends AbstractJSObject {

	private static final long serialVersionUID = 6512647543978668198L;

	@JsonProperty("user")
	private AuthUser ldapUser;

	@JsonProperty("groups")
	private List<AuthGroup> groups = new ArrayList<>(0);

	@JsonProperty("message")
	private String message;

	private int statusCode;
	
	public AuthUser getLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(AuthUser ldapUser) {
		this.ldapUser = ldapUser;
	}

	public List<AuthGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<AuthGroup> groups) {
		this.groups = groups;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public boolean isAuthorized() {
		return (this.ldapUser != null);
	}

	public boolean hasGroup(String groupName) {
		if (this.groups == null || this.groups.size() == 0) {
			return false;
		}
		
		boolean hasGroup = false;
		loop: for (AuthGroup group : this.groups) {
			if (StringUtils.equals(group.getName(), groupName)) {
				hasGroup = true;
				break loop;
			}
		}
		return hasGroup;
	}
}
