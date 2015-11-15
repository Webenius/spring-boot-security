package com.sambi.app.rest.exception;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

	private static final long serialVersionUID = -1099091914203779518L;
	
	private RestExceptionCode exceptionCode;
	private HttpStatus httpStatus;

   public RestException(RestExceptionCode exceptionCode) {
        super(exceptionCode.getError());
        this.exceptionCode = exceptionCode;
        this.httpStatus = exceptionCode.getHttpStatus();
    }

   public RestException(String message, RestExceptionCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.httpStatus = exceptionCode.getHttpStatus();
    }

	public RestException(String message, RestExceptionCode exceptionCode, HttpStatus httpStatus) {
		super(message);
		this.exceptionCode = exceptionCode;
		this.httpStatus = httpStatus;
	}

	public RestExceptionCode getExceptionCode() {
		return exceptionCode;
	}

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}