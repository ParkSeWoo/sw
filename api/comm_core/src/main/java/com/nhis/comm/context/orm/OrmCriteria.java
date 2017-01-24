package com.nhis.comm.context.orm;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Metamodel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by sewoo on 2016. 12. 28..
 * ORM의 가변 조건을 취급 CriteriaBuilder 래퍼.
 * <p> Criteria의 간이적인 취급을 가능하게합니다.
 * <p> Criteria에 이용하는 조건 절은 필요에 따라 추가하십시오.
 * <p> 빌드 결과로 CriteriaQuery는 result * 메소드에서 받아 가세요.
 *
 */
public class OrmCriteria<T> {

	public static final String DefaultAlias = "m";

	private final Class<T> clazz;
	private final String alias;
	private final Metamodel metamodel;
	private final CriteriaBuilder builder;
	private final CriteriaQuery<T> query;
	private final Root<T> root;
	private final Set<Predicate> predicates = new LinkedHashSet<>();
	private final Set<Order> orders = new LinkedHashSet<>();

	/** 지정한 Entity 클래스에 별칭을 끈 붙인 Criteria를 생성합니다. */
	private OrmCriteria(EntityManager em, Class<T> clazz, String alias) {
		this.clazz = clazz;
		this.metamodel = em.getMetamodel();
		this.builder = em.getCriteriaBuilder();
		this.query = builder.createQuery(clazz);
		this.root = query.from(clazz);
		this.alias = alias;
		this.root.alias(alias);
	}

	/** 내부에 보유하는 엔티티 클래스를 돌려줍니다. */
	public Class<T> entityClass() {
		return clazz;
	}

	/** 엔티티의 메타 정보를 반환합니다. */
	public Metamodel metamodel() {
		return metamodel;
	}

	/** 내부에 보유 CriteriaBuilder을 반환합니다. */
	public CriteriaBuilder builder() {
		return builder;
	}

	/** 내부에 보유 Root를 반환합니다. */
	public Root<T> root() {
		return root;
	}

	/**
	 * 연결합니다.
	 * <p> 인수는 Join 가능한 필드 (@ManyToOne 등)을 지정하십시오.
	 * <p> Join 요소는 호출자에서 유지하고 필요에 따라 이용하십시오.
	 */
	public <Y> Join<T, Y> join(String associationPath) {
		return root.join(associationPath);
	}

	public <Y> Join<T, Y> join(String associationPath, String alias) {
		Join<T, Y> v = join(associationPath);
		v.alias(alias);
		return v;
	}

	/**
	 * 조립 한 CriteriaQuery를 반환합니다.
	 * <p> 복잡한 쿼리와 집계 함수는이 메소드에서 반환 된 query를 바탕으로 추가 구축하십시오.
	 */
	public CriteriaQuery<T> result() {
		return result(q -> q);
	}
	@SuppressWarnings("unchecked")
	public CriteriaQuery<T> result(Function<CriteriaQuery<?>, CriteriaQuery<?>> extension) {
		CriteriaQuery<T> q = query.where(predicates.toArray(new Predicate[0]));
		q = (CriteriaQuery<T>)extension.apply(q);
		return orders.isEmpty() ? q : q.orderBy(orders.toArray(new Order[0]));
	}

	public CriteriaQuery<Long> resultCount() {
		return resultCount(q -> q);
	}
	@SuppressWarnings("unchecked")
	public CriteriaQuery<Long> resultCount(Function<CriteriaQuery<?>, CriteriaQuery<?>> extension) {
		CriteriaQuery<Long> q = builder.createQuery(Long.class);
		q.from(clazz).alias(alias);
		q.where(predicates.toArray(new Predicate[0]));
		if (q.isDistinct()) {
			q.select(builder.countDistinct(root));
		} else {
			q.select(builder.count(root));
		}
		return (CriteriaQuery<Long>)extension.apply(q);
	}

	/**
	 * 조건절 (or 조건 포함)를 추가합니다.
	 * <p> 인수는 CriteriaBuilder에서 생성 한 Predicate를 추가하십시오.
	 */
	public OrmCriteria<T> add(final Predicate predicate) {
		this.predicates.add(predicate);
		return this;
	}

	/** or 조건을 부여합니다. */
	public OrmCriteria<T> or(final Predicate... predicates) {
		if (predicates.length != 0) {
			add(builder.or(predicates));
		}
		return this;
	}

	/** null 일치 조건을 부여합니다. */
	public OrmCriteria<T> isNull(String field) {
		return add(builder.isNull(root.get(field)));
	}

	/** null 불일치 조건을 부여합니다. */
	public OrmCriteria<T> isNotNull(String field) {
		return add(builder.isNotNull(root.get(field)));
	}

	/** 일치 조건을 부여합니다. (값이 null의 경우는 무시됩니다) */
	public OrmCriteria<T> equal(String field, final Object value) {
		return equal(root, field, value);
	}

	public OrmCriteria<T> equal(Path<?> path, String field, final Object value) {
		if (isValid(value)) {
			add(builder.equal(path.get(field), value));
		}
		return this;
	}

	private boolean isValid(final Object value) {
		if (value instanceof String) {
			return StringUtils.isNotBlank((String) value);
		} else if (value instanceof Optional) {
			return ((Optional<?>) value).isPresent();
		} else {
			return value != null;
		}
	}

