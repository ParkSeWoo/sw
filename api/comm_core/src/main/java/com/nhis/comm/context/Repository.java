package com.nhis.comm.context;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 28..
 *
 * 특정 도메인 객체에 의존하지 않는 범용적인 Repository입니다.
 * <p> 형식 안전하지 않은 Repository로 사용할 수 있습니다.
 *
 */

public interface Repository {

	/**
	 * @return 도메인 계층에서 인프라 계층 구성 요소에 대한 액세스를 제공하는 도우미 유틸리티를 반환합니다。
	 */
	DomainHelper dh();

	/**
	 * 기본 키와 일치하는 {@link Entity}를 반환합니다.
	 * @param <T> 반환형식
	 * @param clazz 얻게되는 instance class
	 * @param id 기본 키
	 * @return 기본 키와 일치하는 {@link Entity}.
	 */
	<T extends Entity> Optional<T> get(final Class<T> clazz, final Serializable id);

	/**
	 * 기본 키와 일치하는 {@link Entity}를 반환합니다.
	 * @param <T> 반환형식
	 * @param clazz 얻게되는 instance class
	 * @param id 기본 키
	 * @return 기본 키와 일치하는 {@link Entity}. 일치하지 않을 경우에는 예외.
	 */
	<T extends Entity> T load(final Class<T> clazz, final Serializable id);

	/**
	 * 기본 키와 일치하는 {@link Entity}를 반환합니다.
	 * <p>락부 (for update) 취득을 위해 교착 상태 회피를 의식하도록하십시오.
	 * @param <T> 반환 형식
	 * @param clazz 얻게되는 instance class
	 * @param id 기본 키
	 * @return 기본 키와 일치하는 {@link Entity}. 일치하지 않을 경우에는 예외.
	 */
	<T extends Entity> T loadForUpdate(final Class<T> clazz, final Serializable id);

	/**
	 * 기본 키와 일치하는 {@link Entity}가 존재하거나 반환합니다.
	 * @param <T> 확인형
	 * @param clazz 대상 class
	 * @param id 기본 키
	 * @return 존재할 때는 true
	 */
	<T extends Entity> boolean exists(final Class<T> clazz, final Serializable id);

	/**
	 * 관리하는 {@link Entity}을 모두 반환합니다.
	 * 조건 검색 등 #template을 이용하여 실행하도록하십시오.
	 * @param <T> 반환 형식
	 * @param clazz
	 * @return {@link Entity}목록
	 */
	<T extends Entity> List<T> findAll(final Class<T> clazz);

	/**
	 * {@link Entity} 신규 추가합니다.
	 * @param entity 추가 대상{@link Entity}
	 * @return 추가 된 {@link Entity}의 기본 키
	 */
	<T extends Entity> T save(final T entity);

	/**
	 * {@link Entity} 신규 추가 또는 업데이트 합니다.
	 * <p> 이미 동일한 기본 키가 존재하는 경우 갱신.
	 * 존재하지 않을 때는 신규 ​​추가됩니다.
	 * @param entity 추가 대상 {@link Entity}
	 */
	<T extends Entity> T saveOrUpdate(final T entity);

	/**
	 * {@link Entity}를 업데이트합니다
	 * @param entity 업데이트 대상 {@link Entity}
	 */
	<T extends Entity> T update(final T entity);

	/**
	 * {@link Entity}을 삭제합니다
	 * @param entity 삭제 {@link Entity}
	 */
	<T extends Entity> T delete(final T entity);
}
