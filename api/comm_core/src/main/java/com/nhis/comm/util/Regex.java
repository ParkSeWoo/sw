package com.nhis.comm.util;

/**
 * Created by sewoo on 2016. 12. 28..
 * 정규 표현식 상수 인터페이스.
 * <p> Checker.match와 함께 이용하십시오.
 */
public interface Regex {
	/** Ascii */
	String rAscii = "^\\p{ASCII}*$";
	/** 문자 */
	String rAlpha = "^[a-zA-Z]*$";
	/** 알파벳 대문자 */
	String rAlphaUpper = "^[A-Z]*$";
	/** 알파벳 소문자 */
	String rAlphaLower = "^[a-z]*$";
	/** 영수 */
	String rAlnum = "^[0-9a-zA-Z]*$";
	/** 기호 */
	String rSymbol = "^\\p{Punct}*$";
	/** 영숫자기호 */
	String rAlnumSymbol = "^[0-9a-zA-Z\\p{Punct}]*$";
	/** 숫자 */
	String rNumber = "^[-]?[0-9]*$";
	/** 정수 */
	String rNumberNatural = "^[0-9]*$";
	/** 배정 밀도 부동 소수점 */
	String rDecimal = "^[-]?(\\d+)(\\.\\d+)?$";
	// see UnicodeBlock
	/** 히라가나 */
	String rHiragana = "^\\p{InHiragana}*$";
	/** 가타카나 */
	String rKatakana = "^\\p{InKatakana}*$";
	/** 반각가타카나 */
	String rHankata = "^[｡-ﾟ]*$";
	/** 반각문자열 */
	String rHankaku = "^[\\p{InBasicLatin}｡-ﾟ]*$"; //라틴 문자 + 반각 카타카나
	/** 전각문자열 */
	String rZenkaku = "^[^\\p{InBasicLatin}｡-ﾟ]*$"; //전각의 정의를 반각 이외로 결론 지어
	/** 한자 */
	String rKanji = "^[\\p{InCJKUnifiedIdeographs}들\\p{InCJKCompatibilityIdeographs}]*$";
	/** 문자  */
	String rWord = "^(?s).*$";
	/** 코드 */
	String rCode = "^[0-9a-zA-Z_-]*$"; // 영수 + 밑줄 + 하이픈
	/** 한글 */
	String rHan = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";
	/** IP*/
	String rIP = "([0-9]{1,3})\\\\.([0-9]{1,3})\\\\.([0-9]{1,3})\\\\.([0-9]{1,3})";

}
