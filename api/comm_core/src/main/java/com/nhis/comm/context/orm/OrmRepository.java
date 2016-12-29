package com.nhis.comm.context.orm;

import com.nhis.comm.ValidationException;
import com.nhis.comm.context.DomainHelper;
import com.nhis.comm.context.Entity;
import com.nhis.comm.context.Repository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 28..
 * JPA (Hibernate)의 Repository 기본 구현.
 * <p> 본 컴포넌트는 Repository 및 Entity의 1-n 관계를 실현하기 위해 SpringData의 기반을
 * 사용하지 않는 형태로 간단한 ORM 구현을 제공합니다.
 * <p> OrmRepository를 상속하여 생성되는 Repository의 입도는 데이터 소스 단위입니다.
 */

@Setter
public abstract class OrmRepository implements Repository {

	@Autowired
	private DomainHelper dh;
	@Autowired(required = false)
	private OrmInterceptor interceptor;

	public abstract EntityManager em();

	/** {@inheritDoc} */
	@Override
	public DomainHelper dh() {
		return dh;
	}

	protected Optional<OrmInterceptor> interceptor() {
		return Optional.ofNullable(interceptor);
	}

	/**
	 * ORM 작업 간이 접근자를 생성합니다.
	 * <p> OrmTemplate 외침 때마다 생성됩니다.
	 */
	public OrmTemplate tmpl() {
		return new OrmTemplate(em());
	}

	public OrmTemplate tmpl(OrmQueryMetadata metadata) {
		return new OrmTemplate(em(), metadata);
	}

	/** 지정한 Entity 클래스를 축으로 한 Criteria를 생성합니다。 */
	public <T extends Entity> OrmCriteria<T> criteria(Class<T> clazz) {
		return OrmCriteria.of(em(), clazz);
	}

	/** 지정한 Entity 클래스에 별칭을 끈 붙인 Criteria를 생성합니다. */
	public <T extends Entity> OrmCriteria<T> criteria(Class<T> clazz, String alias) {
		return OrmCriteria.of(em(), clazz, alias);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> Optional<T> get(Class<T> clazz, Serializable id) {
		T m = em().find(clazz, id);
		if (m != null) m.hashCode(); // force loading
		return Optional.ofNullable(m);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> T load(Class<T> clazz, Serializable id) {
		try {
			T m = em().getReference(clazz, id);
			m.hashCode(); // force loading
			return m;
		} catch (EntityNotFoundException e) {
			throw new ValidationException(ValidationException.ErrorKeys.EntityNotFound);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> T loadForUpdate(Class<T> clazz, Serializable id) {
		T m = em().find(clazz, id, LockModeType.PESSIMISTIC_WRITE);
		if (m == null) throw new ValidationException(ValidationException.ErrorKeys.EntityNotFound);
		m.hashCode(); // force loading
		return m;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> boolean exists(Class<T> clazz, Serializable id) {
		return get(clazz, id).isPresent();
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> List<T> findAll(Class<T> clazz) {
		return tmpl().loadAll(clazz);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> T save(T entity) {
		interceptor().ifPresent(i -> i.touchForCreate(entity));
		em().persist(entity);
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> T saveOrUpdate(T entity) {
		interceptor().ifPresent(i -> i.touchForUpdate(entity));
		return em().merge(entity);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> T update(T entity) {
		interceptor().ifPresent(i -> i.touchForUpdate(entity));
		return em().merge(entity);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Entity> T delete(T entity) {
		em().remove(entity);
		return entity;
	}

	/**
	 * 세션 캐시중인 영속화되지 않은 엔티티를 모두 DB 및 동기화 (SQL 발행)합니다.
	 * <p> SQL 발행 타이밍을 명확히하려는 곳에서 호출하도록하십시오. 일괄 처리 등에서 세션 캐시가
	 * 메모리를 핍박하는 경우에는 #flushAndClear을 정기적으로 호출 세션 캐시 비를 막도록하십시오.
	 */
	public OrmRepository flush() {
		em().flush();
		return this;
	}

	/**
	 * 세션 캐시중인 영속화되지 않은 엔티티를 DB와 동기화 한 후 세션 캐시를 초기화합니다.
	 * <p> 대량의 업데이트가 발생하는 일괄 처리 등에서 암묵적으로 유지되는 세션 캐시 메모리를 핍박하고
	 *     큰 문제를 일으키는 경우가 많이 볼 수 있습니다. 정기적으로 본 처리를 호출하여 세션 캐시
	 *     크기를 정량적으로 유지하도록하십시오.
	 */
	public OrmRepository flushAndClear() {
		em().flush();
		em().clear();
		return this;
	}

	/** JPA 구성 요소를 생성하기위한 설정 정보를 표현합니다. */
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class OrmRepositoryProperties extends JpaProperties {
		/** 스키마 끈 부착 대상으로하는 패키지. (annotatedClasses과 중 하나를 설정) */
		private String[] packageToScan;
		/** Entity로 등록하는 클래스. (packageToScan과 중 하나를 설정) */
		private Class<?>[] annotatedClasses;

		public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(String name, final DataSource dataSource) {
			EntityManagerFactoryBuilder emfBuilder = new EntityManagerFactoryBuilder(
					vendorAdapter(), getProperties(), null);
			EntityManagerFactoryBuilder.Builder builder = emfBuilder
					.dataSource(dataSource)
					.persistenceUnit(name)
					.properties(getHibernateProperties(dataSource))
					.jta(false);
			if (ArrayUtils.isNotEmpty(annotatedClasses)) {
				builder.packages(annotatedClasses);
			} else {
				builder.packages(packageToScan);
			}
			return builder.build();
		}

		private JpaVendorAdapter vendorAdapter() {
			AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
			adapter.setShowSql(isShowSql());
			adapter.setDatabase(getDatabase());
			adapter.setDatabasePlatform(getDatabasePlatform());
			adapter.setGenerateDdl(isGenerateDdl());
			return adapter;
		}

		public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
			return new JpaTransactionManager(emf);
		}


		public void setPackageToScan(String... packageToScan) {
			this.packageToScan = packageToScan;
		}

		public void setAnnotatedClasses(Class<?>... annotatedClasses) {
			this.annotatedClasses = annotatedClasses;
		}



	}

}
