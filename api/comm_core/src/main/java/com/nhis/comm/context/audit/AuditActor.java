package com.nhis.comm.context.audit;

import com.nhis.comm.ActionStatusType;
import com.nhis.comm.context.Dto;
import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.orm.*;
import com.nhis.comm.model.constraints.*;
import com.nhis.comm.util.ConvertUtils;
import com.nhis.comm.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by sewoo on 2016. 12. 29..
 *
 * 시스템 이용자의 감사 로그를 표현합니다
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class AuditActor extends OrmActiveRecord<AuditActor> {
	private static final long serialVersionUID = 1l;

	@Id
	@GeneratedValue
	private Long id;
	/** 이용자 ID */
	@IdStr
	private String actorId;
	/** 이용자 역할 */
	@NotNull
	@Enumerated(EnumType.STRING)
	private Actor.ActorRoleType roleType;
	/** 이용자 소스 (IP 등) */
	private String source;
	/** 카테고리 */
	@Category
	private String category;
	/** 메세지 */
	private String message;
	/** 처리 상태 */
	@NotNull
	@Enumerated(EnumType.STRING)
	private ActionStatusType statusType;
	/** 오류 사유 */
	@DescriptionEmpty
	private String errorReason;
	/** 처리시간(msec) */
	private Long time;
	/** 시작시간 */
	@NotNull
	private LocalDateTime startDate;
	/** 종료 시간 (미완료시는 null) */
	private LocalDateTime endDate;

	/** 이용자 감사 로그를 완료 상태로합니다. */
	public AuditActor finish(final SystemRepository rep) {
		LocalDateTime now = rep.dh().time().date();
		setStatusType(ActionStatusType.Processed);
		setEndDate(now);
		setTime(DateUtils.between(startDate, endDate).get().toMillis());
		return update(rep);
	}

	/** 이용자 감사 로그를 취소 상태로합니다. */
	public AuditActor cancel(final SystemRepository rep, String errorReason) {
		LocalDateTime now = rep.dh().time().date();
		setStatusType(ActionStatusType.Cancelled);
		setErrorReason(StringUtils.abbreviate(errorReason, 250));
		setEndDate(now);
		setTime(DateUtils.between(startDate, endDate).get().toMillis());
		return update(rep);
	}

	/** 이용자 감사 로그를 예외 상태로합니다. */
	public AuditActor error(final SystemRepository rep, String errorReason) {
		LocalDateTime now = rep.dh().time().date();
		setStatusType(ActionStatusType.Error);
		setErrorReason(StringUtils.abbreviate(errorReason, 250));
		setEndDate(now);
		setTime(DateUtils.between(startDate, endDate).get().toMillis());
		return update(rep);
	}

	/** 이용자 감사 로그를 등록합니다 */
	public static AuditActor register(final SystemRepository rep, final RegAuditActor p) {
		return p.create(rep.dh().actor(), rep.dh().time().date()).save(rep);
	}

	/** 이용자 감사 로그를 검색합니다 */
	public static PagingList<AuditActor> find(final SystemRepository rep, final FindAuditActor p) {
		return rep.tmpl().find(AuditActor.class, (criteria) -> {
			return criteria
					.like(new String[] { "actorId", "source" }, p.actorId, MatchMode.ANYWHERE)
					.equal("category", p.category)
					.equal("roleType", p.roleType)
					.equal("statusType", p.statusType)
					.like(new String[] { "message", "errorReason" }, p.keyword, MatchMode.ANYWHERE)
					.between("startDate", p.fromDay.atStartOfDay(), DateUtils.dateTo(p.toDay));
		}, p.page.sortIfEmpty(Sort.SortOrder.desc("startDate")));
	}

	/** 검색 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindAuditActor implements Dto {
		private static final long serialVersionUID = 1l;
		@IdStrEmpty
		private String actorId;
		@CategoryEmpty
		private String category;
		@DescriptionEmpty
		private String keyword;
		@NotNull
		private Actor.ActorRoleType roleType = Actor.ActorRoleType.User;
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
	public static class RegAuditActor implements Dto {
		private static final long serialVersionUID = 1l;
		private String category;
		private String message;

		public AuditActor create(final Actor actor, LocalDateTime now) {
			AuditActor audit = new AuditActor();
			audit.setActorId(actor.getId());
			audit.setRoleType(actor.getRoleType());
			audit.setSource(actor.getSource());
			audit.setCategory(category);
			audit.setMessage(ConvertUtils.left(message, 300));
			audit.setStatusType(ActionStatusType.Processing);
			audit.setStartDate(now);
			return audit;
		}

		public static RegAuditActor of(String message) {
			return of("default", message);
		}

		public static RegAuditActor of(String category, String message) {
			return new RegAuditActor(category, message);
		}
	}

}
