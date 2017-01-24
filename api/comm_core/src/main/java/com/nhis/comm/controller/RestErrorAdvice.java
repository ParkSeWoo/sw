package com.nhis.comm.controller;

/**
 * Created by sewoo on 2017. 1. 12..
 *
 */

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import com.nhis.comm.ValidationException;
import com.nhis.comm.ValidationException.*;
import com.nhis.comm.context.actor.ActorSession;

/**
 * REST에 대한 예외 Map 변환 지원.
 * <p>AOP 조언 모든 RestController에 대해 예외 처리를 맞추고 포함 합니다
 */
@ControllerAdvice(annotations = RestController.class)
public class RestErrorAdvice {

	protected Log log = LogFactory.getLog(getClass());

	@Autowired
	private MessageSource msg;
	@Autowired
	private ActorSession session;

	/** Servlet 예외 */
	@ExceptionHandler(ServletRequestBindingException.class)
	public ResponseEntity<Map<String, String[]>> handleServletRequestBinding(ServletRequestBindingException e) {
		log.warn(e.getMessage());
		return new ErrorHolder(msg, locale(), "error.ServletRequestBinding").result(HttpStatus.BAD_REQUEST);
	}

	private Locale locale() {
		return session.actor().getLocale();
	}

	/** 미디어 유형 불일치 예외 */
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ResponseEntity<Map<String, String[]>> handleHttpMediaTypeNotAcceptable(
			HttpMediaTypeNotAcceptableException e) {
		log.warn(e.getMessage());
		return new ErrorHolder(msg, locale(), "error.HttpMediaTypeNotAcceptable").result(HttpStatus.BAD_REQUEST);
	}

	/** 낙관적 독점적 (Hibernate 버전 체크) 예외 */
	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<Map<String, String[]>> handleOptimisticLockingFailureException(
			OptimisticLockingFailureException e) {
		log.warn(e.getMessage(), e);
		return new ErrorHolder(msg, locale(), "error.OptimisticLockingFailure").result(HttpStatus.BAD_REQUEST);
	}

	/** 권한예외 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String[]>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn(e.getMessage());
		return new ErrorHolder(msg, locale(), ErrorKeys.AccessDenied).result(HttpStatus.UNAUTHORIZED);
	}

	/** 지정한 정보가 존재하지 않는 예외 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Map<String, String[]>> handleEntityNotFoundException(EntityNotFoundException e) {
		log.warn(e.getMessage(), e);
		return new ErrorHolder(msg, locale(), ErrorKeys.EntityNotFound).result(HttpStatus.BAD_REQUEST);
	}

	/** BeanValidation (JSR303) 제한 예외 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String[]>> handleConstraintViolation(ConstraintViolationException e) {
		log.warn(e.getMessage());
		Warns warns = Warns.init();
		e.getConstraintViolations().forEach((v) -> warns.add(v.getPropertyPath().toString(), v.getMessage()));
		return new ErrorHolder(msg, locale(), warns.list()).result(HttpStatus.BAD_REQUEST);
	}

	/** Controller에 대한 요청 끈 부착 예외 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<Map<String, String[]>> handleBind(BindException e) {
		log.warn(e.getMessage());
		Warns warns = Warns.init();
		e.getAllErrors().forEach((oe) -> {
			String field = "";
			if (1 == oe.getCodes().length) {
				field = bindField(oe.getCodes()[0]);
			} else if (1 < oe.getCodes().length) {
				//low : 접두사는 중복이므로 분리합니다
				field = bindField(oe.getCodes()[1]);
			}
			List<String> args = Arrays.stream(oe.getArguments())
					.filter((arg) -> !(arg instanceof MessageSourceResolvable))
					.map(Object::toString)
					.collect(Collectors.toList());
			String message = oe.getDefaultMessage();
			if (0 <= oe.getCodes()[0].indexOf("typeMismatch")) {
				message = oe.getCodes()[2];
			}
			warns.add(field, message, args.toArray(new String[0]));
		});
		return new ErrorHolder(msg, locale(), warns.list()).result(HttpStatus.BAD_REQUEST);
	}

	protected String bindField(String field) {
		return Optional.ofNullable(field).map((v) -> v.substring(v.indexOf('.') + 1)).orElse("");
	}

	/** RestTemplate 예외시 브리지 지원 */
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<String> handleHttpClientError(HttpClientErrorException e) {
		HttpHeaders headers = new HttpHeaders();
		headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(e.getResponseBodyAsString(), headers, e.getStatusCode());
	}

