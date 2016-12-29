package com.nhis.comm.context.actor;

import com.nhis.comm.context.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

/**
 * Created by sewoo on 2016. 12. 27..
 *
 * 사용자 : 배우를 정의 하는 Class 입니다
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actor implements Dto {
	private static final long serialVersionUID = 1L;

	/** 익명 사용자 상수 */
	public static Actor Anonymous = new Actor("unknown", ActorRoleType.Anonymous);
	/** 시스템 이용자 상수 */
	public static Actor System = new Actor("system", ActorRoleType.System);

	/** 이용자 ID */
	private String id;
	/** 이용자 이름 */
	private String name;
	/** 이용자가 가지는 롤 타입{@link ActorRoleType} */
	private ActorRoleType roleType;
	/** 이용자가 사용하는{@link Locale} */
	private Locale locale;
	/** 이용자가 접속 하는 체널 */
	private String channel;
	/** 이용자를 특정하는 외부 정보。(IP등) */
	private String source;

	public Actor(String id, ActorRoleType roleType) {
		this(id, id, roleType);
	}

	public Actor(String id, String name, ActorRoleType roleType) {
		this(id, name, roleType, Locale.getDefault(), null, null);
	}

	/**
	 * 이용자 역활 。
	 */
	public static enum ActorRoleType {
		/** 익명사용자(ID등의 특정 정보가 없는 이용자) */
		Anonymous,
		/** 이용자 (주로BtoC의 고객, BtoB제공처 직원) */
		User,
		/** 내부 이용자(주로BtoC 직원, BtoB제공 모든 직원) */
		Internal,
		/** 시스템 관리자(IT 시스템 담당 직원 또는 시스템 관리 회사의 직원) */
		Administrator,
		/** 시스템(시스템에서 자동처리) */
		System;

		public boolean isAnonymous() {
			return this == Anonymous;
		}

		public boolean isSystem() {
			return this == System;
		}

		public boolean notSystem() {
			return !isSystem();
		}
	}
}
