package com.nhis.comm.context.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhis.comm.context.Dto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by doldol on 2016. 12. 22..
 */
public class RestInvoker {

	/** 내부에서 이용하는 RestTemplate */
	private final RestTemplate template;
	/** 크리에이티브 메타 생성을 위한 오브젝트 콘 벡터 */
	private final ObjectMapper mapper;
	/** 실행시간의 루트 URL */
	private final String rootUrl;

	/** construct (생성자) */
	public RestInvoker(RestTemplate template, ObjectMapper mapper, String rootUrl) {
		this.template = template;
		this.mapper = mapper;
		this.rootUrl = rootUrl;
	}

	/** API에 대한 요청이 있을경우。*/
	public <T> T get(String path, Class<T> responseType, Object... variables) {
		return template.getForObject(servicePath(path), responseType, variables);
	}

	/** 지정한 패스에 접속하기 전에 URL을 첨부하고 답장
	 *  <p>패스는 「/」을 맨 먼저 기술하였음
	 */
	public String servicePath(String path) {
		return Optional.ofNullable(this.rootUrl).orElse("") + path;
	}

	 /** API 에대한 GET 요구가 있을경우。
      * <p>이전값의 총 요구사항이 있을경우
	  */
	public <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables).getBody();
	}

	/**
	 * API 에대한 GET 요구가 있을경우。
	 * <p>request 설정된 bean 프로퍼티-값에서 path 결합됩니다
	 */
	public <T> T get(String path, Class<T> responseType, Dto request, Object... variables) {
		return template.getForObject(servicePath(path, request), responseType, variables);
	}

	/**
	 * 지정하신 패스에 접속하기 전에 URL과 request parameter를 끝에 부여해서 돌려줍니다。
	 * <P>path는 「/」을 맨 먼저 기술하였습니다。
	 * <p>레크리에이션 매개 변수는 간단하게 객체를 지원하는것이고 계층구조는 복잡하지 않는 것처럼 주의해야 합니다。
	 */
	@SuppressWarnings("unchecked")
	public String servicePath(String path, Dto request) {
		if (request == null) {
			return servicePath(path);
		} else {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(servicePath(path));
			mapper.convertValue(request, Map.class).forEach((k, v) ->
					buildQuery(builder, k.toString(), v));
			return builder.build().toUriString();
		}
	}

	protected void buildQuery(UriComponentsBuilder builder, String key, Object value) {
		if (value == null) return;
		if (value.getClass().isArray()) {
			if (((Object[])value).length == 0) return;
			builder.queryParam(key, (Object[])value);
		} else if (value instanceof List) {
			if (((List<?>)value).isEmpty()) return;
			builder.queryParam(key, ((List<?>)value).toArray());
		} else if (value instanceof Map) {
			((Map<?, ?>)value).forEach((k, v) ->
					buildQuery(builder, key + "." + k, v));
		} else {
			builder.queryParam(key, value);
		}
	}

	/**
	 * API 에대해 GET 요청합니다。
	 * <p>request 에설정된 bean은 속성값 쌍으로 path에 결합됩니다
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오
	 */
	public <T> T get(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path, request), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables).getBody();
	}

	/**
	 * API에대해 GET 요청합니다。
	 */
	public <T> ResponseEntity<T> getEntity(String path, Class<T> responseType, Object... variables) {
		return template.getForEntity(servicePath(path), responseType, variables);
	}

	/**
	 * API에대해 GET 요청합니다。
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오.
	 */
	public <T> ResponseEntity<T> getEntity(String path, ParameterizedTypeReference<T> responseType, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables);
	}

	/**
	 * API에대해 GET 요청합니다。
	 * <p>request 설정된 bean 프로퍼티-값에서 path 결합되었습니다.
	 */
	public <T> ResponseEntity<T> getEntity(String path, Class<T> responseType, Dto request, Object... variables) {
		return template.getForEntity(servicePath(path, request), responseType, variables);
	}

	/**
	 * API에대해 GET 요청합니다。
	 * <p>request 설정된 bean 프로퍼티-값에서 path 결합되었습니다.
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오.
	 */
	public <T> ResponseEntity<T> getEntity(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path, request), HttpMethod.GET, HttpEntity.EMPTY, responseType, variables);
	}

	/**
	 * API에대해 GET 요청합니다。
	 * <p>request 값은 application / json으로 요구됩니다. 수신자는 반드시 바인드 변수에 & # 64; RequestBody을 부여합니다.
	 */
	public <T> T post(String path, Class<T> responseType, Dto request, Object... variables) {
		return template.postForObject(servicePath(path), request, responseType, variables);
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / json으로 요구됩니다. 수신자는 반드시 바인드 변수에 & # 64; RequestBody을 부여합니다.
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오.
	 */
	public <T> T post(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.POST, new HttpEntity<>(request), responseType, variables).getBody();
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / json으로 요구됩니다. 수신자는 반드시 바인드 변수에 & # 64; RequestBody을 부여합니다.
	 */
	public <T> ResponseEntity<T> postEntity(String path, Class<T> responseType, Dto request, Object... variables) {
		return template.postForEntity(servicePath(path), request, responseType, variables);
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / json으로 요구됩니다. 수신자는 반드시 바인드 변수에 & # 64; RequestBody을 부여합니다.
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오
	 */
	public <T> ResponseEntity<T> postEntity(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.POST, new HttpEntity<>(request), responseType, variables);
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / x-www-form-urlencoded로 요구됩니다
	 */
	public <T> T postForm(String path, Class<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.POST,
				formEntity(request), responseType, variables).getBody();
	}

	@SuppressWarnings("unchecked")
	protected HttpEntity<MultiValueMap<String, Object>> formEntity(Dto request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		mapper.convertValue(request, Map.class).forEach((k, v) ->
				buildMap(map, k.toString(), v));
		return new HttpEntity<>(map, headers);
	}

	@SuppressWarnings("unchecked")
	protected void buildMap(MultiValueMap<String, Object> map, String key, Object value) {
		if (value == null) return;
		if (value.getClass().isArray()) {
			if (((Object[])value).length == 0) return;
			map.put(key, Arrays.asList((Object[])value).stream()
					.map(Object::toString)
					.collect(Collectors.toList()));
		} else if (value instanceof List) {
			if (((List<Object>)value).isEmpty()) return;
			map.put(key, (List<Object>)value).stream()
					.map(Object::toString)
					.collect(Collectors.toList());
		} else if (value instanceof Map) {
			((Map<?, ?>)value).forEach((k, v) ->
					buildMap(map, key + "." + k, v));
		} else {
			map.add(key, value.toString());
		}
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / x-www-form-urlencoded로 요구됩니다
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오.
	 */
	public <T> T postForm(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.POST,
				formEntity(request), responseType, variables).getBody();
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / x-www-form-urlencoded로 요구됩니다
	 */
	public <T> ResponseEntity<T> postFormEntity(String path, Class<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.POST,
				formEntity(request), responseType, variables);
	}

	/**
	 * API에대해 POST 요청합니다。
	 * <p>request 값은 application / x-www-form-urlencoded로 요구됩니다
	 * <p>반환 값 총칭 형을 필요로 할 때는 이곳을 이용하십시오.
	 */
	public <T> ResponseEntity<T> postFormEntity(String path, ParameterizedTypeReference<T> responseType, Dto request, Object... variables) {
		return template.exchange(servicePath(path), HttpMethod.POST,
				formEntity(request), responseType, variables);
	}



}
