package com.tnt.oauth2jwt.rest;

import com.tnt.oauth2jwt.config.oauth.resourceserver.CurrentUser;
import com.tnt.oauth2jwt.model.AuthUser;
import com.tnt.oauth2jwt.service.SomeResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/someresources")
public class SomeResourceRest {
	Logger logger = LoggerFactory.getLogger(SomeResourceRest.class);

	private final SomeResourceService someResourceService;

	@Autowired
	public SomeResourceRest(SomeResourceService someResourceService) {
		this.someResourceService = someResourceService;
	}

	@GetMapping
	public ResponseEntity<Object> get(@CurrentUser AuthUser user) {
		logger.info("Logged in user: {}", user.getUsername());
		return ResponseEntity.ok(someResourceService.get());
	}
}
