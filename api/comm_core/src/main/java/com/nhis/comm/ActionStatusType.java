package com.nhis.comm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sewoo on 2016. 12. 29..
 *
 * 어떤 행위에 관한 처리 상태 enum class.
 */
public enum ActionStatusType {

	/** 미처리 */
	Unprocessed,
	/** 처리중 */
	Processing,
	/** 처리 완료 */
	Processed,
	/** 취소 */
	Cancelled,
	/** 오류 */
	Error;

	/** 완료된 상태 목록 */
	public static final List<ActionStatusType> finishTypes = Collections.unmodifiableList(
			Arrays.asList(Processed, Cancelled));

	/** 완료되지 않은 상태 목록 (진행 중은 제외) */
	public static final List<ActionStatusType> unprocessingTypes = Collections.unmodifiableList(
			Arrays.asList(Unprocessed, Error));

	/** 완료되지 않은 상태 목록 (진행 중도 포함) */
	public static final List<ActionStatusType> unprocessedTypes = Collections.unmodifiableList(
			Arrays.asList(Unprocessed, Processing, Error));

	/** 완료된 상태의 경우는 true */
	public boolean isFinish() {
		return finishTypes.contains(this);
	}

	/** 완료되지 않은 상태 (진행 중은 제외)의 경우는 true */
	public boolean isUnprocessing() {
		return unprocessingTypes.contains(this);
	}

	/** 완료되지 않은 상태 (진행 중도 포함)의 경우는 true */
	public boolean isUnprocessed() {
		return unprocessedTypes.contains(this);
	}
}
