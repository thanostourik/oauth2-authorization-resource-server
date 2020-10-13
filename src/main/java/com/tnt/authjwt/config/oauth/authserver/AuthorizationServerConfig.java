package com.tnt.authjwt.config.oauth.authserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;
import java.util.Arrays;

@Configuration
@Import(AuthorizationServerEndpointsConfiguration.class)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${authjwt.oauth.client.web.client-id}")
	private String clientId;

	@Value("${authjwt.oauth.client.web.client-secret}")
	private String clientSecret;

	@Value("${authjwt.oauth.authorization-server.access-token-validity}")
	private int accessTokenValiditySeconds;

	@Value("${authjwt.oauth.authorization-server.refresh-token-validity}")
	private int refreshTokenValiditySeconds;

	private final KeyPair keyPair;
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public AuthorizationServerConfig(
			KeyPair keyPair,
			PasswordEncoder passwordEncoder,
			UserDetailsService userDetailsService,
			AuthenticationConfiguration authenticationConfiguration
	) throws Exception {
		this.keyPair = keyPair;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
				.inMemory()
				.withClient(clientId)
				.secret(passwordEncoder.encode(clientSecret))
				.scopes("any")
				.autoApprove(true)
				.authorizedGrantTypes("password", "refresh_token")
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security
				.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

		endpoints
				.tokenStore(tokenStore())
				.tokenEnhancer(tokenEnhancerChain)
				.userDetailsService(this.userDetailsService)
				.authenticationManager(this.authenticationManager)
				.exceptionTranslator(loggingExceptionTranslator());
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setKeyPair(this.keyPair);
		return converter;
	}

	@Bean
	@Primary
	public TokenEndpoint tokenEndpoint(AuthorizationServerEndpointsConfiguration conf) {
		TokenEndpoint tokenEndpoint = new CustomTokenEndpoint();
		tokenEndpoint.setClientDetailsService(conf.getEndpointsConfigurer().getClientDetailsService());
		tokenEndpoint.setProviderExceptionHandler(conf.getEndpointsConfigurer().getExceptionTranslator());
		tokenEndpoint.setTokenGranter(conf.getEndpointsConfigurer().getTokenGranter());
		tokenEndpoint.setOAuth2RequestFactory(conf.getEndpointsConfigurer().getOAuth2RequestFactory());
		tokenEndpoint.setOAuth2RequestValidator(conf.getEndpointsConfigurer().getOAuth2RequestValidator());
		tokenEndpoint.setAllowedRequestMethods(conf.getEndpointsConfigurer().getAllowedTokenEndpointRequestMethods());
		return tokenEndpoint;
	}

	/**
	 * For debuging purposes
	 *
	 * @return
	 */
	@Bean
	public WebResponseExceptionTranslator loggingExceptionTranslator() {
		return new DefaultWebResponseExceptionTranslator() {
			@Override
			public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
				// This is the line that prints the stack trace to the log. You can customise this to format the trace etc if you like
				e.printStackTrace();

				// Carry on handling the exception
				ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
				HttpHeaders headers = new HttpHeaders();
				headers.setAll(responseEntity.getHeaders().toSingleValueMap());
				OAuth2Exception excBody = responseEntity.getBody();
				return new ResponseEntity<>(excBody, headers, responseEntity.getStatusCode());
			}
		};
	}
}
