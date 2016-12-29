package com.nhis.comm.util;

import com.nhis.comm.model.constraints.ISODate;
import com.nhis.comm.model.constraints.ISODateTime;
import lombok.Value;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by sewoo on 2016. 12. 28..
 * 날짜와 시간의 쌍을 표현합니다.
 * <p> 0:00에 영업일 전환이되지 않는 케이스 등에서의 이용을 상정하고 있습니다.
 */
@Value
public class TimePoint implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 날짜(일) */
	@ISODate
	private LocalDate day;
	/** 날짜의 시스템 날짜 및 시간 */
	@ISODateTime
	private LocalDateTime date;

	public LocalDate day() {
		return getDay();
	}

	public LocalDateTime date() {
		return getDate();
	}

	/** 지정날자와 같은지 (day == targetDay) */
	public boolean equalsDay(LocalDate targetDay) {
		return day.compareTo(targetDay) == 0;
	}

	/** 지정 날자 이전(day &lt; targetDay) */
	public boolean beforeDay(LocalDate targetDay) {
		return day.compareTo(targetDay) < 0;
	}

	/** 지정 날짜 이전 또는 같음。(day &lt;= targetDay) */
	public boolean beforeEqualsDay(LocalDate targetDay) {
		return day.compareTo(targetDay) <= 0;
	}

	/** 지정 날짜 이후. (targetDay &lt; day) */
	public boolean afterDay(LocalDate targetDay) {
		return 0 < day.compareTo(targetDay);
	}

	/** 지정 날짜 같거나 이후.(targetDay &lt;= day) */
	public boolean afterEqualsDay(LocalDate targetDay) {
		return 0 <= day.compareTo(targetDay);
	}

	/** 날짜 / 시간을 바탕으로 TimePoint을 생성합니다. */
	public static TimePoint of(LocalDate day, LocalDateTime date) {
		return new TimePoint(day, date);
	}

	/** 날짜를 바탕으로 TimePoint을 생성합니다. */
	public static TimePoint of(LocalDate day) {
		return of(day, day.atStartOfDay());
	}

	/** TimePoint를 생성합니다. */
	public static TimePoint now() {
		LocalDateTime now = LocalDateTime.now();
		return of(now.toLocalDate(), now);
	}

	/** TimePoint를 생성합니다. */
	public static TimePoint now(Clock clock) {
		LocalDateTime now = LocalDateTime.now(clock);
		return of(now.toLocalDate(), now);
	}

}
