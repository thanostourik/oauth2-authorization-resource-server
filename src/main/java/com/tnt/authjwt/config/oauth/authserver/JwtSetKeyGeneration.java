package com.tnt.authjwt.config.oauth.authserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
public class JwtSetKeyGeneration {

	@Value("${authjwt.oauth.authorization-server.rsa.alias}")
	private String rsaAlias;

	@Value("${authjwt.oauth.authorization-server.rsa.keystore}")
	private String rsaKeystore;

	@Value("${authjwt.oauth.authorization-server.rsa.password}")
	private String rsaPassword;

	@Bean
	public KeyPair keyPairBean() {
		Resource keystoreResource = new ClassPathResource(rsaKeystore);
		KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(keystoreResource, rsaPassword.toCharArray());
		return keyFactory.getKeyPair(rsaAlias);
	}
}
