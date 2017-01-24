package com.nhis.comm.context;

import com.nhis.comm.context.orm.OrmActiveRecord;
import com.nhis.comm.context.orm.OrmRepository;
import com.nhis.comm.model.constraints.OutlineEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.criterion.MatchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 28..
 *
 * 응용 프로그램 설정 정보를 표현합니다.
 * <p> 미리 초기 데이터가 등록되는 것을 전제로하고 값만 변경 허용합니다.
 */
@javax.persistence.Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AppSetting extends OrmActiveRecord<AppSetting> {
	private static final long serialVersionUID = 1l;

	/**
	 * 설정ID
	 */
	@Id
	@Size(max = 120)
	private String id;
	/**
	 * 구분
	 */
	@Size(max = 60)
	private String category;
	/**
	 * 요약
	 */
	@Size(max = 1300)
	private String outline;
	/**
	 * 값
	 */
	@Size(max = 1300)
	private String value;

	/**
	 * 설정 정보 값을 가져옵니다.
	 */
	public String str() {
		return value;
	}

	public String str(String defaultValue) {
		return value == null ? defaultValue : value;
	}

	public int intValue() {
		return Integer.parseInt(value);
	}

	public int intValue(int defaultValue) {
		return value == null ? defaultValue : Integer.parseInt(value);
	}

	public long longValue() {
		return Long.parseLong(value);
	}

	public long longValue(long defaultValue) {
		return value == null ? defaultValue : Long.parseLong(value);
	}

	public boolean bool() {
		return Boolean.parseBoolean(value);
	}

	public boolean bool(OrmRepository rep, boolean defaultValue) {
		return value == null ? defaultValue : Boolean.parseBoolean(value);
	}

	public BigDecimal decimal() {
		return new BigDecimal(value);
	}

	public BigDecimal decimal(BigDecimal defaultValue) {
		return value == null ? defaultValue : new BigDecimal(value);
	}

	/**
	 * 설정정보값을 가져옵니다。
	 */
	public static Optional<AppSetting> get(OrmRepository rep, String id) {
		return rep.get(AppSetting.class, id);
	}

	public static AppSetting load(OrmRepository rep, String id) {
		return rep.load(AppSetting.class, id);
	}

	/**
	 * 설정 정보 값을 설정합니다。
	 */
	public AppSetting update(OrmRepository rep, String value) {
		setValue(value);
		return update(rep);
	}

	/**
	 * 응용 프로그램 설정 정보를 검색합니다.
	 */
	public static List<AppSetting> find(OrmRepository rep, FindAppSetting p) {
		return rep.tmpl().find(AppSetting.class, (criteria) -> {
			return criteria
					.like(new String[]{"id", "category", "outline"}, p.keyword, MatchMode.ANYWHERE)
					.result();
		});
	}

	/**
	 * 검색 매개 변수
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindAppSetting implements Dto {
		private static final long serialVersionUID = 1l;
		@OutlineEmpty
		private String keyword;
	}
}