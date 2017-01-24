package com.nhis.comm.context;

import com.nhis.comm.util.DateUtils;
import com.nhis.comm.util.TimePoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by sewoo on 2016. 12. 28..
 * 일,시 유틸리티 구성요소 입니다.
 */
public class Timestamper {

	public static final String KeyDay = "system.businessDay.day";

	@Autowired(required = false)
	private AppSettingHandler setting;

	private final Clock clock;

	public Timestamper() {
		clock = Clock.systemDefaultZone();
	}

	public Timestamper(final Clock clock) {
		this.clock = clock;
	}

	/** 영업일을 반환합니다. */
	public LocalDate day() {
		return setting == null ? LocalDate.now(clock) : DateUtils.day(setting.setting(KeyDay).str());
	}

	/** 날짜와 시간을 반환합니다. */
	public LocalDateTime date() {
		return LocalDateTime.now(clock);
	}

	/** 일 / 시간을 반환합니다. */
	public TimePoint tp() {
		return TimePoint.of(day(), date());
	}

	/**
	 * 영업일을 지정된 날짜에 진행합니다.
	 * <p>AppSettingHandler 설정시에만 유효합니다.
	 * @param day 업데이트 일
	 */
	public Timestamper proceedDay(LocalDate day) {
		if (setting != null)
			setting.update(KeyDay, DateUtils.dayFormat(day));
		return this;
	}

}
