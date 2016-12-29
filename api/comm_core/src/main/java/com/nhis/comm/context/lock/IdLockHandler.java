package com.nhis.comm.context.lock;

import com.nhis.comm.InvocationException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Created by sewoo on 2016. 12. 29..
 *
 * ID 단위의 잠금을 표현합니다.
 * low : 여기에서는 간단하게 계좌 단위의 ID 잠금만을 대상으로합니다.
 * low : 일반적으로 DB의 테이블 잠금에 "for update"요청 비관적 잠금을 취하거나 합니다만, 샘플이므로 메모리 잠금하고 있습니다.
 *
 */
public class IdLockHandler {
	private Map<Serializable, ReentrantReadWriteLock> lockMap = new HashMap<>();

	/** ID 록에서 작업을 수행합니다. */
	public void call(Serializable id, LockType lockType, final Runnable command) {
		call(id, lockType, () -> {
			command.run();
			return true;
		});
	}

	public <T> T call(Serializable id, LockType lockType, final Supplier<T> callable) {
		if (lockType.isWrite()) {
			writeLock(id);
		} else {
			readLock(id);
		}
		try {
			return callable.get();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new InvocationException("error.Exception", e);
		} finally {
			unlock(id);
		}
	}

	private void writeLock(final Serializable id) {
		Optional.of(id).ifPresent((v) -> {
			synchronized (lockMap) {
				idLock(v).writeLock().lock();
			}
		});
	}

	private ReentrantReadWriteLock idLock(final Serializable id) {
		if (!lockMap.containsKey(id)) {
			lockMap.put(id, new ReentrantReadWriteLock());
		}
		return lockMap.get(id);
	}

	public void readLock(final Serializable id) {
		Optional.of(id).ifPresent((v) -> {
			synchronized (lockMap) {
				idLock(v).readLock().lock();
			}
		});
	}

	public void unlock(final Serializable id) {
		Optional.of(id).ifPresent((v) -> {
			synchronized (lockMap) {
				ReentrantReadWriteLock idLock = idLock(v);
				if (idLock.isWriteLockedByCurrentThread()) {
					idLock.writeLock().unlock();
				} else {
					idLock.readLock().unlock();
				}
			}
		});
	}

	/**
	 * 잠금 유형을 표현하는 Enum.
	 */
	public static enum LockType {
		/** 읽기 전용 잠금 */
		Read,
		/** 읽고 쓰기 전용 잠금 */
		Write;

		public boolean isRead() {
			return !isWrite();
		}

		public boolean isWrite() {
			return this == Write;
		}
	}

}
