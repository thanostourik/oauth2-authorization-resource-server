package com.tnt.oauth2jwt.config.oauth.authserver;

import com.tnt.oauth2jwt.model.AuthUser;
import com.tnt.oauth2jwt.service.AuthUserService;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Primary
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final AuthUserService authUserService;

	@Autowired
	public CustomUserDetailsService(AuthUserService authUserService) {
		this.authUserService = authUserService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<AuthUser> user = authUserService.findByUsername(username);
		if ( user.isEmpty() ) {
			throw new UsernameNotFoundException("User with email: " + username + " not found!");
		}

		return user.get();
	}

	public AuthUser getAuthUser(String user) {
		return null;
	}

	public AuthUser getAuthUser(JwtAuthenticationToken principal) {

		Map<String, Object> claims = principal.getTokenAttributes();
		Long id = Long.valueOf(claims.get("id").toString());
		String email = claims.get("user_name").toString();

		Collection<GrantedAuthority> authorities = Stream.ofNullable((JSONArray) claims.get("authorities"))
				.map(Object::toString)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());

		return new AuthUser(id, email, authorities);
	}
}
