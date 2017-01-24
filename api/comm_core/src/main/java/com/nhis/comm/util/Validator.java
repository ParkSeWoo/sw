package com.nhis.comm.util;

import com.nhis.comm.*;
import com.nhis.comm.ValidationException;

import java.util.function.Consumer;

/**
 * Created by sewoo on 2016. 12. 28..
 * 심사 예외의 구축 개념을 표현 합니다.
 */
public class Validator {
	private ValidationException.Warns warns = ValidationException.Warns.init();

	/** 심사 처리합니다. */
	public static void validate(Consumer<Validator> proc) {
		Validator validator = new Validator();
		proc.accept(validator);
		validator.verify();
	}

	/** 심사를 실시합니다. valid가 false의 경우에 예외를 내부에 스택합니다. */
	public Validator check(boolean valid, String message) {
		if (!valid)
			warns.add(message);
		return this;
	}

	/** 개별 속성의 심사를 실시합니다. valid가 false의 경우에 예외를 내부에 스택합니다. */
	public Validator checkField(boolean valid, String field, String message) {
		if (!valid)
			warns.add(field, message);
		return this;
	}

	/** 심사를 실시합니다. 실패했을 때는 즉시 예외를 발생시킵니다. */
	public Validator verify(boolean valid, String message) {
		return check(valid, message).verify();
	}

	/** 개별 속성의 심사를 실시합니다. 실패했을 때는 즉시 예외를 발생시킵니다. */
	public Validator verifyField(boolean valid, String field, String message) {
		return checkField(valid, field, message).verify();
	}

	/** 검증합니다. 사전에 갔다 check에서 예외가 존재하고 있었을 때는 예외를 발생시킵니다. */
	public Validator verify() {
		if (hasWarn())
			throw new ValidationException(warns);
		return clear();
	}

	/** 심사 예외를 보유하고있는 경우는 true를 돌려줍니다. */
	public boolean hasWarn() {
		return warns.nonEmpty();
	}

	/** 내부에 보유 심사 예외를 초기화합니다. */
	public Validator clear() {
		warns.list().clear();
		return this;
	}

}
