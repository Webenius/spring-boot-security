package com.sambi.app.rest.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sambi.app.domain.AuthorityEntity;
import com.sambi.app.repositories.AuthorityRepository;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	private AuthorityRepository authorityRepository;

    public List<AuthorityEntity> authorize(String username, String password) {

    	return authorityRepository.findBy(username, password);
    }
}
