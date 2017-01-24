package com.nhis.comm.context.audit;

import com.nhis.comm.InvocationException;
import com.nhis.comm.ValidationException;
import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.actor.ActorSession;
import com.nhis.comm.context.audit.AuditEvent;
import com.nhis.comm.context.audit.AuditEvent.RegAuditEvent;
import com.nhis.comm.context.orm.SystemRepository;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by sewoo on 2016. 12. 29..
 * 이용자 감사와 시스템 감사 (정시 배치 및 일일 배치 등) 등을 취급합니다.
 * <p> 암시 적 적용을 원하는 경우 AOP와의 제휴도 검토하십시오.
 * <p> 대상이되는 로그는 Logger뿐만 아니라 시스템 스키마 감사 테이블에 요청 합니다.
 * (시작과 완료시 다른 TX함으로써 응답 없음 상태를 감지 가능)
 */
@Setter
public class AuditHandler {
	public static final Logger LoggerActor = LoggerFactory.getLogger("Audit.Actor");
	public static final Logger LoggerEvent = LoggerFactory.getLogger("Audit.Event");
	protected Logger loggerSystem = LoggerFactory.getLogger(getClass());

	@Autowired
	private ActorSession session;
	@Autowired
	private AuditPersister persister;

	/** 준 처리에 대한 감사 로그를 기록합니다. */
	public <T> T audit(String message, final Supplier<T> callable) {
		return audit("default", message, callable);
	}

	/** 준 처리에 대한 감사 로그를 기록합니다. */
	public void audit(String message, final Runnable command) {
		audit(message, () -> {
			command.run();
			return true;
		});
	}

	/** 준 처리에 대한 감사 로그를 기록합니다. */
	public <T> T audit(String category, String message, final Supplier<T> callable) {
		logger().trace(message(message, "[시작]", null));
		long start = System.currentTimeMillis();
		try {
			T v = session.actor().getRoleType().isSystem() ? callEvent(category, message, callable)
					: callAudit(category, message, callable);
			logger().info(message(message, "[완료]", start));
			return v;
		} catch (ValidationException e) {
			logger().warn(message(message, "[심사예]", start));
			throw e;
		} catch (RuntimeException e) {
			logger().error(message(message, "[예외]", start));
			throw (RuntimeException) e;
		} catch (Exception e) {
			logger().error(message(message, "[예외]", start));
			throw new InvocationException("error.Exception", e);
		}
	}

	/** 준(넘겨온) 처리에 대한 감사 로그를 기록합니다. */
	public void audit(String category, String message, final Runnable command) {
		audit(category, message, () -> {
			command.run();
			return true;
		});
	}

	private Logger logger() {
		return session.actor().getRoleType().isSystem() ? LoggerEvent : LoggerActor;
	}

	private String message(String message, String prefix, Long startMillis) {
		Actor actor = session.actor();
		StringBuilder sb = new StringBuilder(prefix + " ");
		if (actor.getRoleType().notSystem()) {
			sb.append("[" + actor.getId() + "] ");
		}
		sb.append(message);
		if (startMillis != null) {
			sb.append(" [" + (System.currentTimeMillis() - startMillis) + "ms]");
		}
		return sb.toString();
	}

	public <T> T callAudit(String category, String message, final Supplier<T> callable) {
		Optional<AuditActor> audit = Optional.empty();
		try {
			try { // 시스템 스키마의 장애는 본질적인 오류에 영향을주지 않도록
				audit = Optional.of(persister.start(AuditActor.RegAuditActor.of(category, message)));
			} catch (Exception e) {
				loggerSystem.error(e.getMessage(), e);
			}
			T v = callable.get();
			try {
				audit.ifPresent(persister::finish);
			} catch (Exception e) {
				loggerSystem.error(e.getMessage(), e);
			}
			return v;
		} catch (ValidationException e) {
			try {
				audit.ifPresent((v) -> persister.cancel(v, e.getMessage()));
			} catch (Exception ex) {
				loggerSystem.error(ex.getMessage(), ex);
			}
			throw e;
		} catch (RuntimeException e) {
			try {
				audit.ifPresent((v) -> persister.error(v, e.getMessage()));
			} catch (Exception ex) {
				loggerSystem.error(ex.getMessage(), ex);
			}
			throw e;
		} catch (Exception e) {
			try {
				audit.ifPresent((v) -> persister.error(v, e.getMessage()));
			} catch (Exception ex) {
				loggerSystem.error(ex.getMessage(), ex);
			}
			throw new InvocationException(e);
		}
	}

	public <T> T callEvent(String category, String message, final Supplier<T> callable) {
		Optional<AuditEvent> audit = Optional.empty();
		try {
			try { // 시스템 스키마의 장애는 본질적인 오류에 영향을주지 않도록
				audit = Optional.of(persister.start(RegAuditEvent.of(category, message)));
			} catch (Exception e) {
				loggerSystem.error(e.getMessage(), e);
			}
			T v = callable.get();
			try {
				audit.ifPresent(persister::finish);
			} catch (Exception e) {
				loggerSystem.error(e.getMessage(), e);
			}
			return v;
		} catch (ValidationException e) {
			try {
				audit.ifPresent((v) -> persister.cancel(v, e.getMessage()));
			} catch (Exception ex) {
				loggerSystem.error(ex.getMessage(), ex);
			}
			throw e;
		} catch (RuntimeException e) {
			try {
				audit.ifPresent((v) -> persister.error(v, e.getMessage()));
			} catch (Exception ex) {
				loggerSystem.error(ex.getMessage(), ex);
			}
			throw (RuntimeException) e;
		} catch (Exception e) {
			try {
				audit.ifPresent((v) -> persister.error(v, e.getMessage()));
			} catch (Exception ex) {
				loggerSystem.error(ex.getMessage(), ex);
			}
			throw new InvocationException(e);
		}
	}

	/**
	 * 감사 로그를 시스템 스키마로 유지합니다.
	 */
	public static class AuditPersister {
		@Autowired
		private SystemRepository rep;

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditActor start(AuditActor.RegAuditActor p) {
			return AuditActor.register(rep, p);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditActor finish(AuditActor audit) {
			return audit.finish(rep);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditActor cancel(AuditActor audit, String errorReason) {
			return audit.cancel(rep, errorReason);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditActor error(AuditActor audit, String errorReason) {
			return audit.error(rep, errorReason);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditEvent start(RegAuditEvent p) {
			return AuditEvent.register(rep, p);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditEvent finish(AuditEvent event) {
			return event.finish(rep);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditEvent cancel(AuditEvent event, String errorReason) {
			return event.cancel(rep, errorReason);
		}

		@Transactional(value = SystemRepository.BeanNameTx, propagation = Propagation.REQUIRES_NEW)
		public AuditEvent error(AuditEvent event, String errorReason) {
			return event.error(rep, errorReason);
		}
	}

}
