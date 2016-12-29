package com.nhis.comm.context.rest;

import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.actor.ActorSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 27..
 */
public class RestActorSessionInterceptor implements ClientHttpRequestInterceptor {

	public static final String AttrActorSession = "ActorSession";

	private final ActorSession session;

	public RestActorSessionInterceptor(ActorSession session) {
		this.session = session;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
																								throws IOException {
		request.getHeaders().add(AttrActorSession, RestActorSessionConverter.convert(session.actor()));
		return execution.execute(request, body);
	}

	/** HTTP 헤더에 ActorSession을 적용할 때의 변환 조건을 표현합니다.。 */
	public static class RestActorSessionConverter {
		private static final Logger logger = LoggerFactory.getLogger(RestActorSessionConverter.class);
		public static String convert(Actor actor) {
			return String.join("_", actor.getId(), actor.getName(), actor.getRoleType().name(),
					actor.getLocale().getLanguage(),
					Optional.ofNullable(actor.getChannel()).orElse("none"),
					Optional.ofNullable(actor.getSource()).orElse("none"));
		}
		public static Actor convert(String actorStr) {
			String[] params = actorStr.split("_");
			if (params.length != 6) {
				logger.warn("Actor로 변환하는데 실패하였습니다. ");
				return Actor.Anonymous;
			}
			return new Actor(params[0], params[1], Actor.ActorRoleType.valueOf(params[2]),
					new Locale(params[3]), params[4], params[5]);
		}
	}
}
