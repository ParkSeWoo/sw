package com.nhis.comm.context.actor;

/**
 * Created by sewoo on 2016. 12. 27..
 */
public class ActorSession {

	private ThreadLocal<Actor> actorLocal = new ThreadLocal<>();

	/** 이용자 세션에 사용자를 넣습니다。 */
	public ActorSession bind(final Actor actor) {
		actorLocal.set(actor);
		return this;
	}

	/** 이용자 세션을 삭제 합니다。 */
	public ActorSession unbind() {
		actorLocal.remove();
		return this;
	}

	/** 유효한 이용자를 반환 합니다 세션이 없다면 익명으로 반환 합니다。 */
	public Actor actor() {
		Actor actor = actorLocal.get();
		return actor != null ? actor : Actor.Anonymous;
	}
}
