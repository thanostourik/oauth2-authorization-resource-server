package com.tnt.oauth2jwt.config.oauth.authserver;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AuthUtils {

	public static final String REFRESH_TOKEN = "refresh_token";

	/**
	 * Private constructor to hide the public implicit one
	 */
	private AuthUtils() {}

	/**
	 * Set refresh token in a cookie
	 *
	 * @param refreshToken The refresh token
	 * @param response The servlet response in which to add the cookie
	 */
	public static void createRefreshTokenCookie(String refreshToken, HttpServletResponse response) {

		JsonParser objectMapper = JsonParserFactory.create();
		Jwt jwt = JwtHelper.decode(refreshToken);
		String claimsStr = jwt.getClaims();
		Map<String, Object> claims = objectMapper.parseMap(claimsStr);
		Integer expiration = (Integer) claims.get("exp");

		Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expiration);
		cookie.setPath("/oauth/token");
		response.addCookie(cookie);
	}

	/**
	 * Remove the refresh token cookie
	 * @param response The server response from which to remove the cookie
	 */
	public static void removeRefreshTokenCookie(HttpServletResponse response) {
		// Set the refresh_token cookie to the response with 0 expiration
		Cookie cookie = new Cookie(REFRESH_TOKEN, "");
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookie.setPath("/oauth/token");
		response.addCookie(cookie);
	}
}
