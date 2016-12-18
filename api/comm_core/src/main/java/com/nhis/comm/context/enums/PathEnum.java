package com.nhis.comm.context.enums;

/**
 * Created by sewoo on 2016. 12. 16..
 */
public enum PathEnum {
	MAIN_PATH("com.nhis.api"),
	COMM_PATH("com.nhis.comm")
	;

	private final String paths;

	//construct
	PathEnum(String cho) {
		paths = cho;
	}

	public String getSpan() {
		return paths;
	}

}
