package com.nhis.comm.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 28..
 * 자주 이용되는 일시 유틸리티를 표현합니다.
 */
public class DateUtils {

	private static WeekendQuery WeekendQuery = new WeekendQuery();

	/** 지정된 문자열 (YYYY-MM-DD)을 바탕으로 날짜로 변환합니다. */
	public static LocalDate day(String dayStr) {
		return dayOpt(dayStr).orElse(null);
	}

	public static Optional<LocalDate> dayOpt(String dayStr) {
		if (StringUtils.isBlank(dayStr))
			return Optional.empty();
		return Optional.of(LocalDate.parse(dayStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE));
	}

	/** 지정된 문자열과 포맷 형식을 바탕으로 날짜로 변환합니다. */
	public static LocalDateTime date(String dateStr, DateTimeFormatter formatter) {
		return dateOpt(dateStr, formatter).orElse(null);
	}

	public static Optional<LocalDateTime> dateOpt(String dateStr, DateTimeFormatter formatter) {
		 if (StringUtils.isBlank(dateStr))
			return Optional.empty();
		return Optional.of(LocalDateTime.parse(dateStr.trim(), formatter));
	}

	/** 지 정된 문자열과 포맷 스트링을 바탕으로 날짜로 변환합니다. */
	public  static LocalDateTime date(String dateStr, String format) {
		return date(dateStr, DateTimeFormatter.ofPattern(format));
	}

 	public static Optional<LocalDateTime> dateOpt(String dateStr, String format) {
 		return dateOpt(dateStr, DateTimeFormatter.ofPattern(format));
 	}

 	/** 지정된 날짜를 날짜로 변환합니다.*/
 	public static LocalDateTime dateByDay(LocalDate day) {
 		return dateByDayOpt(day).orElse(null);
 	}

 	public static Optional<LocalDateTime> dateByDayOpt(LocalDate day) {
 		return Optional.ofNullable(day).map((v) -> v.atStartOfDay());
 	}

 	/** 지정한 날짜의 다음날부터 1msec 뺀 날짜를 반환합니다. */
 	public static LocalDateTime dateTo(LocalDate day) {
 		return dateToOpt(day).orElse(null);
 	}

 	public static Optional<LocalDateTime> dateToOpt(LocalDate day) {
 		return Optional.ofNullable(day).map((v) -> v.atTime(23, 59, 59));
 	}

 	/** 지정된 일시 형과 포맷 형식을 바탕으로 문자열 (YYYY-MM-DD)로 변경합니다. */
 	public static String dayFormat(LocalDate day) {
 		return dayFormatOpt(day).orElse(null);
 	}

 	public static Optional<String> dayFormatOpt(LocalDate day) {
 		return Optional.ofNullable(day).map((v) -> v.format(DateTimeFormatter.ISO_LOCAL_DATE));
 	}

 	/** 지정된 일시 형과 포맷 형식을 바탕으로 문자열로 변경합니다. */
	public static String dateFormat(LocalDateTime date, DateTimeFormatter formatter) {
		return dateFormatOpt(date, formatter).orElse(null);
	}

	public static Optional<String> dateFormatOpt(LocalDateTime date, DateTimeFormatter formatter) {
		return Optional.ofNullable(date).map((v) -> v.format(formatter));
	}

	/** 지정된 일시 형과 포맷 문자열을 원래 문자열로 변경합니다 */
	public static String dateFormat(LocalDateTime date, String format) {
		return dateFormatOpt(date, format).orElse(null);
	}

	public static Optional<String> dateFormatOpt(LocalDateTime date, String format) {
		return Optional.ofNullable(date).map((v) -> v.format(DateTimeFormatter.ofPattern(format)));
	}

	/** 날짜 간격을 가져옵니다. */
	public static Optional<Period> between(LocalDate start, LocalDate end) {
		if (start == null || end == null)
			return Optional.empty();
		return Optional.of(Period.between(start, end));
	}

	/** 시간 간격을 가져옵니다. */
	public static Optional<Duration> between(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null)
			return Optional.empty();
		return Optional.of(Duration.between(start, end));
	}

	/** 지정 영업일이 주말 (토일) 또는 판정합니다. (인수는 필수) */
	public static boolean isWeekend(LocalDate day) {
		Assert.notNull(day);
		return day.query(WeekendQuery);
	}

	/** 지정 연도의 마직막 날을 가져옵니다. */
	public static LocalDate dayTo(int year) {
		return LocalDate.ofYearDay(year, Year.of(year).isLeap() ? 366 : 365);
	}

	/** 주말확인 TemporalQuery & gt; Boolean & lt;을 표현합니다.    */
	public static class WeekendQuery implements TemporalQuery<Boolean> {
		@Override
		public Boolean queryFrom(TemporalAccessor temporal) {
			DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
			return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
		}
	}

}
