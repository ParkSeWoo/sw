package com.nhis.comm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Created by sewoo on 2017. 1. 6..
 */

@Configuration
public class OAuth2ServerConfig  {

	@Configuration
	@RequiredArgsConstructor
	@EnableAuthorizationServer
	public static class JwtOAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		private final AuthenticationManager authenticationManager;

		@Value("${resource.id:spring-boot-application}")
		private String resourceId;

		@Value("${access_token.validity_period:3600}")
		private int accessTokenValiditySeconds = 3600;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.accessTokenConverter(jwtAccessTokenConverter())
					.authenticationManager(this.authenticationManager);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory()
					.withClient("bar")
					.authorizedGrantTypes("password")
					.authorities("ROLE_USER")
					.scopes("read", "write")
					.resourceIds(resourceId)
					.accessTokenValiditySeconds(accessTokenValiditySeconds)
					.secret("foo");
		}

		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			return new JwtAccessTokenConverter();
		}
	}

}







