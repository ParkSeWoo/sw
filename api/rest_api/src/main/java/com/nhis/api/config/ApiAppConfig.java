package com.nhis.api.config;

import com.nhis.comm.config.ApplicationConfig;
import com.nhis.comm.config.OAuth2APIConfig;
import com.nhis.comm.context.Timestamper;
import com.nhis.comm.context.actor.ActorSession;
import com.nhis.comm.context.orm.DefaultRepository;
import com.nhis.comm.context.rest.RestActorSessionBindFilter;
import com.nhis.comm.model.BusinessDayHandler;
import com.nhis.comm.model.BusinessDayHandler.HolidayAccessor;
import com.nhis.comm.model.DataFixtures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by sewoo on 2017. 1. 2..
 */
@Configuration
@Import({ApplicationConfig.class, OAuth2APIConfig.class})
public class ApiAppConfig {

	/** Domain층의 컨테이너 관리를 표현 합니다.。 */
	@Configuration
	static class DomainAutoConfig {

		/** 데이터 생성 유틸리티 */
		@Bean
		@ConditionalOnProperty(prefix = DataFixtures.Prefix, name = "enabled", matchIfMissing = false)
		DataFixtures fixtures() {
			return new DataFixtures();
		}

		/** 휴일 정보 접근 */
		@Bean
		HolidayAccessor holidayAccessor(DefaultRepository rep) {
			return new HolidayAccessor(rep);
		}

		/** 영업일 유틸리티 */
		@Bean
		BusinessDayHandler businessDayHandler(Timestamper time, HolidayAccessor holidayAccessor) {
			return new BusinessDayHandler(time, holidayAccessor);
		}
	}

	/** 프로세스 범위의 확장 정의를 표현합니다. */
	@Configuration
	static class ProcessAutoConfig {

		/**
		 * 요청에 이용자 정보가 설정되어 있던 때는 그냥 스레드 로컬에 끈 지어 있습니다
		 * <p>동기화 Servlet에서만 제대로 동작 합니다.
		 */
		@Bean
		public RestActorSessionBindFilter restActorSessionBindFilter() {
			return restActorSessionBindFilter();
		}

		/**
		 * 요청에 이용자 정보가 설정되어 있던 때는 그냥 스레드 로컬에 끈 지어 있습니다
		 * <p>동기화 Servlet에서만 제대로 동작 합니다.
		 */
		@Bean
		public RestActorSessionBindFilter restActorSessionBindFilter(ActorSession session) {
			return new RestActorSessionBindFilter(session);
		}

	}

	/** 확장 상태 점검 정의를 표현합니다. */
	@Configuration
	static class HealthCheckAuthConfig {
		/** 영업일 체크 */
		@Bean
		HealthIndicator dayIndicator(final Timestamper time, final BusinessDayHandler day) {
			return new AbstractHealthIndicator() {
				@Override
				protected void doHealthCheck(Health.Builder builder) throws Exception {
					builder.up();
					builder.withDetail("day", day.day())
							.withDetail("dayMinus1", day.day(-1))
							.withDetail("dayPlus1", day.day(1))
							.withDetail("dayPlus2", day.day(2))
							.withDetail("dayPlus3", day.day(3));
				}
			};
		}
	}


}