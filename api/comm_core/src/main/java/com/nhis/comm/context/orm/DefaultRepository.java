package com.nhis.comm.context.orm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

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
