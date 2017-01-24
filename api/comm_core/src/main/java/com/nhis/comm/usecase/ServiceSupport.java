package com.nhis.comm.usecase;

import com.nhis.comm.context.DomainHelper;
import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.audit.AuditHandler;
import com.nhis.comm.context.lock.IdLockHandler;
import com.nhis.comm.context.orm.DefaultRepository;
import com.nhis.comm.model.BusinessDayHandler;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.function.Supplier;

/**
 * Created by sewoo on 2016. 12. 28..
 * 유스 케이스 서비스의 기본 클래스。
 */

@Setter
public abstract class ServiceSupport {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource msg;

	/** 도메인 계층을위한 헬퍼 클래스 */
	@Autowired
	protected DomainHelper dh;
	/** 표준 스키마의 Repository */
	@Autowired
	protected DefaultRepository rep;

	@Autowired
	@Qualifier(DefaultRepository.BeanNameTx)
	private PlatformTransactionManager tx;

	/** ID 잠금 유틸리티 */
	@Autowired
	protected IdLockHandler idLock;
	/** 감사 유틸리티 */
	@Autowired
	protected AuditHandler audit;

	@Autowired
	@Lazy
	private BusinessDayHandler businessDay;

	/** 트랜잭션 처리를 실행합니다。 */
	protected <T> T tx(Supplier<T> callable) {
		return ServiceUtils.tx(tx, callable);
	}

	/** 트랜잭션 처리를 실행합니다。 */
	protected void tx(Runnable command) {
		ServiceUtils.tx(tx, command);
	}

	/** 운동코드 Lock 부착 트랜잭션 처리를 실행 합니다。 */
	protected <T> T tx(String exerciseCd, IdLockHandler.LockType lockType, final Supplier<T> callable) {
		return idLock.call(exerciseCd, lockType, () -> {
			return tx(callable);
		});
	}

	/** 운동코드 Lock 부착 트랜잭션 처리를 실행 합니다。 */
	protected void tx(String exerciseCd, IdLockHandler.LockType lockType, final Runnable callable) {
		idLock.call(exerciseCd, lockType, () -> {
			tx(callable);
			return true;
		});
	}

	/** i18n 메시지 변환을 수행합니다. */
	protected String msg(String message) {
		return msg.getMessage(message, null, message, actor().getLocale());
	}

	/** i18n 메시지 변환을 수행합니다. */
	protected String msg(String message, Object... args) {
		return msg.getMessage(message, args, message, actor().getLocale());
	}

	/** 이용자를 반환합니다。 */
	protected Actor actor() {
		return dh.actor();
	}

	/** 영엽일 유틸리티를 반환합니다。 */
	protected BusinessDayHandler businessDay() {
		return businessDay;
	}

}

