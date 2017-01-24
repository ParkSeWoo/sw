package com.nhis.comm;

/**
 * Created by sewoo on 2016. 12. 28..
 * 처리시 실행 예외를 표현합니다。
 * <p>복구 불가능한 시스템 예외를 감싸는 목적으로 이용하세요。
 */
public class InvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvocationException(String message) {
		super(message);
	}

	public InvocationException(Throwable cause) {
		super(cause);
	}

}