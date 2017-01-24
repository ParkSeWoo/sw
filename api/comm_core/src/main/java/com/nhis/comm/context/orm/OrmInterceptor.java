package com.nhis.comm.context.orm;

import com.nhis.comm.context.Timestamper;
import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.actor.ActorSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

/**
 * Created by sewoo on 2016. 12. 28..
 * Entity 지속성 타이밍에서 AOP 처리를 폐쇄 Interceptor.
 */
@Getter
@Setter
public class OrmInterceptor {

	@Autowired
	private ActorSession session;
	@Autowired
	private Timestamper time;

	/** 등록시 사전 연결 처리합니다. */
	public void touchForCreate(Object entity) {
		if (entity instanceof OrmActiveMetaRecord) {
			Actor staff = session.actor();
			LocalDateTime now = time.date();
			OrmActiveMetaRecord<?> metaEntity = (OrmActiveMetaRecord<?>) entity;
			metaEntity.setCreateId(staff.getId());
			metaEntity.setCreateDate(now);
			metaEntity.setUpdateId(staff.getId());
			metaEntity.setUpdateDate(now);
		}
	}

	/** 변경시 사전 연결 처리합니다. */
	public boolean touchForUpdate(final Object entity) {
		if (entity instanceof OrmActiveMetaRecord) {
			Actor staff = session.actor();
			LocalDateTime now = time.date();
			OrmActiveMetaRecord<?> metaEntity = (OrmActiveMetaRecord<?>) entity;
			if (metaEntity.getCreateDate() == null) {
				metaEntity.setCreateId(staff.getId());
				metaEntity.setCreateDate(now);
			}
			metaEntity.setUpdateId(staff.getId());
			metaEntity.setUpdateDate(now);
		}
		return false;
	}
}
