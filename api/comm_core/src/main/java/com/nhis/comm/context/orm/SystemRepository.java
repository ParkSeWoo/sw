package com.nhis.comm.context.orm;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Created by sewoo on 2016. 12. 28..
 */
public class SystemRepository extends OrmRepository {

	public static final String BeanNameDs = "systemDataSource";
	public static final String BeanNameEmf = "systemEntityManagerFactory";
	public static final String BeanNameTx = "systemTransactionManager";

	@PersistenceContext(unitName = BeanNameEmf)
	private EntityManager em;

	@Override
	public EntityManager em() {
		return em;
	}

	/** 시스템 스키마 DataSource를 생성합니다. */
	@ConfigurationProperties(prefix = "extension.datasource.system")
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class SystemDataSourceProperties extends OrmDataSourceProperties {
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
