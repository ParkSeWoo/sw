package com.nhis.comm.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

/**
 * Created by doldol on 2016. 12. 27..
 */
public class RestExporterSupport {

	/** 반환 값을 생성해 돌려줍니다. (반환 값이 원시 또는 null을 허용 할 때는 이곳을 이용하십시오) */
	protected <T> ResponseEntity<T> result(Supplier<T> command) {
		return ResponseEntity.status(HttpStatus.OK).body(command.get());
	}

	/** 반환값을 생성해 돌려 줍니다。 */
	protected ResponseEntity<Void> resultEmpty(Runnable command) {
		command.run();
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