	/** 불일치 조건을 부여합니다. (값이 null의 경우는 무시됩니다) */
	public OrmCriteria<T> equalNot(String field, final Object value) {
		if (isValid(value)) {
			add(builder.notEqual(root.get(field), value));
		}
		return this;
	}

	/** 일치 조건을 부여합니다. (값이 null의 경우는 무시됩니다) */
	public OrmCriteria<T> equalProp(String field, final String fieldOther) {
		add(builder.equal(root.get(field), root.get(fieldOther)));
		return this;
	}

	/** like 조건을 부여합니다. (값이 null의 경우는 무시됩니다) */
	public OrmCriteria<T> like(String field, String value, MatchMode mode) {
		if (isValid(value)) {
			add(builder.like(root.get(field), mode.toMatchString(value)));
		}
		return this;
	}

	/** like 조건을 부여합니다. [여러 필드에 대한 OR 결합 (값이 null의 경우는 무시됩니다) */
	public OrmCriteria<T> like(String[] fields, String value, MatchMode mode) {
		if (isValid(value)) {
			Predicate[] predicates = new Predicate[fields.length];
			for (int i = 0; i < fields.length; i++) {
				predicates[i] = builder.like(root.get(fields[i]), mode.toMatchString(value));
			}
			add(builder.or(predicates));
		}
		return this;
	}

	/** in 조건을 부여합니다. */
	public OrmCriteria<T> in(String field, final Object[] values) {
		if (values != null && 0 < values.length) {
			add(root.get(field).in(values));
		}
		return this;
	}

	/** between 조건을 부여합니다. */
	public OrmCriteria<T> between(String field, final Date from, final Date to) {
		if (from != null && to != null) {
			predicates.add(builder.between(root.get(field), from, to));
		}
		return this;
	}

	/** between 조건을 부여합니다. */
	public OrmCriteria<T> between(String field, final LocalDate from, final LocalDate to) {
		if (from != null && to != null) {
			predicates.add(builder.between(root.get(field), from, to));
		}
		return this;
	}

	/** between 조건을 부여합니다. */
	public OrmCriteria<T> between(String field, final LocalDateTime from, final LocalDateTime to) {
		if (from != null && to != null) {
			predicates.add(builder.between(root.get(field), from, to));
		}
		return this;
	}

	/** between 조건을 부여합니다. */
	public OrmCriteria<T> between(String field, final String from, final String to) {
		if (isValid(from) && isValid(to)) {
			predicates.add(builder.between(root.get(field), from, to));
		}
		return this;
	}

	/** 필드 & gt; = 값 조건을 부여합니다. (값이 null의 경우는 무시됩니다) */
	public <Y extends Comparable<? super Y>> OrmCriteria<T> gte(String field, final Y value) {
		if (isValid(value)) {
			add(builder.greaterThanOrEqualTo(root.get(field), value));
		}
		return this;
	}

	/** 필드 & gt; 값 조건을 부여합니다. (값이 null의 경우는 무시됩니다) */
	public <Y extends Comparable<? super Y>> OrmCriteria<T> gt(String field, final Y value) {
		if (isValid(value)) {
			add(builder.greaterThan(root.get(field), value));
		}
		return this;
	}

	/** 필드 & lt; = 값 조건을 부여합니다. */
	public <Y extends Comparable<? super Y>> OrmCriteria<T> lte(String field, final Y value) {
		if (isValid(value)) {
			add(builder.lessThanOrEqualTo(root.get(field), value));
		}
		return this;
	}

	/** 필드 & lt; 값 조건을 부여합니다. */
	public <Y extends Comparable<? super Y>>  OrmCriteria<T> lt(String field, final Y value) {
		if (isValid(value)) {
			add(builder.lessThan(root.get(field), value));
		}
		return this;
	}

	/** 정렬 조건을 추가합니다. */
	public OrmCriteria<T> sort(Sort sort) {
		sort.getOrders().forEach(this::sort);
		return this;
	}

	/** 정렬 조건을 추가합니다. */
	public OrmCriteria<T> sort(Sort.SortOrder order) {
		if (order.isAscending()) {
			sort(order.getProperty());
		} else {
			sortDesc(order.getProperty());
		}
		return this;
	}

	/** 오름차순 조건을 추가합니다. */
	public OrmCriteria<T> sort(String field) {
		orders.add(builder.asc(root.get(field)));
		return this;
	}

	/** 내림차순 조건을 추가합니다. */
	public OrmCriteria<T> sortDesc(String field) {
		orders.add(builder.desc(root.get(field)));
		return this;
	}

	public boolean emptySort() {
		return !orders.isEmpty();
	}

	/** 지정한 Entity 클래스를 축으로 한 Criteria를 생성합니다. */
	public static <T> OrmCriteria<T> of(EntityManager em, Class<T> clazz) {
		return new OrmCriteria<>(em, clazz, DefaultAlias);
	}

	/** 지정한 Entity 클래스에 별칭을 끈 붙인 Criteria를 생성합니다. */
	public static <T> OrmCriteria<T> of(EntityManager em, Class<T> clazz, String alias) {
		return new OrmCriteria<>(em, clazz, alias);
	}

}
