package com.tnt.oauth2jwt.service;

import com.tnt.oauth2jwt.model.SomeResource;
import com.tnt.oauth2jwt.repo.SomeResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SomeResourceService {

	private final SomeResourceRepository repo;

	@Autowired
	public SomeResourceService(SomeResourceRepository repo) {
		this.repo = repo;
	}

	public Iterable<SomeResource> get() {
		return repo.findAll();
	}

}
