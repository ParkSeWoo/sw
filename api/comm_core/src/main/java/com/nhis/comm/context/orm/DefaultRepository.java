package com.nhis.comm.context.orm;

import javax.persistence.*;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.*;

import lombok.*;
import org.springframework.stereotype.Service;


/**
 * Created by sewoo on 2016. 12. 28..
 *
 * 표준 스키마 Repository를 표현합니다.
 */
@Setter
public class DefaultRepository extends OrmRepository {

	public static final String BeanNameDs = "dataSource";
	public static final String BeanNameEmf = "entityManagerFactory";
	public static final String BeanNameTx = "transactionManager";

	@PersistenceContext(unitName = BeanNameEmf)
	private EntityManager em;

	@Override
	public EntityManager em() {
		return em;
	}

	/** 표준 스키마 DataSource를 생성합니다. */
	@ConfigurationProperties(prefix = "extension.datasource.default")
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class DefaultDataSourceProperties extends OrmDataSourceProperties {
		private OrmRepositoryProperties jpa = new OrmRepositoryProperties();

		public DataSource dataSource() {
			return super.dataSource();
		}

		public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
				final DataSource dataSource) {
			return jpa.entityManagerFactoryBean(BeanNameEmf, dataSource);
		}

		public JpaTransactionManager transactionManager(final EntityManagerFactory emf) {
			return jpa.transactionManager(emf);
		}
	}

}
