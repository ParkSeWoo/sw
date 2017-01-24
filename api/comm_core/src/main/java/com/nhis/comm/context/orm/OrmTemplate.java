package com.nhis.comm.context.orm;

import com.nhis.comm.ValidationException;
import com.nhis.comm.ValidationException.ErrorKeys;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by sewoo on 2016. 12. 28..
 * JPA의 EntityManager에 대한 빠른 접근. (세션 당 생성하여 사용하십시오)
 * <p> EntityManager의 방법으로 이용하고 싶은 작업이 있으면 필요에 따라 랩 메소드를 추가하십시오.
 */
public class OrmTemplate {

		private final EntityManager em;
		private final Optional<OrmQueryMetadata> metadata;

		public OrmTemplate(EntityManager em) {
			this.em = em;
			this.metadata = Optional.empty();
		}

		public OrmTemplate(EntityManager em, OrmQueryMetadata metadata) {
			this.em = em;
			this.metadata = Optional.ofNullable(metadata);
		}

		private <T> TypedQuery<T> query(final CriteriaQuery<T> query) {
			TypedQuery<T> q = em.createQuery(query);
			metadata.ifPresent(meta -> {
				meta.hints().forEach((k, v) -> q.setHint(k, v));
				meta.lockMode().ifPresent(l -> q.setLockMode(l));
			});
			return q;
		}

		/** 지정된 엔터티의 ID 값을 가져옵니다. */
		@SuppressWarnings("unchecked")
		public <T> Serializable idValue(T entity) {
			return ((JpaEntityInformation<T, Serializable>)OrmUtils.entityInformation(em, entity.getClass())).getId(entity);
		}

		/** Criteria에서 한 건 가져옵니다. */
		public <T> Optional<T> get(final CriteriaQuery<T> criteria) {
			return find(criteria).stream().findFirst();
		}

		/** Criteria에서 한 건 가져옵니다. (존재하지 않을 때는 ValidationException) */
		public <T> T load(final CriteriaQuery<T> criteria) {
			return get(criteria).orElseThrow(() -> new ValidationException(ErrorKeys.EntityNotFound));
		}

		/**
		 * Criteria에서 검색합니다.
		 * ※ 임의의 조건 검색 등 가변 조건 검색이 필요할 때 이용하세요
		 */
		public <T> List<T> find(final CriteriaQuery<T> criteria) {
			return query(criteria).getResultList();
		}

		/**
		 * Criteria에서 페이징 검색합니다.
		 * <p> Pagination에 설정된 검색 조건은 무시됩니다. CriteriaQuery 구축시에 설정하도록하십시오.
		 */
		public <T> PagingList<T> find(final CriteriaQuery<T> criteria, Optional<CriteriaQuery<Long>> criteriaCount, final Pagination page) {
			Assert.notNull(page);
			long total = criteriaCount.map(cnt -> query(cnt).getResultList().get(0)).orElse(-1L);
			if (total == 0) return new PagingList<>(new ArrayList<>(), new Pagination(page, 0));

			TypedQuery<T> query = query(criteria);
			if (0 < page.getPage()) query.setFirstResult(page.getFirstResult());
			if (0 < page.getSize()) query.setMaxResults(page.getSize());
			return new PagingList<T>(query.getResultList(), new Pagination(page, total));
		}


		/**
		 * Criteria에서 한 건 가져옵니다.
		 * <p> 클로저 반환 값은 인수에 취하는 OrmCriteria의 result * 실행 결과를 반환합니다.
		 */
		public <T> Optional<T> get(Class<T> entityClass, Function<OrmCriteria<T>, CriteriaQuery<T>> func) {
			return get(func.apply(OrmCriteria.of(em, entityClass)));
		}

		public <T> Optional<T> get(Class<T> entityClass, String alias, Function<OrmCriteria<T>, CriteriaQuery<T>> func) {
			return get(func.apply(OrmCriteria.of(em, entityClass, alias)));
		}

		/**
		 * Criteria에서 한 건 가져옵니다. (존재하지 않을 때는 ValidationException)
		 * <p> 클로저 반환 값은 인수에 취하는 OrmCriteria의 result * 실행 결과를 반환합니다.
		 */
		public <T> T load(Class<T> entityClass, Function<OrmCriteria<T>, CriteriaQuery<T>> func) {
			return load(func.apply(OrmCriteria.of(em, entityClass)));
		}

		public <T> T load(Class<T> entityClass, String alias, Function<OrmCriteria<T>, CriteriaQuery<T>> func) {
			return load(func.apply(OrmCriteria.of(em, entityClass, alias)));
		}

		/**
		 * Criteria에서 검색합니다.
		 * <p> 클로저 반환 값은 인수에 취하는 OrmCriteria의 result * 실행 결과를 반환합니다.
		 */
		public <T> List<T> find(Class<T> entityClass, Function<OrmCriteria<T>, CriteriaQuery<T>> func) {
			return find(func.apply(OrmCriteria.of(em, entityClass)));
		}

