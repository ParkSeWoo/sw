package com.nhis.oauth2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * Created by sewoo on 2017. 1. 6..
 * rest_oauth2 프로젝트가 시작퇴는 class 입니다
 */

@SpringBootApplication
public class OAuth2App {

	public static void main(String[] args) {
		new SpringApplicationBuilder(OAuth2App.class)
				.profiles("oauth2")
				.run(args);

	}
}