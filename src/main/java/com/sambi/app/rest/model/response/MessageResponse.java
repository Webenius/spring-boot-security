package com.sambi.app.rest.model.response;

public final class MessageResponse extends AbstractResponse {

	private final String message;

	public MessageResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
