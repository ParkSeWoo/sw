package com.nhis.comm.context.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sewoo on 2017. 1. 13..
 *
 * 보안 관련 설정 정보를 표현합니다
 */
@Data
@ConfigurationProperties(prefix = "extension.security")
public class SecurityProperties {
	/** Spring Security 따라 인증 / 허가 설정 정보 */
	private SecurityAuthProperties auth = new SecurityAuthProperties();
	/** CORS 설정정보 */
	private SecurityCorsProperties cors = new SecurityCorsProperties();

	public SecurityAuthProperties auth() {
		return auth;
	}

	public SecurityCorsProperties cors() {
		return cors;
	}

	/** Spring Security 대한 고급 설정 정보  */
	@Data
	public static class SecurityAuthProperties {
		/** 요청시 로그인 ID를 취득하는 키 */
		private String loginKey = "loginId";
		/** 요청시 암호를 가져 오기 키 */
		private String passwordKey = "password";
		/** 인증 대상 경로 */
		private String[] path = new String[] { "/api/**" };
		/** 인증 대상 경로 (관리자 용) */
		private String[] pathAdmin = new String[] { "/api/admin/**" };
		/** 인증 제외 경로 (인증 대상에서 제외) */
		private String[] excludesPath = new String[] { "/api/system/job/**" };
		/** 인증 무시 경로 (필터 미적용 인증되지 않은 고려 정적 자원 등) */
		private String[] ignorePath = new String[] { "/css/**", "/js/**", "/img/**", "/**/favicon.ico" };
		/** 로그인 API 경로 */
		private String loginPath = "/api/login";
		/** 로그 아웃 API 경로 */
		private String logoutPath = "/api/logout";
		/** 한 명이 동시 사용 가능한 최대 세션 수 */
		private int maximumSessions = 2;
		/**
		 * 사원 용 모드의 경우는 true.
		 * <p> 로그인 경로는 동일하지만, 로그인 처리의 취급이 바뀝니다.
		 * <ul>
		 * <li>true: SecurityUserService
		 * <li>false: SecurityAdminService
		 * </ul>
		 */
		private boolean admin = false;
		/** 인증이 유효한 경우는 true */
		private boolean enabled = true;
	}

	/** CORS 설정 정보를 표현합니다. CORS : HTTP 접근제어 */
	@Data
	public static class SecurityCorsProperties {
		private boolean allowCredentials = true;
		private String allowedOrigin = "*";
		private String allowedHeader = "*";
		private String allowedMethod = "*";
		private long maxAge = 3600L;
		private String path = "/**";
	}

}
