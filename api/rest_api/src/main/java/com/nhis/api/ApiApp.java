package com.nhis.api;

import com.nhis.comm.context.enums.PathEnum;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by sewoo on 2016. 12. 16..
 * <p>ApiApp class는 Spring Boot가 제공하는 임베디드 Tomcat8.5 에서
 * 응용프로그램이 시작 됩니다.
 * <p> 개발서버는 tomcat이 아닌 weblogic12C로 동작 될 예정입니다.</p>
 * </p>
 */

@SpringBootApplication(scanBasePackages={""}) //scanBasePackageClasses
public class ApiApp {
	public static void main(String[] args) {
		new SpringApplicationBuilder(ApiApp.class)
				.profiles("app")
				.run(args);
	}
}