		public <T> List<T> find(Class<T> entityClass, String alias, Function<OrmCriteria<T>, CriteriaQuery<T>> func) {
			return find(func.apply(OrmCriteria.of(em, entityClass, alias)));
		}

		/**
		 * Criteria에서 페이징 검색합니다.
		 * <p> Pagination에 설정된 검색 조건은 무시됩니다. OrmCriteria 구축시에 설정하도록하십시오.
		 */
		public <T> PagingList<T> find(Class<T> entityClass, Function<OrmCriteria<T>, OrmCriteria<T>> func,
									  final Pagination page) {
			OrmCriteria<T> criteria = OrmCriteria.of(em, entityClass);
			func.apply(criteria);
			return find(criteria.result(), page.isIgnoreTotal() ? Optional.empty() : Optional.of(criteria.resultCount()), page);
		}

		public <T> PagingList<T> find(Class<T> entityClass, String alias, Function<OrmCriteria<T>, OrmCriteria<T>> func,
									  final Pagination page) {
			OrmCriteria<T> criteria = OrmCriteria.of(em, entityClass, alias);
			func.apply(criteria);
			return find(criteria.result(), page.isIgnoreTotal() ? Optional.empty() : Optional.of(criteria.resultCount()), page);
		}

		/**
		 * Criteria에서 페이징 검색합니다.
		 * <p> CriteriaQuery가 제공하는 subquery 나 groupBy 등의 구문을 이용하고 싶을 때는 이쪽의 extension으로 지정하십시오.
		 * <p> Pagination에 설정된 검색 조건은 무시됩니다. OrmCriteria 구축시에 설정하도록하십시오.
		 */
		public <T> PagingList<T> find(Class<T> entityClass, Function<OrmCriteria<T>, OrmCriteria<T>> func,
									  Function<CriteriaQuery<?>, CriteriaQuery<?>> extension, final Pagination page) {
			OrmCriteria<T> criteria = OrmCriteria.of(em, entityClass);
			func.apply(criteria);
			return find(criteria.result(extension), page.isIgnoreTotal() ? Optional.empty() : Optional.of(criteria.resultCount(extension)), page);
		}

		public <T> PagingList<T> find(Class<T> entityClass, String alias, Function<OrmCriteria<T>, OrmCriteria<T>> func,
									  Function<CriteriaQuery<?>, CriteriaQuery<?>> extension, final Pagination page) {
			OrmCriteria<T> criteria = OrmCriteria.of(em, entityClass, alias);
			func.apply(criteria);
			return find(criteria.result(extension), page.isIgnoreTotal() ? Optional.empty() : Optional.of(criteria.resultCount(extension)), page);
		}

		/**
		 * JPQL에서 한 건 가져옵니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public <T> Optional<T> get(final String qlString, final Object... args) {
			List<T> list = find(qlString, args);
			return list.stream().findFirst();
		}

		/**
		 * JPQL에서 한 건 가져옵니다. (존재하지 않을 때는 ValidationException)
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public <T> T load(final String qlString, final Object... args) {
			Optional<T> v = get(qlString, args);
			return v.orElseThrow(() -> new ValidationException(ErrorKeys.EntityNotFound));
		}

		/** 대상 Entity를 모두 가져옵니다. */
		public <T> List<T> loadAll(final Class<T> entityClass) {
			return find(OrmCriteria.of(em, entityClass).result());
		}

		/**
		 * JPQL에서 검색합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> List<T> find(final String qlString, final Object... args) {
			return bindArgs(em.createQuery(qlString), args).getResultList();
		}

		/**
		 * JPQL에서 페이징 검색합니다.
		 * <p> 카운트 절이 잘 구축되지 않는 경우는 Pagination # ignoreTotal을 true로하여
		 * 별도 일반 검색에서 총 건수를 산출하도록하십시오.
		 * <p> page에 설정 된 정렬 조건은 무시되므로 qlString 구축시에 명시 적으로 설정하십시오.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> PagingList<T> find(final String qlString, final Pagination page, final Object... args) {
			long total = page.isIgnoreTotal() ? -1L : load(QueryUtils.createCountQueryFor(qlString), args);
			List<T> list = bindArgs(em.createQuery(qlString), args, page).getResultList();
			return new PagingList<>(list, new Pagination(page, total));
		}

		/**
		 * 정의 된 JPQL에서 한 건 가져옵니다.
		 * <p> 사전 name에 맞는 @NamedQuery 정의가 필요합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public <T> Optional<T> getNamed(final String name, final Object... args) {
			List<T> list = findNamed(name, args);
			return list.stream().findFirst();
		}

		/**
		 * 정의 된 JPQL에서 한 건 취득을합니다. (존재하지 않을 때는 ValidationException)
		 * <p> 사전 name에 맞는 @NamedQuery 정의가 필요합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public <T> T loadNamed(final String name, final Object... args) {
			Optional<T> v = getNamed(name, args);
			return v.orElseThrow(() -> new ValidationException(ErrorKeys.EntityNotFound));
		}

		/**
		 * 정의 된 JPQL에서 검색합니다.
		 * <p> 사전 name에 맞는 @NamedQuery 정의가 필요합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> List<T> findNamed(final String name, final Object... args) {
			return bindArgs(em.createNamedQuery(name), args).getResultList();
		}

		/**
		 * 정의 된 JPQL에서 페이징 검색합니다.
		 * <p> 사전 name에 맞는 @NamedQuery 정의가 필요합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 * <p> page에 설정 된 정렬 조건은 무시됩니다.
		 */
		@SuppressWarnings("unchecked")
		public <T> PagingList<T> findNamed(final String name, final String nameCount, final Pagination page, final Map<String, Object> args) {
			long total = page.isIgnoreTotal() ? -1L : loadNamed(nameCount, args);
			List<T> list = bindArgs(em.createNamedQuery(name), page, args).getResultList();
			return new PagingList<>(list, new Pagination(page, total));
		}

