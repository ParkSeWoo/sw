package com.nhis.comm.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by doldol on 2016. 12. 22..
 */

@Slf4j
@Component
public class ApplicationInitializer implements ApplicationRunner, ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

	}


	@Override
	public void run(ApplicationArguments args) throws Exception {

	}
}
