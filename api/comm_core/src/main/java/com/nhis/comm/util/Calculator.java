package com.nhis.comm.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by sewoo on 2016. 12. 28..
 * 계산 유틸리티.
 * <p> 단순 계산의 단순화를 목적으로 구현된 Class 스레드로부터 안전하지 않습니다.
 */
public class Calculator {

	private final AtomicReference<BigDecimal> value = new AtomicReference<>();
	/** 소수 자릿수 */
	private int scale = 0;
	/** 단수 정의. 표준은 잘라 */
	private RoundingMode mode = RoundingMode.DOWN;
	/** 계산때 마다 반올림을 할 때는 true */
	private boolean roundingAlways = false;
	/** scale 미 설정시 나누기 scale 값 */
	private int defaultScale = 18;

	private Calculator(Number v) {
		try {
			this.value.set(new BigDecimal(v.toString()));
		} catch (NumberFormatException e) {
			this.value.set(BigDecimal.ZERO);
		}
	}

	private Calculator(BigDecimal v) {
		this.value.set(v);
	}

	/**
	 * 계산 전처리 정의.
	 * @param scale 소수 자릿수
	 * @return 자신의 인스턴스
	 */
	public Calculator scale(int scale) {
		return scale(scale, RoundingMode.DOWN);
	}

	/**
	 * 계산 전처리 정의.
	 * @param scale 소수 자릿수
	 * @param mode 끝 정의
	 */
	public Calculator scale(int scale, RoundingMode mode) {
		this.scale = scale;
		this.mode = mode;
		return this;
	}

	/**
	 * 계산 전에 반올림 정의합니다.
	 * @param roundingAlways 계산 때마다 반올림을 할 때는 true
	 */
	public Calculator roundingAlways(boolean roundingAlways) {
		this.roundingAlways = roundingAlways;
		return this;
	}

	/** 与えた計算値を自身が保持する値に加えます。 */
	public Calculator add(Number v) {
		try {
			add(new BigDecimal(v.toString()));
		} catch (NumberFormatException e) {
		}
		return this;
	}

	/** 전달받은 값은 자신이 보유한 값에 추가 됩니다.*/
	public Calculator add(BigDecimal v) {
		value.set(rounding(value.get().add(v)));
		return this;
	}

	private BigDecimal rounding(BigDecimal v) {
		return roundingAlways ? v.setScale(scale, mode) : v;
	}

	/** 자신이 보유하는 값으로 준 값을 뺍니다 */
	public Calculator subtract(Number v) {
		try {
			subtract(new BigDecimal(v.toString()));
		} catch (NumberFormatException e) {
		}
		return this;
	}

	/** 자신이 유지하는 값으로 준 값을 뺍니다. */
	public Calculator subtract(BigDecimal v) {
		value.set(rounding(value.get().subtract(v)));
		return this;
	}

	/** 자신이 보유하는 값에 준 계산 값을 곱합니다. */
	public Calculator multiply(Number v) {
		try {
			multiply(new BigDecimal(v.toString()));
		} catch (NumberFormatException e) {
		}
		return this;
	}

	/** 자신이 보유하는 값에 준 계산 값을 곱합니다. */
	public Calculator multiply(BigDecimal v) {
		value.set(rounding(value.get().multiply(v)));
		return this;
	}

	/** 준(파라메터 값) 계산 값에서 자신이 보유하는 값을 나눕니다. */
	public Calculator divideBy(Number v) {
		try {
			divideBy(new BigDecimal(v.toString()));
		} catch (NumberFormatException e) {
		}
		return this;
	}

	/** 준 계산 값에서 자신이 보유하는 값을 나눕니다. */
	public Calculator divideBy(BigDecimal v) {
		BigDecimal ret = roundingAlways ? value.get().divide(v, scale, mode)
				: value.get().divide(v, defaultScale, mode);
		value.set(ret);
		return this;
	}

	/** 계산 결과를 int 형으로 반환합니다.。*/
	public int intValue() {
		return decimal().intValue();
	}

	/** 계산 결과를 long 형으로 반환합니다.。*/
	public long longValue() {
		return decimal().longValue();
	}

	/** 계산 결과를 BigDecimal 형으로 반환합니다. */
	public BigDecimal decimal() {
		BigDecimal v = value.get();
		return v != null ? v.setScale(scale, mode) : BigDecimal.ZERO;
	}

	/** 시작 값 0으로 초기화 된 Calculator */
	public static Calculator init() {
		return new Calculator(BigDecimal.ZERO);
	}

	/**
	 * @param v 초기 값
	 * @return 초기화 된 Calculator
	 */
	public static Calculator of(Number v) {
		return new Calculator(v);
	}

	/**
	 * @param v 초기 값
	 * @return 초기화 된 Calculator
	 */
	public static Calculator of(BigDecimal v) {
		return new Calculator(v);
	}
}
