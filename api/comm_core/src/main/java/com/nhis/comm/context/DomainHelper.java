package com.nhis.comm.context;

import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.actor.ActorSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sewoo on 2016. 12. 28..
 *
 * 도메인 처리를 실시하는데 필요한 인프라 계층 구성 요소에 접근을 제공합니다
 */
@Setter
public class DomainHelper {

	@Autowired
	private ActorSession actorSession;
	@Autowired
	private Timestamper time;
	@Autowired
	private AppSettingHandler settingHandler;

	/** 로그인중인 유스 케이스 이용자를 가져옵니다. */
	public Actor actor() {
		return actorSession().actor();
	}

	/** 스레드 로컬 범위의 이용자 세션을 가져옵니다. */
	public ActorSession actorSession() {
		return actorSession;
	}

	/** 일,시 유틸리티를 가져옵니다. */
	public Timestamper time() {
		return time;
	}

	/** 응용 프로그램 설정 정보를 가져옵니다 */
	public AppSetting setting(String id) {
		return settingHandler.setting(id);
	}

	/** 응용 프로그램 설정 정보를 설정합니다 */
	public AppSetting settingSet(String id, String value) {
		return settingHandler.update(id, value);
	}
}
