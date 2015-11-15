package com.sambi.app.rest.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.sambi.app.domain.AuthorityEntity;
import com.sambi.app.domain.UserEntity;
import com.sambi.app.rest.exception.RestException;
import com.sambi.app.rest.exception.RestExceptionCode;
import com.sambi.app.rest.services.AuthService;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private AuthService authenticationService;
    @Autowired
    private AppUserStorage appUserStorage;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = (String)authentication.getPrincipal();
        String password = (String)authentication.getCredentials();
 
        if (appUserStorage.isAppUserPresent(username)) {
            throw new RestException(RestExceptionCode.EC_FE_002);
        }
        logger.debug("Authentification for user: {}", username);

        List<AuthorityEntity> authorities = this.authenticationService.authorize(username, password);
        AppUser appUser = new AppUser();

        UserEntity user = null;
        for(AuthorityEntity authEntity: authorities) {

            if(user == null) {
                user = authEntity.getUser();
                if (!user.isEnabled()) {
                    throw new RestException(RestExceptionCode.EC_FE_006);
                }
                appUser.setUsername(user.getUsername());
                appUser.setFirstName(user.getUsername());//TODO: set the right firstName
            }
            appUser.addRole(authEntity.getAuthority());
            appUser.addAuthority(new SimpleGrantedAuthority(authEntity.getAuthority()));
        }
        appUserStorage.storeAppUser(appUser);

        return new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
