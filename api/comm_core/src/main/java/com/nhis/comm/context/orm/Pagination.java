package com.nhis.comm.context.orm;

import com.nhis.comm.context.Dto;
import com.nhis.comm.util.Calculator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.RoundingMode;

/**
 * Created by sewoo on 2016. 12. 28..
 * 페이징 정보를 표현 합니다.
 */

@Data
@AllArgsConstructor
public class Pagination implements Dto {

	private static final long serialVersionUID = 1l;
	public static final int DefaultSize = 100;

	/** 페이지 수 (시작) */
	private int page;
	/** 페이지 당 건수 */
	private int size;
	/** 총 건수 */
	private Long total;
	/**  총 건수 산출을 무시 유무 */
	private boolean ignoreTotal;
	/** 정렬 조건 */
	private Sort sort;

	public Pagination() {
		this(1);
	}

	public Pagination(int page) {
		this(page, DefaultSize, null, false, new Sort());
	}

	public Pagination(int page, int size) {
		this(page, size, null, false, new Sort());
	}

	public Pagination(int page, int size, final Sort sort) {
		this(page, size, null, false, sort);
	}

	public Pagination(final Pagination req, long total) {
		this(req.getPage(), req.getSize(), total, false, req.getSort());
	}

	/** 계산 산출을 비활성화합니다. */
	public Pagination ignoreTotal() {
		this.ignoreTotal = true;
		return this;
	}

	/** 정렬 지정이 지정되지 않은 경우에는 준 정렬 조건보다 우선합니다. */
	public Pagination sortIfEmpty(Sort.SortOrder... orders) {
		if (sort != null)
			sort.ifEmpty(orders);
		return this;
	}

	/** 최대 페이지 수를 반환합니다. total 설정시에만 적절한 값이 반환됩니다. */
	public int getMaxPage() {
		return (total == null) ? 0 : Calculator.of(total)
				.scale(0, RoundingMode.UP).divideBy(size).intValue();
	}

	/** 시작 건수를 반환합니다. */
	public int getFirstResult() {
		return (page - 1) * size;
	}


}
