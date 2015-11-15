package com.sambi.app.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sambi.app.rest.exception.RestException;
import com.sambi.app.rest.exception.RestExceptionCode;

/**
 * Checks for the X-Auth-Token HTTP header and if set automatically
 * authenticates the user using the data from the token.
 */
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenProcessingFilter.class);

    private final TokenService tokenService;
    private final AppUserStorage appUserStorage;
    private final ObjectMapper jsonMapper;

    public AuthenticationTokenProcessingFilter(TokenService tokenService, AppUserStorage appUserStorage) {
        this.tokenService   = tokenService;
        this.appUserStorage = appUserStorage;
        this.jsonMapper     = new ObjectMapper();
    }

    /**
     * Extracts the token either from the X-Auth-Token HTTP header or from a
     * request parameter called "token".
     */
    private static String extractAuthTokenFromRequest(HttpServletRequest request) {

        // try getting the token from the header
        String token = request.getHeader("X-Auth-Token");
        logger.debug("Request Header token: {}", token);

        // if no token was set in the header, try the request parameters
        if (token == null) {
            token = request.getParameter("token");
            logger.debug("Request Parameter token: {}", token);
        }
        return token;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,X-Auth-Token");
        httpResponse.setHeader("Access-Control-Expose-Headers", "X-Auth-Token");
        String token = extractAuthTokenFromRequest(httpRequest);

        if (token != null) {
            try {
                // extract JSON UserDetails from the token
                String jsonUserDetails = tokenService.extractJsonUserDetails(token);
                logger.debug("Extracted JsonUserDetails from Token in Request: {}", jsonUserDetails);

                if (jsonUserDetails != null) {

                    AppUser appUser = appUserStorage.getAppUser(jsonMapper.readValue(jsonUserDetails, AppUser.class));
                    logger.debug("AppUser: {}", appUser);

                    if (appUser != null) {
                        Date expirationDate = tokenService.extractExpirationDate(token);
                        logger.debug("Token expirationDate: {}", expirationDate);

                        if (expirationDate != null && expirationDate.getTime() > System.currentTimeMillis()) {

                            logger.debug("The token isn't expired, authenticate the user from the token for this request " +
                                    "add an updated token to the response so the client-side session will be refreshed");

                            // if the token isn't expired, authenticate the user from the token for this request
                            // and add an updated token to the response so the client-side session will be refreshed
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            addUpdatedAuthTokenToResponse(jsonUserDetails, httpResponse);
                            appUserStorage.storeAppUser(appUser);

                            logger.debug("Authenticated user: {} ", jsonUserDetails);
                        } else {
                            logger.debug("User athentication expired: {}", appUser.getUsername());
                            throw new RestException("Authorisierung abgelaufen", RestExceptionCode.FC_RE_001, HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        logger.debug("User authentification expired on server side");
                        //TODO: ist die folgende Exception nötig???
                        throw new RestException("Authorisierung abgelaufen", RestExceptionCode.FC_RE_001, HttpStatus.UNAUTHORIZED);
                    }
                }
            } catch (InvalidSignatureException | IllegalArgumentException e) {
                logger.debug("Invalid signature passed; continue filter chain");
            }
        }
        logger.debug("httpRequest Method: {}", httpRequest.getMethod());
        if (!httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
        }
    }

    /**
     * Adds an updated token to the response's header.
     */
    private void addUpdatedAuthTokenToResponse(String jsonUserDetails, HttpServletResponse response) {
        try {
            response.addHeader("X-Auth-Token", tokenService.createToken(jsonUserDetails));
        } catch (JsonProcessingException e) {
            logger.error("Failed to create auth token", e);
        }
    }
}
