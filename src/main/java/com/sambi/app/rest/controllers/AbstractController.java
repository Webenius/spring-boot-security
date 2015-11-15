package com.sambi.app.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sambi.app.rest.exception.RestException;
import com.sambi.app.rest.exception.RestExceptionCode;
import com.sambi.app.rest.security.AppUser;

/**
 * @author rbensassi
 * 
 * TODO: all the Controllers (except AuthenticationController) should extends AbstractController!!!
 */
public class AbstractController {

    private final static Logger logger = LoggerFactory.getLogger(AbstractController.class);

    String getAuthorizedUserName() {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

//        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof AppUser)) {
//            // hack for development
//            if ("dev".equals(System.getProperty("spring.profiles.active"))) {
//                return "test";
//            }
        if (auth != null && !(auth.getPrincipal() instanceof AppUser)) {
            logger.error("No authorized user found");
            throw new RestException("Kein berechtigter Benutzer gefunden", RestExceptionCode.FC_RE_001);
        }
        return ((AppUser)auth.getPrincipal()).getUsername();
    }
}