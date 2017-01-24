package com.nhis.oauth2.config;

import com.nhis.comm.config.OAuth2APIConfig;
import com.nhis.comm.config.OAuth2ServerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by sewoo on 2017. 1. 6..
 */


@Configuration
@Import({OAuth2ServerConfig.class})
public class OAuth2AppConfig {


}