package com.sambi.app.rest.model.response;

/**
 * @author rbensassi
 * date 09.09.2015
 */
public class TokenResponse {

	private final String token;

	public TokenResponse(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
