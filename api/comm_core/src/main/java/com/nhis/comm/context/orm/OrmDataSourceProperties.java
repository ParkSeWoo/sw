package com.nhis.comm.context.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by sewoo on 2016. 12. 28..
 *
 *  DataSource 생성에 대한 설정 클래스.
 * <p> 상속 중에도 @ConfigurationProperties 정의를하고 application.yml과 紐付하십시오.
 * <p> 기반 구현에 HikariCP를 이용하고 있습니다. 필요에 따라 설정 가능한 필드를 늘리도록하십시오.
 *
 */
@Data
public class OrmDataSourceProperties {

	/** 드라이버 클래스 이름 (미 설정시는 url로부터 자동 등록) */
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private Properties props = new Properties();

	/**  */
	private int minIdle = 1;
	/** 최소 연결 풀링 수 */
	private int maxPoolSize = 20;

	/** 연결 상태를 확인 할 때 true */
	private boolean validation = true;
	/** 연결 상태 확인 쿼리 (미 설정시하고 Database가 지원하는 때는 자동 설정) */
	private String validationQuery;

	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName());
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setMinimumIdle(minIdle);
		config.setMaximumPoolSize(maxPoolSize);
		if (validation) {
			config.setConnectionTestQuery(validationQuery());
		}
		config.setDataSourceProperties(props);
		return new HikariDataSource(config);
	}

	private String driverClassName() {
		if (StringUtils.hasText(driverClassName)) {
			return driverClassName;
		}
		return DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
	}

	private String validationQuery() {
		if (StringUtils.hasText(validationQuery)) {
			return validationQuery;
		}
		return DatabaseDriver.fromJdbcUrl(url).getValidationQuery();
	}

}
