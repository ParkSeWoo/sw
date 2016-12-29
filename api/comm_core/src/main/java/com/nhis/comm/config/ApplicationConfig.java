package com.nhis.comm.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by doldol on 2016. 12. 21..
 */

@Configuration
public class ApplicationConfig {

	/**
	 * spring mvc의 확장 구성
	 */
	@Configuration
	static class WebMvcConfig extends WebMvcConfigurerAdapter {
		@Autowired
		private MessageSource message;

		/** Hibernate의 LazyLoading 회피 대응。  see JacksonAutoConfiguration */
		@Bean
		Hibernate5Module jsonHibernate5Module() {
			return new Hibernate5Module();
		}

		/** BeanValidation 메시지 UTF-8을 지원하는 Validator。 */
		@Bean
		LocalValidatorFactoryBean validator() {
			LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
			factory.setValidationMessageSource(message);
			return factory;
		}

		/** 표준 Validator의 교환을 합니다。 */
		@Override
		public org.springframework.validation.Validator getValidator() {
			return validator();
		}

	}


}
