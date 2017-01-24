package com.nhis.comm.context.orm;

import com.nhis.comm.context.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by sewoo on 2016. 12. 28..
 * OrmActiveRecord 가입 / 변경 메타 개념을 부여한 기저 클래스.
 * 본 클래스를 상속하여 생성 된 Entity는 지속시 자동 메타 정보 업데이트가 이루어집니다.
 * @see OrmInterceptor
 */
public abstract class OrmActiveMetaRecord<T extends Entity> extends OrmActiveRecord<T>
																	implements Serializable, Entity {
	private static final long serialVersionUID = 1L;

	/** 등록 이용자 ID */
	public abstract String getCreateId();

	public abstract void setCreateId(String createId);

	/** 등록 일시 */
	public abstract LocalDateTime getCreateDate();

	public abstract void setCreateDate(LocalDateTime createDate);

	/** 업데이트 이용자 ID */
	public abstract String getUpdateId();

	public abstract void setUpdateId(String updateId);

	/** 업데이트 날짜 */
	public abstract LocalDateTime getUpdateDate();

	public abstract void setUpdateDate(LocalDateTime updateDate);
}
