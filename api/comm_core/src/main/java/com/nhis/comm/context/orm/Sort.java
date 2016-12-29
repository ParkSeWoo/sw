package com.nhis.comm.context.orm;

import com.nhis.comm.context.Dto;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sewoo on 2016. 12. 28..
 * 정렬 정보를 표현합니다.
 * 여러 개 정렬 정보 (SortOrder)을 내포합니다.
 */

@Data
public class Sort implements Dto {
	private static final long serialVersionUID = 1L;

	/** 정렬 조건 */
	private final List<SortOrder> orders = new ArrayList<SortOrder>();

	/** 정렬 조건을 추가합니다. */
	public Sort add(SortOrder order) {
		orders.add(order);
		return this;
	}

	/**  정렬 기준 (오름차순)를 추가합니다. */
	public Sort asc(String property) {
		return add(SortOrder.asc(property));
	}

	/** 정렬 조건 (내림차순)를 추가합니다. */
	public Sort desc(String property) {
		return add(SortOrder.desc(property));
	}

	/**  정렬 조건 목록을 반환합니다.  */
	public List<SortOrder> orders() {
		return orders;
	}

	/**  정렬 조건이 지정되지 않은이었다 때 정렬 순서가 덮어 씁니다. */
	public Sort ifEmpty(SortOrder... items) {
		if (orders.isEmpty() && items != null) {
			orders.addAll(Arrays.asList(items));
		}
		return this;
	}

	/** 오름차순으로 정렬 정보를 반환합니다. */
	public static Sort ascBy(String property) {
		return new Sort().asc(property);
	}

	/**  내림차순으로 정렬 정보를 반환합니다. */
	public static Sort descBy(String property) {
		return new Sort().desc(property);
	}

	/** 필드 단위의 정렬 정보를 표현합니다. */
	@Value
	public static class SortOrder implements Serializable {
		private static final long serialVersionUID = 1L;
		private String property;
		private boolean ascending;

		public static SortOrder asc(String property) {
			return new SortOrder(property, true);
		}

		public static SortOrder desc(String property) {
			return new SortOrder(property, false);
		}
	}


}
