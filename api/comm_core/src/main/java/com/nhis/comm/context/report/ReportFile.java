package com.nhis.comm.context.report;

import com.nhis.comm.context.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by sewoo on 2016. 12. 27..
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportFile implements Dto {
	private static final long serialVersionUID = 1L;
	private String name;
	private byte[] data;

	public int size() {
		return data.length;
	}
}
