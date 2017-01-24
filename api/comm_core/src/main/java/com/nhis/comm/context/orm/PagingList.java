package com.nhis.comm.context.orm;

import com.nhis.comm.context.Dto;
import lombok.Value;

import java.util.List;

/**
 * Created by sewoo on 2016. 12. 28..
 * 페이징 목록을 표현합니다.
 *
 * @param <T> 결과 개체 (목록의 요소)
 */

@Value
public class PagingList<T> implements Dto {
	private static final long serialVersionUID = 1L;

	private List<T> list;
	private Pagination page;

}
