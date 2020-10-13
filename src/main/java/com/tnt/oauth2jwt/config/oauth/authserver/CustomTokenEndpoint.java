package com.tnt.oauth2jwt.config.oauth.authserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequestMapping("/oauth/token")
public class CustomTokenEndpoint extends TokenEndpoint {

	/**
	 * Get access token endpoint
	 * @param principal Current user
	 * @param parameters The request parameters
	 * @param request The servlet request
	 * @param response The servlet resposne
	 * @return The get access token response
	 * @throws HttpRequestMethodNotSupportedException Throws if other method but POST is used
	 */
	@PostMapping
	public ResponseEntity<?> postAccessToken(
			Principal principal,
			@RequestParam Map<String, String> parameters,
			HttpServletRequest request,
			HttpServletResponse response
	) throws HttpRequestMethodNotSupportedException {

		// If grant type is refresh_token then set the refresh token from the cookie to the parameters
		if ( AuthUtils.REFRESH_TOKEN.equals(parameters.get("grant_type")) ) {
			Cookie[] cookies = request.getCookies();
			Optional<Cookie> refreshCookie = Optional.ofNullable(cookies)
					.stream()
					.flatMap(Arrays::stream)
					.filter(cookie -> AuthUtils.REFRESH_TOKEN.equals(cookie.getName()))
					.findFirst();
			if ( !refreshCookie.isPresent() ) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
			}
			refreshCookie.ifPresent(cookie -> parameters.put(cookie.getName(), cookie.getValue()));
		}

		// Call super for default actions
		ResponseEntity<OAuth2AccessToken> responseEntity = super.postAccessToken(principal, parameters);

		// Create the refresh token cookie
		AuthUtils.createRefreshTokenCookie(Objects.requireNonNull(responseEntity.getBody()).getRefreshToken().getValue(), response);

		return responseEntity;
	}

	/**
	 * On logout remove the refresh token cookie
	 * @param response The servlet response
	 * @return The logout response
	 */
	@PostMapping("/revoke")
	public ResponseEntity<Object> logout(HttpServletResponse response) {
		AuthUtils.removeRefreshTokenCookie(response);
		return ResponseEntity.ok("");
	}
}
