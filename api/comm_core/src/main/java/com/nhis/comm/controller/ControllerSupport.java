package com.nhis.comm.controller;

import com.nhis.comm.ValidationException;
import com.nhis.comm.context.ResourceBundleHandler;
import com.nhis.comm.context.actor.ActorSession;
import com.nhis.comm.context.report.ReportFile;
import com.nhis.comm.context.Timestamper;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by sewoo on 2016. 12. 27..
 * @Controller 를 지원하는 부모 class 입니다.
 */

@Setter
public class ControllerSupport {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource msg;
	@Autowired
	private ResourceBundleHandler label;
	@Autowired
	private Timestamper time;
	@Autowired
	private ActorSession session;

	/** i18n메시지 변환을 수행 합니다。 */
	protected String msg(String message) {
		return msg(message, session.actor().getLocale());
	}

	protected String msg(String message, final Locale locale) {
		return msg.getMessage(message, new String[0], locale);
	}

	/** 리소스 파일 (basename.properties)의 키 / 값 MAP 정보를 반환합니다
	 *  API 호츨 측에서 i18n 대응을 하고 싶을 때 등에 이용하십시오.
	 */
	protected Map<String, String> labels(String basename) {
		return labels(basename, session.actor().getLocale());
	}

	protected Map<String, String> labels(String basename, final Locale locale) {
		return label.labels(basename, locale);
	}

	/** 메시지 리소스 접근자를 반환합니다。 */
	protected MessageSource msgResource() {
		return msg;
	}

	/** 일시 유틸리티를 반환 합니다。 */
	protected Timestamper time() {
		return time;
	}

	/**
	 * 지정된 키/값을 Map으로 변환 합니다。
	 * get등으로 null을 반환 할 수 있을때 이 메소드 Map화 후 반환합니다。
	 * ※null는 JSON 바인딩되지 않기 때문에, 클라이언트 측에서 Status가 200에도 예외 취급 될 가능성이 있습니다。
	 */
	protected <T> Map<String, T> objectToMap(String key, final T t) {
		Map<String, T> ret = new HashMap<>();
		ret.put(key, t);
		return ret;
	}

	protected <T> Map<String, T> objectToMap(final T t) {
		return objectToMap("result", t);
	}

	/** 반환값을 생성해 돌려 줍니다。(반환값이 null을 혀용 할 때는 이곳을 이용 하세요.) */
	protected <T> ResponseEntity<T> result(Supplier<T> command) {
		return ResponseEntity.status(HttpStatus.OK).body(command.get());
	}

	protected ResponseEntity<Void> resultEmpty(Runnable command) {
		command.run();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/** 파일 업로드 정보(MultipartFile)를 ReportFile로 변환 합니다。 */
	protected ReportFile uploadFile(final MultipartFile file) {
		return uploadFile(file, (String[]) null);
	}

	/**
	 * 파일 업로드 정보(MultipartFile)를 ReportFile로 변환 합니다。
	 * <p>acceptExtensions를 허용하는 파일 확장자(소문자 통일)을 설정하십시오。
	 */
	protected ReportFile uploadFile(final MultipartFile file, final String... acceptExtensions) {
		String fname = StringUtils.lowerCase(file.getOriginalFilename());
		if (acceptExtensions != null && !FilenameUtils.isExtension(fname, acceptExtensions)) {
			throw new ValidationException("file", "업로드 파일은 [{0}]을 지정하십시오.",
					new String[] { StringUtils.join(acceptExtensions) });
		}
		try {
			return new ReportFile(file.getOriginalFilename(), file.getBytes());
		} catch (IOException e) {
			throw new ValidationException("file", "업로드 파일의 분석에 실패 했습니다.");
		}
	}

	/**
	 * 파일 다운로드 설정。
	 * <p>이용할 때는 반환 값을 void로 정의。
	 */
	protected void exportFile(final HttpServletResponse res, final ReportFile file) {
		exportFile(res, file, MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	protected void exportFile(final HttpServletResponse res, final ReportFile file, final String contentType) {
		String filename;
		try {
			filename = URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20");
		} catch (Exception e) {
			throw new ValidationException("파일 이름이 잘못되었습니다.");
		}
		res.setContentLength(file.size());
		res.setContentType(contentType);
		res.setHeader("Content-Disposition",
				"attachment; filename=" + filename);
		try {
			IOUtils.write(file.getData(), res.getOutputStream());
		} catch (IOException e) {
			throw new ValidationException("파일 출력에 실패했습니다.");
		}
	}

}
