package com.tnt.authjwt.service;

import com.tnt.authjwt.model.AuthUser;
import com.tnt.authjwt.repo.AuthUserRepository;
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
