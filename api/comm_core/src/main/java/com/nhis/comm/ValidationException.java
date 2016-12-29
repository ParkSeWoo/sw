package com.nhis.comm;

import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 27..
 * 예외처리 확인 Class 입니다.
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Warns warns;

	/** 필드에 종속되지 않는 글로벌 심사 옐외를 통지하는 경우 이용하세요。 */
	public ValidationException(String message) {
		super(message);
		warns = Warns.init(message);
	}

	/** 필드에 종속 심사 예외를 통지하는 경우에 이용하세요。 */
	public ValidationException(String field, String message) {
		super(message);
		warns = Warns.init(field, message);
	}

	/** 필드에 종속 심사 예외를 통지하는 경우에 이용하세요。 */
	public ValidationException(String field, String message, String[] messageArgs) {
		super(message);
		warns = Warns.init(field, message, messageArgs);
	}

	/** 여러 건의 심사 예외를 통지 하는 경우에 이용하세요。 */
	public ValidationException(final Warns warns) {
		super(warns.head().map((v) -> v.getMessage()).orElse(ErrorKeys.Exception));
		this.warns = warns;
	}

	/** 발생한 심사 예외 목록을 반환합니다。*/
	public List<Warn> list() {
		return warns.list();
	}

	@Override
	public String getMessage() {
		return warns.head().map((v) -> v.getMessage()).orElse(ErrorKeys.Exception);
	}

	/** 심사 예외 정보입니다。  */
	public static class Warns implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<Warn> list = new ArrayList<>();

		private Warns() {
		}

		public Warns add(String message) {
			list.add(new Warn(null, message, null));
			return this;
		}

		public Warns add(String field, String message) {
			list.add(new Warn(field, message, null));
			return this;
		}

		public Warns add(String field, String message, String[] messageArgs) {
			list.add(new Warn(field, message, messageArgs));
			return this;
		}

		public Optional<Warn> head() {
			return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
		}

		public List<Warn> list() {
			return list;
		}

		public boolean nonEmpty() {
			return !list.isEmpty();
		}

		public static Warns init() {
			return new Warns();
		}

		public static Warns init(String message) {
			return init().add(message);
		}

		public static Warns init(String field, String message) {
			return init().add(field, message);
		}

		public static Warns init(String field, String message, String[] messageArgs) {
			return init().add(field, message, messageArgs);
		}

	}

	/** 필드 스코프의 심사 예외 토큰을 표현 합니다。 */
	@Value
	public static class Warn implements Serializable {
		private static final long serialVersionUID = 1L;
		/** 심사 예외 필드 키 */
		private String field;
		/** 심사 예외 메시지 */
		private String message;
		/** 심사 예외 메시지 인수 */
		private String[] messageArgs;

		/** 필드에 종속되지 않는 글로벌 예외는 true */
		public boolean global() {
			return field == null;
		}
	}

	/** 심사 예외시 이용되는 메시지키 상수 */
	public static interface ErrorKeys {
		/** 시스템 서버에서 문제가 발생했을 수 있습니다. */
		String Exception = "error.Exception";

		/** 정보를 찾을 수 없습니다. */
		String EntityNotFound = "error.EntityNotFoundException";

		/** 로그인 상태가 유효하지 않습니다. */
		String Authentication = "error.Authentication";

		/** 대상 기능의 이용이 허용되지 않습니다. */
		String AccessDenied = "error.AccessDeniedException";

		/** 로그인에 실패했습니다. */
		String Login = "error.login";

		/** 이미등록된 ID 입니다. */
		String DuplicateId = "error.duplicateId";

		/** 이미 처리된 정보 입니다. */
		String ActionUnprocessing = "error.ActionStatusType.unprocessing";
	}

}
