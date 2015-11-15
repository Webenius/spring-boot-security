package com.sambi.app.rest.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sambi.app.rest.RestApplication;
import com.sambi.app.rest.exception.RestException;
import com.sambi.app.rest.exception.RestExceptionCode;
import com.sambi.app.rest.model.request.User;
import com.sambi.app.rest.model.response.MessageResponse;
import com.sambi.app.rest.model.response.TokenResponse;
import com.sambi.app.rest.security.AppUser;
import com.sambi.app.rest.security.AppUserStorage;
import com.sambi.app.rest.security.TokenService;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AppUserStorage appUserStorage;
    private final ObjectMapper jsonMapper = new ObjectMapper();


    @RequestMapping(value="/login", method=RequestMethod.POST)
	public TokenResponse authenticate(@RequestBody User user, HttpServletRequest request) throws JsonProcessingException {

        //commonService.checkDatabaseConnection(); // check if database is available

		String jsonUserDetails = null;
		if (user==null || StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            throw new RestException(RestExceptionCode.EC_FE_001);
		}
		
		try {
			// authenticate against Spring Security
			UsernamePasswordAuthenticationToken usernamePasswordAuth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			Authentication authentication = authenticationManager.authenticate(usernamePasswordAuth);
			if (authentication.isAuthenticated() && authentication.getPrincipal() != null && (authentication.getPrincipal() instanceof AppUser)) {
				jsonUserDetails = jsonMapper.writeValueAsString(authentication.getPrincipal());
				logger.info("Authenticated user: {}", jsonUserDetails);
			}
		} catch (Exception e) {
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {  
			   ipAddress = request.getRemoteAddr();  
			}
			logger.error("Username: {}; IP address: {}; {}", user.getUsername(),ipAddress, e.getMessage(), e);
			throw new RestException(RestExceptionCode.EC_FE_001);
		}
		
		// no exception was thrown, so create a token and return it
		return new TokenResponse(tokenService.createToken(jsonUserDetails));
	}


    @RequestMapping("/logout/{username}")
	public MessageResponse logout(@PathVariable("username") String username, HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) new SecurityContextLogoutHandler().logout(request, response, auth);
	    appUserStorage.removeAppUser(username);
		return new MessageResponse("Benutzer '"+username+"' abgemeldet");
	}


    @RequestMapping("/version")
    public MessageResponse version() {
        String appVersion = RestApplication.class.getPackage().getImplementationVersion();
        return new MessageResponse(appVersion);
    }
}