package com.nhis.comm.usecase;

import com.nhis.comm.ValidationException;
import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.InvocationException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Created by sewoo on 2016. 12. 28..
 * Service 에서 처리되는 Util 처리
 */
public class ServiceUtils {
	/** 반환 값을 필요로하는 트랜잭션을 처리를 합니다。 */
	public static <T> T tx(PlatformTransactionManager tx, Supplier<T> callable) {
		return new TransactionTemplate(tx).execute((status) -> {
			try {
				return callable.get();
			} catch (RuntimeException e) {
				throw (RuntimeException) e;
			} catch (Exception e) {
				throw new InvocationException("error.Exception", e);
			}
		});
	}

	/** 반환값을 필요로 하지 않는 트랜잭션을 처리 합니다。 */
	public static void tx(PlatformTransactionManager tx, Runnable callable) {
		@SuppressWarnings("unused")
		boolean ret = tx(tx, () -> {
			callable.run();
			return true;
		});
	}

	/** 익명 이외의 이용자 정보를 반환합니다.。 */
	public static Actor actorUser(Actor actor) {
		if (actor.getRoleType().isAnonymous()) {
			throw new ValidationException(ValidationException.ErrorKeys.Authentication);
		}
		return actor;
	}

}
