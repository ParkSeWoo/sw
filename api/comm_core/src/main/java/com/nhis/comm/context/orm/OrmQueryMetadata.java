package com.nhis.comm.context.orm;

import javax.persistence.LockModeType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by doldol on 2016. 12. 28..
 * Query위한 추가 메타 정보를 구축합니다.
 */
public class OrmQueryMetadata {

	private final Map<String, Object> hints = new HashMap<>();
	private Optional<LockModeType> lockMode = Optional.empty();

	private OrmQueryMetadata() {}

	/** 내부에 유지하는 hint 정보를 반환합니다. */
	public Map<String, Object> hints() {
		return hints;
	}

	/** 내부에 보유 된 잠금 모드를 돌려줍니다. */
	public Optional<LockModeType> lockMode() {
		return lockMode;
	}

	/** hint를 추가합니다。 */
	public OrmQueryMetadata hint(String hintName, Object value) {
		this.hints.put(hintName, value);
		return this;
	}

	/** 잠금 모드를 설정합니다. */
	public OrmQueryMetadata lockMode(LockModeType lockMode) {
		this.lockMode = Optional.ofNullable(lockMode);
		return this;
	}

	public static OrmQueryMetadata empty() {
		return new OrmQueryMetadata();
	}

	public static OrmQueryMetadata withLock(LockModeType lockMode) {
		return empty().lockMode(lockMode);
	}

	public static OrmQueryMetadata withHint(String hintName, Object value) {
		return empty().hint(hintName, value);
	}

}
