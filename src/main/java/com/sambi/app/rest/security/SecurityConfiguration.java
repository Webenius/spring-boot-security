package com.sambi.app.rest.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.sambi.app.rest.exception.RestExceptionCode;
import com.sambi.app.rest.web.json.PlainJsonBuilder;

/**
 * NOTE: Global method security is not possible while using JPA!
 *       Just use request security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/***************************************************************
	 * The following tables are needed by the JdbcUserDetailsManager
	 * users (username, password, enabled)
	 * authorities (username, authority)
	 * groups (group_name)
	 * group_authorities (group_id, authority)
	 * group_members (group_id, username)
	 * 
	 * TODO: See: https://docs.spring.io/spring-security/site/docs/3.0.x/reference/appendix-schema.html
	 */
    @Autowired
    private DataSource datasource;

    @Value("${maxAuthMinutes:30}")
    private int maxAuthMinutes;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private AppUserStorage appUserStorage;


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(customAuthenticationProvider);
	}

	@Bean
	@Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	@Autowired
	protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
	    auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.addFilterBefore(new AuthenticationTokenProcessingFilter(tokenService(), appUserStorage), BasicAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(new AuthenticationEntryPoint() {
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                    throws IOException, ServletException {
                    PlainJsonBuilder.buildJsonError(response, RestExceptionCode.FC_RE_001, "Authorisierung erforderlich", HttpStatus.UNAUTHORIZED);
                }
            })
            .accessDeniedHandler(new AccessDeniedHandler() {
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
                    throws IOException, ServletException {
                    PlainJsonBuilder.buildJsonError(response, RestExceptionCode.FC_RE_001, "Zugriff verweigert", HttpStatus.FORBIDDEN);
                }
            })
            .and()
            .authorizeRequests()
            .antMatchers("/login").permitAll()
            .antMatchers("/version").permitAll()
            .antMatchers("/restoration/approve/**").hasRole("ADMIN")
            .antMatchers("/restoration/abandon/**").hasRole("ADMIN")
            .anyRequest().hasRole("USER");
	}

	@Bean
	public TokenService tokenService() {
	    int[] keyInts = new int[] {3, 35, 53, 75, 43, 15, 165, 188, 131, 126, 6, 101, 119, 123, 166, 143, 90, 179, 40, 230, 240, 84, 201, 40, 169, 15, 132, 178, 210, 80, 46, 191, 211, 251, 90, 146, 210, 6, 71, 239, 150, 138, 180, 195, 119, 98, 61, 34, 61, 46, 33, 114, 5, 46, 79, 8, 192, 205, 154, 245, 103, 208, 128, 163};
	    byte[] HMAC_KEY = new byte[keyInts.length];
	    for (int i=0; i < keyInts.length; i++) {
	        HMAC_KEY[i] = (byte)keyInts[i];
	    }
	    MacSigner hmac = new MacSigner(HMAC_KEY);

	    return new TokenService(hmac, hmac, maxAuthMinutes);
	}
}