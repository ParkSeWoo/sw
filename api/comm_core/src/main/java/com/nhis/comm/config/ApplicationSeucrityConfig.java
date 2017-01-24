package com.nhis.comm.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.nhis.comm.context.security.SecurityProperties;

/**
 * 응용 프로그램의 보안 정의를 표현합니다.
 */
@Configuration
@EnableConfigurationProperties({ SecurityProperties.class })
public class ApplicationSeucrityConfig {
    
    /** 암호의 해시 (BCrypt) 인코더. */
    @Bean
    PasswordEncoder passwordEncoder() {
        //low : 제대로하는거야이면 strength와 SecureRandom 사용 등 외부 잘라 포함하여 고려하십시오
        return new BCryptPasswordEncoder();
    }

    /** CORS 전체 적용 */
    @Bean
    @ConditionalOnProperty(prefix = "extension.security.cors", name = "enabled", matchIfMissing = false)
    CorsFilter corsFilter(SecurityProperties props) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(props.cors().isAllowCredentials());
        config.addAllowedOrigin(props.cors().getAllowedOrigin());
        config.addAllowedHeader(props.cors().getAllowedHeader());
        config.addAllowedMethod(props.cors().getAllowedMethod());
        config.setMaxAge(props.cors().getMaxAge());
        source.registerCorsConfiguration(props.cors().getPath(), config);
        return new CorsFilter(source);
    }
}
