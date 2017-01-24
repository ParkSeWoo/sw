package com.nhis.comm.controller;

import com.nhis.comm.context.paths.PathFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by sewoo on 2016. 12. 27..
 */

@RestController
public class RestErrorController implements ErrorController {

	@Autowired(required = false)
	private ErrorAttributes errorAttributes;

	@PostConstruct
	public RestErrorController build() {
		if (errorAttributes == null) {
			errorAttributes = new DefaultErrorAttributes();
		}
		return this;
	}

	@Override
	public String getErrorPath() {
		return PathFactory.PATH_ERROR;
	}

	@RequestMapping(PathFactory.PATH_ERROR)
	public Map<String, Object> error(HttpServletRequest request) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return this.errorAttributes.getErrorAttributes(requestAttributes, false);
	}
}