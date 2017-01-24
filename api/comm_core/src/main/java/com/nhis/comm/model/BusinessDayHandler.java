package com.nhis.comm.model;

import com.nhis.comm.model.master.Holiday;
import com.nhis.comm.context.Timestamper;
import com.nhis.comm.context.orm.DefaultRepository;
import com.nhis.comm.util.DateUtils;
import com.nhis.comm.model.master.Holiday.RegHoliday;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 28..
 *
 * 도메인에 따라 영업일 관련 유틸리티 핸들러.
 */
@Component
@AllArgsConstructor
public class BusinessDayHandler {
	private Timestamper time;
	private HolidayAccessor holidayAccessor;

	/** 영업일을 반환 합니다 */
	public LocalDate day() {
		return time.day();
	}

	/** 영업일을 반환합니다. */
	public LocalDate day(int daysToAdd) {
		LocalDate day = day();
		if (0 < daysToAdd) {
			for (int i = 0; i < daysToAdd; i++)
				day = dayNext(day);
		} else if (daysToAdd < 0) {
			for (int i = 0; i < (-daysToAdd); i++)
				day = dayPrevious(day);
		}
		return day;
	}

	private LocalDate dayNext(LocalDate baseDay) {
		LocalDate day = baseDay.plusDays(1);
		while (isHolidayOrWeeekDay(day))
			day = day.plusDays(1);
		return day;
	}

	private LocalDate dayPrevious(LocalDate baseDay) {
		LocalDate day = baseDay.minusDays(1);
		while (isHolidayOrWeeekDay(day))
			day = day.minusDays(1);
		return day;
	}

	/** 공휴일이나 주말시는 true. */
	private boolean isHolidayOrWeeekDay(LocalDate day) {
		return (DateUtils.isWeekend(day) || isHoliday(day));
	}

	private boolean isHoliday(LocalDate day) {
		return holidayAccessor == null ? false : holidayAccessor.getHoliday(day).isPresent();
	}

	/** 공휴일 마스터를 검색 / 등록 액세서. */
	@Component
	@AllArgsConstructor
	public static class HolidayAccessor {
		private DefaultRepository rep;

		@Transactional(DefaultRepository.BeanNameTx)
		@Cacheable(cacheNames = "HolidayAccessor.getHoliday")
		public Optional<Holiday> getHoliday(LocalDate day) {
			return Holiday.get(rep, day);
		}

		@Transactional(DefaultRepository.BeanNameTx)
		@CacheEvict(cacheNames = "HolidayAccessor.getHoliday", allEntries = true)
		public void register(final DefaultRepository rep, final RegHoliday p) {
			Holiday.register(rep, p);
		}

	}
}
