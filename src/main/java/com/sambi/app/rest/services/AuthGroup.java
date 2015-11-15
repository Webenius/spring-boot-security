package com.sambi.app.rest.services;

import jdk.nashorn.api.scripting.AbstractJSObject;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthGroup extends AbstractJSObject {

	private static final long serialVersionUID = -8954251819008392558L;
	
	@JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}