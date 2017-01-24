package com.nhis.comm.context.rest;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.GenericFilterBean;

import com.nhis.comm.context.actor.ActorSession;
import com.nhis.comm.context.rest.RestActorSessionInterceptor.RestActorSessionConverter;


/**
 * 프로세스간에 ActorSession 을 선점하는 Filter。 (받는측)
 * <p>미리 요청하는 프로세스 RestActorSessionInterceptor을 적용해야 합니다。
 * <p>비동기 Servlet 을 이용할경우 이용이 불가능。( 별도 유사한 구조를 코딩 해야 합니다. )
 */
public class RestActorSessionBindFilter extends GenericFilterBean {

    private final ActorSession session;

    public RestActorSessionBindFilter(ActorSession session) {
        this.session = session;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String actorStr = ((HttpServletRequest)request).getHeader(RestActorSessionInterceptor.AttrActorSession);
            Optional.ofNullable(actorStr).ifPresent((str) ->
                    session.bind(RestActorSessionConverter.convert(str)));
            chain.doFilter(request, response);
        } finally {
            session.unbind();
        }
    }
    
}