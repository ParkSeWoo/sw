package com.nhis.comm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhis.comm.context.rest.RestInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Created by doldol on 2016. 12. 22..
 */
public abstract class RestInvokerSupport {

	public abstract String applicationName();

	public String rootPath() {
		return null;
	}

	@Autowired //rest http req send res
	@LoadBalanced
	protected RestTemplate template;

	@Autowired //json mapper
	protected ObjectMapper mapper;

	//null point exception 방지
	private Optional<RestInvoker> invoker = Optional.empty();

	/** 초기화 작업을 수행 합니다。 */
	@PostConstruct
	public void initialize() {
		invoker = Optional.of(new RestInvoker(template, mapper, rootUrl()));
	}

	/** API 연결 경로의 URL을 반환합니다。 */
	protected String rootUrl() {
		return String.format("http://%s%s", applicationName(), Optional.ofNullable(rootPath()).orElse(""));
	}

	/** Ribbon 을이용한 RestInvoker 을반환합니다。 */
	protected RestInvoker invoker() {
		return invoker.orElseThrow(() -> new IllegalStateException("먼저 initialize 메소드를 호출해주세요"));
	}
}