	/** 응용 프로그램 예외 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Map<String, String[]>> handleValidation(ValidationException e) {
		log.warn(e.getMessage());
		return new ErrorHolder(msg, locale(), e).result(HttpStatus.BAD_REQUEST);
	}

	/** IO 예외 (Tomcat의 Broken pipe는 서버 측의 책임이 아니므로 제외합니다) */
	@ExceptionHandler(IOException.class)
	public ResponseEntity<Map<String, String[]>> handleIOException(IOException e) {
		if (e.getMessage() != null && e.getMessage().contains("Broken pipe")) {
			log.info("클라이언트 사유로 처리가 중단되었습니다.");
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return handleException(e);
		}
	}

	/** 일반예외 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String[]>> handleException(Exception e) {
		log.error("예기치 않은 예외가 발생했습니다.", e);
		return new ErrorHolder(msg, locale(), ErrorKeys.Exception, "서버 측에서 문제가 발생했을 가능성이 있습니다.\n")
				.result(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 예외 정보의 스택을 표현합니다.
	 * <p>막힌 예외 정보는 {@link #result (HttpStatus)}를 호출하여 Map을 가진 ResponseEntity로 변환 가능합니다.
	 * Map의 key는 filed 지정 값 value는 메시지 키의 변환 값 (messages-validation.properties)가 들어갑니다.
	 * <p>{@link #errorGlobal}로 등록한 경우 키는 공입니다.
	 * <p>클라이언트는 반환 값을 [{ "fieldA": "messageA"}, { "fieldB": "messageB"}]로받습니다.
	 */
	public static class ErrorHolder {
		private Map<String, List<String>> errors = new HashMap<>();
		private MessageSource msg;
		private Locale locale;

		public ErrorHolder(final MessageSource msg, final Locale locale) {
			this.msg = msg;
			this.locale = locale;
		}

		public ErrorHolder(final MessageSource msg, final Locale locale, final ValidationException e) {
			this(msg, locale, e.list());
		}

		public ErrorHolder(final MessageSource msg, final Locale locale, final List<Warn> warns) {
			this.msg = msg;
			this.locale = locale;
			warns.forEach((warn) -> {
				if (warn.global())
					errorGlobal(warn.getMessage());
				else
					error(warn.getField(), warn.getMessage(), warn.getMessageArgs());
			});
		}

		public ErrorHolder(final MessageSource msg, final Locale locale, String globalMsgKey, String... msgArgs) {
			this.msg = msg;
			this.locale = locale;
			errorGlobal(globalMsgKey, msgArgs);
		}

		/** 글로벌 예외 (필드 키가 비어)를 추가합니다. */
		public ErrorHolder errorGlobal(String msgKey, String defaultMsg, String... msgArgs) {
			if (!errors.containsKey(""))
				errors.put("", new ArrayList<>());
			errors.get("").add(msg.getMessage(msgKey, msgArgs, defaultMsg, locale));
			return this;
		}

		/** 글로벌 예외 (필드 키가 비어)를 추가합니다. */
		public ErrorHolder errorGlobal(String msgKey, String... msgArgs) {
			return errorGlobal(msgKey, msgKey, msgArgs);
		}

		/** 필드 단위의 예외를 추가합니다. */
		public ErrorHolder error(String field, String msgKey, String... msgArgs) {
			if (!errors.containsKey(field))
				errors.put(field, new ArrayList<>());
			errors.get(field).add(msg.getMessage(msgKey, msgArgs, msgKey, locale));
			return this;
		}

		/** 보유하는 예외 정보를 ResponseEntity로 변환합니다. */
		public ResponseEntity<Map<String, String[]>> result(HttpStatus status) {
			return new ResponseEntity<Map<String, String[]>>(
					errors.entrySet().stream().collect(Collectors.toMap(
							Map.Entry::getKey, (entry) -> entry.getValue().toArray(new String[0]))),
					status);
		}
	}

}

