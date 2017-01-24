package com.nhis.comm.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Created by sewoo on 2017. 1. 4..
 *
 * OAuth2 관련 환경 설정
 * ResourceServerConfigurerAdapter :
 */

@Configuration
@EnableResourceServer
public class OAuth2APIConfig extends ResourceServerConfigurerAdapter {


	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.headers().frameOptions().disable(); // spring security iframe 접근못하는 문제 해결

		http.authorizeRequests().antMatchers("/members/","/members/**").access("#oauth2.hasScope('read')")
				.anyRequest().authenticated();

	}

	/*
	* 참고 사이트     : https://brunch.co.kr/@sbcoba/6
	* 참고 프로그램소스 : https://github.com/sbcoba/spring-boot-oauth2-sample/tree/example6/api-server/src
	* */



}