		/**
		 * SQL에서 검색합니다.
		 * <p> 검색 결과로 select 값 배열 목록을 반환합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> List<T> findBySql(final String sql, final Object... args) {
			return bindArgs(em.createNativeQuery(sql), args).getResultList();
		}

		/**
		 * SQL에서 검색합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> List<T> findBySql(String sql, Class<T> clazz, final Object... args) {
			return bindArgs(em.createNativeQuery(sql, clazz), args).getResultList();
		}

		/**
		 * SQL에서 페이징 검색합니다.
		 * <p> 검색 결과로 select 값 배열 목록을 반환합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> PagingList<T> findBySql(String sql, String sqlCount, final Pagination page, final Object... args) {
			long total = page.isIgnoreTotal() ? -1L : findBySql(sqlCount, args).stream().findFirst().map(v -> Long.parseLong(v.toString())).orElse(0L);
			return new PagingList<T>(bindArgs(em.createNativeQuery(sql), page, args).getResultList(), new Pagination(page, total));
		}

		/**
		 * SQL에서 페이징 검색합니다.
		 * <p> page에 설정 된 정렬 조건은 무시되므로 sql 구축시에 명시 적으로 설정하십시오.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		@SuppressWarnings("unchecked")
		public <T> PagingList<T> findBySql(String sql, String sqlCount, Class<T> clazz, final Pagination page, final Object... args) {
			long total = page.isIgnoreTotal() ? -1L : findBySql(sqlCount, args).stream().findFirst().map(v -> Long.parseLong(v.toString())).orElse(0L);
			return new PagingList<T>(bindArgs(em.createNativeQuery(sql, clazz), page, args).getResultList(), new Pagination(page, total));
		}

		/**
		 * JPQL을 실행합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public int execute(String qlString, final Object... args) {
			return bindArgs(em.createQuery(qlString), args).executeUpdate();
		}

		/**
		 * 정의 된 JPQL을 실행합니다.
		 * <p>사전에 name과 일치하는 @NamedQuery 정의가 필요합니다.
		 * <p>args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public int executeNamed(String name, final Object... args) {
			return bindArgs(em.createNamedQuery(name), args).executeUpdate();
		}

		/**
		 * SQL을 실행합니다.
		 * <p> args에 Map를 지정했을 때는 명명 된 인수로 취급합니다. (Map의 키는 문자열을 지정하십시오)
		 */
		public int executeSql(String sql, final Object... args) {
			return bindArgs(em.createNativeQuery(sql), args).executeUpdate();
		}

		/** 저장을 처리합니다. */
		public void callStoredProcedure(String procedureName, Consumer<StoredProcedureQuery> proc) {
			proc.accept((StoredProcedureQuery)bindArgs(em.createStoredProcedureQuery(procedureName)));
		}

		/**
		 * 쿼리에 값을 끈 지정합니다.
		 * <p> Map 지정시 키에 문자를 지정합니다. 그렇지는 자동으로 1 시작 위치 지정합니다.
		 */
		public Query bindArgs(final Query query, final Object... args) {
			return bindArgs(query, null, args);
		}

		public Query bindArgs(final Query query, final Pagination page, final Object... args) {
			Optional.ofNullable(page).ifPresent((pg) -> {
				if (page.getPage() > 0)
					query.setFirstResult(page.getFirstResult());
				if (page.getSize() > 0)
					query.setMaxResults(page.getSize());
			});
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					if (arg instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked")
						Map<String, Object> argNamed = (Map<String, Object>)arg;
						argNamed.forEach((k, v) -> query.setParameter(k, v));
					} else {
						query.setParameter(i + 1, args[i]);
					}
				}
			}
			return query;
		}

}
