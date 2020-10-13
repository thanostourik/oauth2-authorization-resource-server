package com.tnt.oauth2jwt.service;

import com.tnt.oauth2jwt.model.AuthUser;
import com.tnt.oauth2jwt.repo.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserService {

	private final AuthUserRepository repo;

	@Autowired
	public AuthUserService(AuthUserRepository repo) {
		this.repo = repo;
	}

	public Optional<AuthUser> findByUsername(String username) {
		return repo.findOneByUsername(username);
	}
}
