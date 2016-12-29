package com.nhis.comm.util;

import com.ibm.icu.text.Transliterator;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 29..
 *
 * 각종 형 / 문자열 변환을 지원합니다. (ICU4J 라이브러리에 의존하고 있습니다)
 */
public abstract class ConvertUtils {

	private static Transliterator ZenkakuToHan = Transliterator.getInstance("Fullwidth-Halfwidth");
	private static Transliterator HankakuToZen = Transliterator.getInstance("Halfwidth-Fullwidth");
	private static Transliterator KatakanaToHira = Transliterator.getInstance("Katakana-Hiragana");
	private static Transliterator HiraganaToKana = Transliterator.getInstance("Hiragana-Katakana");

	/** 예외없이 Long로 변환합니다. (변환 할 수 없을 때는 null) */
	public static Long quietlyLong(Object value) {
		try {
			return Optional.ofNullable(value).map(v -> Long.parseLong(v.toString())).orElse(null);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** 예외없이 Integer로 변환합니다. (변환 할 수 없을 때는 null) */
	public static Integer quietlyInt(Object value) {
		try {
			return Optional.ofNullable(value).map(v -> Integer.parseInt(v.toString())).orElse(null);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** 예외없이 BigDecimal로 변환합니다. (변환 할 수 없을 때는 null) */
	public static BigDecimal quietlyDecimal(Object value) {
		try {
			return Optional.ofNullable(value).map((v) -> new BigDecimal(v.toString())).orElse(null);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** 예외없이 Boolean으로 변환합니다. (변환 할 수없는 경우는 false) */
	public static Boolean quietlyBool(Object value) {
		return Optional.ofNullable(value).map((v) -> Boolean.parseBoolean(v.toString())).orElse(false);
	}

	/** 전각문자를 반각 합니다. */
	public static String zenkakuToHan(String text) {
		return Optional.ofNullable(text).map((v) -> ZenkakuToHan.transliterate(v)).orElse(null);
	}

	/** 반각문자를 전각 합니다. */
	public static String hankakuToZen(String text) {
		return Optional.ofNullable(text).map((v) -> HankakuToZen.transliterate(v)).orElse(null);
	}

	/** 지정한 문자열을 추출합니다. (surrogate 페어 대응) */
	public static String substring(String text, int start, int end) {
		if (text == null)
			return null;
		int spos = text.offsetByCodePoints(0, start);
		int epos = text.length() < end ? text.length() : end;
		return text.substring(spos, text.offsetByCodePoints(spos, epos - start));
	}

	/** 문자열을 왼쪽에서 지정한 문자로 가져옵니다. (surrogate 페어 대응) */
	public static String left(String text, int len) {
		return substring(text, 0, len);
	}

	/** 문자열을 왼쪽에서 지정한 바이트 수를 가져옵니다 */
	public static String leftStrict(String text, int lenByte, String charset) {
		StringBuilder sb = new StringBuilder();
		try {
			int cnt = 0;
			for (int i = 0; i < text.length(); i++) {
				String v = text.substring(i, i + 1);
				byte[] b = v.getBytes(charset);
				if (lenByte < cnt + b.length) {
					break;
				} else {
					sb.append(v);
					cnt += b.length;
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return sb.toString();
	}


}
