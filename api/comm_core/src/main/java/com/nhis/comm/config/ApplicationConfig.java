package com.nhis.comm.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.nhis.comm.context.AppSettingHandler;
import com.nhis.comm.context.DomainHelper;
import com.nhis.comm.context.ResourceBundleHandler;
import com.nhis.comm.context.Timestamper;
import com.nhis.comm.context.actor.ActorSession;
import com.nhis.comm.context.audit.AuditHandler;
import com.nhis.comm.context.lock.IdLockHandler;
import com.nhis.comm.controller.RestErrorAdvice;
import com.nhis.comm.controller.RestErrorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by doldol on 2016. 12. 21..
 */

@Configuration
@Import({ApplicationDbConfig.class, ApplicationSeucrityConfig.class})
public class ApplicationConfig {

	/**
	 * spring mvc의 확장 구조
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

	/** 인프라 층 (context 부하)의 구성 요소 정의를 표현합니다 */
	@Configuration
	static class PlainConfig {
		@Bean
		Timestamper timestamper() {
			return new Timestamper();
		}
		@Bean
		ActorSession actorSession() {
			return new ActorSession();
		}
		@Bean
		ResourceBundleHandler resourceBundleHandler() {
			return new ResourceBundleHandler();
		}
		@Bean
		AppSettingHandler appSettingHandler() {
			return new AppSettingHandler();
		}
		@Bean
		AuditHandler auditHandler() {
			return new AuditHandler();
		}
		@Bean
		AuditHandler.AuditPersister auditPersister() {
			return new AuditHandler.AuditPersister();
		}
		@Bean
		IdLockHandler idLockHandler() {
			return new IdLockHandler();
		}
		/*@Bean
		MailHandler mailHandler() {
			return new MailHandler();
		}
		@Bean
		ReportHandler reportHandler() {
			return new ReportHandler();
		}*/
		@Bean
		DomainHelper domainHelper() {
			return new DomainHelper();
		}
	}

	/** 예외 처리 API (json 형식) 지원  */
	@Configuration
	@Import({RestErrorAdvice.class, RestErrorController.class})
	static class ApiConfig {}


}
