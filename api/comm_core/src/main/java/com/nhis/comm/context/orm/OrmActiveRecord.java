package com.nhis.comm.context.orm;

import com.nhis.comm.context.Entity;
import com.nhis.comm.util.Validator;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by sewoo on 2016. 12. 28..
 * ORM 기반으로 ActiveRecord의 개념을 제공하는 Entity 기본 클래스.
 * <p> 여기에서는 자동 인스턴스의 상태에 따라 간단한 동작만을 지원합니다.
 * 실제 ActiveRecord 모델에는 get / find 등의 개념이 포함되어 있지만, 그들은 자기의 상태를
 * 바꾸는 행위가 아니라 대상 인스턴스를 특정하는 행위 (클래스 개념)에 해당하기 때문에,
 * 클래스 메소드로 상속 중에도 개별 정의하도록하십시오.
 * <pre>
 * public static Optional&lt;Account&gt; get(final OrmRepository rep, String id) {
 *     return rep.get(Account.class, id);
 * }
 *
 * public static Account findAll(final OrmRepository rep) {
 *     return rep.findAll(Account.class);
 * }
 * </pre>
 */
public class OrmActiveRecord<T extends Entity> implements Serializable, Entity {

	private static final long serialVersionUID = 1L;

	/** 심사 처리를합니다。 */
	@SuppressWarnings("unchecked")
	protected T validate(Consumer<Validator> proc) {
		Validator.validate(proc);
		return (T) this;
	}

	/**
	 * 주어진 저장소를 통해 자신을 신규 추가합니다.
	 * @param rep 지속성 데 사용하는 관련 {@link OrmRepository}
  	 * @return 자신의 정보
 	 */
 	@SuppressWarnings("unchecked")
 	public T save(final OrmRepository rep) {
 		return (T) rep.save(this);
 	}

 	/**
 	 * 주어진 저장소를 통해 자신을 업데이트합니다.
 	 * @param rep 지속성 데 사용하는 관련 {@link OrmRepository}
 	 */
 	@SuppressWarnings("unchecked")
 	public T update(final OrmRepository rep) {
 		return (T) rep.update(this);
 	}

	/**
	 * 주 어진 저장소를 통해 육체적 삭제합니다.
	 * @param rep 지속성 데 사용하는 관련 {@link OrmRepository}
	 */
	@SuppressWarnings("unchecked")
	public T delete(final OrmRepository rep) {
		return (T) rep.delete(this);
	}

	/**
	 * 주어진 저장소를 통해 자신을 신규 추가 또는 업데이트합니다.
	 * @param rep 지속성 데 사용하는 관련 {@link OrmRepository}
	 */
	@SuppressWarnings("unchecked")
	public T saveOrUpdate(final OrmRepository rep) {
		return (T) rep.saveOrUpdate(this);
	}

}
