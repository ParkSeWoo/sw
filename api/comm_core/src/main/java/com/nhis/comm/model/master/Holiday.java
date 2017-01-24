package com.nhis.comm.model.master;

import com.nhis.comm.context.Dto;
import com.nhis.comm.context.orm.OrmActiveMetaRecord;
import com.nhis.comm.context.orm.OrmRepository;
import com.nhis.comm.model.constraints.*;
import com.nhis.comm.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 29..
 *
 *  휴일 마스터를 표현합니다
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Holiday extends OrmActiveMetaRecord<Holiday> {

	private static final long serialVersionUID = 1l;
	public static final String CategoryDefault = "default";

	/** ID */
	@Id
	@GeneratedValue
	private Long id;
	/** 휴일 구분 */
	@Category
	private String category;
	/** 휴일 */
	@ISODate
	private LocalDate day;
	/** 휴일명칭 */
	@Name(max = 40)
	private String name;
	@ISODateTime
	private LocalDateTime createDate;
	@IdStr
	private String createId;
	@ISODateTime
	private LocalDateTime updateDate;
	@IdStr
	private String updateId;

	/** 휴일 마스터를 가져옵니다。 */
	public static Optional<Holiday> get(final OrmRepository rep, LocalDate day) {
		return get(rep, day, CategoryDefault);
	}

	public static Optional<Holiday> get(final OrmRepository rep, LocalDate day, String category) {
		return rep.tmpl().get("from Holiday h where h.category=?1 and h.day=?2", category, day);
	}

	/** 휴일 마스터를 가져옵니다. (예외 포함) */
	public static Holiday load(final OrmRepository rep, LocalDate day) {
		return load(rep, day, CategoryDefault);
	}

	public static Holiday load(final OrmRepository rep, LocalDate day, String category) {
		return rep.tmpl().load("from Holiday h where h.category=?1 and h.day=?2", category, day);
	}

	/**  휴일 정보를 검색합니다. */
	public static List<Holiday> find(final OrmRepository rep, final int year) {
		return find(rep, year, CategoryDefault);
	}

	public static List<Holiday> find(final OrmRepository rep, final int year, final String category) {
		return rep.tmpl().find("from Holiday h where h.category=?1 and h.day between ?2 and ?3 order by h.day",
				category, LocalDate.ofYearDay(year, 1), DateUtils.dayTo(year));
	}

	/** 휴일 마스터를 등록합니다. */
	public static void register(final OrmRepository rep, final RegHoliday p) {
		rep.tmpl().execute("delete from Holiday h where h.category=?1 and h.day between ?2 and ?3",
				p.category, LocalDate.ofYearDay(p.year, 1), DateUtils.dayTo(p.year));
		p.list.forEach(v -> v.create(p).save(rep));
	}

	/** 등록 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegHoliday implements Dto {
		private static final long serialVersionUID = 1l;
		@CategoryEmpty
		private String category = CategoryDefault;
		@Year
		private int year;
		@Valid
		private List<RegHolidayItem> list;

		public RegHoliday(int year, final List<RegHolidayItem> list) {
			this.year = year;
			this.list = list;
		}
	}

	/** 등록 인자 (요소) */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegHolidayItem implements Dto {
		private static final long serialVersionUID = 1l;
		@ISODate
		private LocalDate day;
		@Name(max = 40)
		private String name;

		public Holiday create(RegHoliday p) {
			Holiday holiday = new Holiday();
			holiday.setCategory(p.category);
			holiday.setDay(day);
			holiday.setName(name);
			return holiday;
		}
	}

}
