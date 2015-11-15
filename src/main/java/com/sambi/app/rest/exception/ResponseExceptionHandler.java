package com.sambi.app.rest.exception;

import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sambi.app.rest.model.response.ErrorResponse;


@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @Override
    @ResponseBody
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Object errorResponse = new ErrorResponse(RestExceptionCode.FC_RE_001, "Methode nicht gefunden");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RestException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMetisRestException(RestException ex) {
    	if (ex.getExceptionCode().isLogError()) {
    	    logger.error("Handle metis exception", ex);
    	}
    	ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
	}

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBadCredentials(Exception ex) {
    	ErrorResponse errorResponse = new ErrorResponse(RestExceptionCode.FC_RE_001, "Authorisierung erforderlich");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(RestExceptionCode.FC_RE_001, "Zugriff verweigert");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({CannotGetJdbcConnectionException.class, CannotCreateTransactionException.class,
        ConnectException.class, DataAccessResourceFailureException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> databaseNotPresent(Exception ex) {
        logger.error("No connection to METIS database");
        ErrorResponse errorResponse = new ErrorResponse(RestExceptionCode.EC_FE_003);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDefaultException(Exception ex) {
        logger.error("Handle exception", ex);
    	ErrorResponse errorResponse = new ErrorResponse(RestExceptionCode.FC_RE_001, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
   
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, RequestAttributes.SCOPE_REQUEST);
        }
        Object errorResponse = new ErrorResponse(RestExceptionCode.FC_RE_001, ex.getMessage());
        return new ResponseEntity<>(errorResponse, headers, status);
    }
}