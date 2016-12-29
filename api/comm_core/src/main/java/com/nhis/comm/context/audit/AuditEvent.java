package com.nhis.comm.context.audit;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;

import com.nhis.comm.ActionStatusType;
import com.nhis.comm.context.Dto;
import com.nhis.comm.context.orm.*;
import com.nhis.comm.context.orm.Sort.SortOrder;
import com.nhis.comm.model.constraints.*;
import com.nhis.comm.util.DateUtils;

import java.time.*;
import lombok.*;

/**
 * Created by sewoo on 2016. 12. 29..
 * 시스템 이벤트 감사 로그를 표현합니다.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class AuditEvent extends OrmActiveRecord<AuditEvent> {
	private static final long serialVersionUID = 1l;

	@Id
	@GeneratedValue
	private Long id;
	/** 카테고리 */
	private String category;
	/** 메시지 */
	private String message;
	/** 처리 상태 */
	@Enumerated(EnumType.STRING)
	private ActionStatusType statusType;
	/** 오류 사유 */
	private String errorReason;
	/** 처리 시간 (msec) */
	private Long time;
	/** 시작시간 */
	@NotNull
	private LocalDateTime startDate;
	/** 종료 시간 (미완료시는 null) */
	private LocalDateTime endDate;

	/** 이벤트 감사 로그를 완료 상태로합니다 */
	public AuditEvent finish(final SystemRepository rep) {
		LocalDateTime now = rep.dh().time().date();
		setStatusType(ActionStatusType.Processed);
		setEndDate(now);
		setTime(DateUtils.between(startDate, endDate).get().toMillis());
		return update(rep);
	}

	/** 이벤트 감사 로그를 취소 상태로합니다 */
	public AuditEvent cancel(final SystemRepository rep, String errorReason) {
		LocalDateTime now = rep.dh().time().date();
		setStatusType(ActionStatusType.Cancelled);
		setErrorReason(StringUtils.abbreviate(errorReason, 250));
		setEndDate(now);
		setTime(DateUtils.between(startDate, endDate).get().toMillis());
		return update(rep);
	}

	/** 이벤트 감사 로그를 예외 상태에 있습니다. */
	public AuditEvent error(final SystemRepository rep, String errorReason) {
		LocalDateTime now = rep.dh().time().date();
		setStatusType(ActionStatusType.Error);
		setErrorReason(StringUtils.abbreviate(errorReason, 250));
		setEndDate(now);
		setTime(DateUtils.between(startDate, endDate).get().toMillis());
		return update(rep);
	}

	/** 이벤트 감사 로그를 등록합니다。 */
	public static AuditEvent register(final SystemRepository rep, final RegAuditEvent p) {
		return p.create(rep.dh().time().date()).save(rep);
	}

	/** 이벤트 감사 로그를 검색합니다. */
	public static PagingList<AuditEvent> find(final SystemRepository rep, final FindAuditEvent p) {
		return rep.tmpl().find(AuditEvent.class, (criteria) -> {
			return criteria
					.equal("category", p.category)
					.equal("statusType", p.statusType)
					.like(new String[] { "message", "errorReason" }, p.keyword, MatchMode.ANYWHERE)
					.between("startDate", p.fromDay.atStartOfDay(), DateUtils.dateTo(p.toDay));
		}, p.page.sortIfEmpty(SortOrder.desc("startDate")));
	}

	/** 검색 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindAuditEvent implements Dto {
		private static final long serialVersionUID = 1l;
		@NameEmpty
		private String category;
		@DescriptionEmpty
		private String keyword;
		private ActionStatusType statusType;
		@ISODate
		private LocalDate fromDay;
		@ISODate
		private LocalDate toDay;
		@NotNull
		private Pagination page = new Pagination();
	}

	/** 등록 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegAuditEvent implements Dto {
		private static final long serialVersionUID = 1l;
		@NameEmpty
		private String category;
		private String message;

		public AuditEvent create(LocalDateTime now) {
			AuditEvent event = new AuditEvent();
			event.setCategory(category);
			event.setMessage(message);
			event.setStatusType(ActionStatusType.Processing);
			event.setStartDate(now);
			return event;
		}

		public static RegAuditEvent of(String message) {
			return of("default", message);
		}

		public static RegAuditEvent of(String category, String message) {
			return new RegAuditEvent(category, message);
		}
	}

}
