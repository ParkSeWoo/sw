package com.nhis.comm.context;

import com.nhis.comm.context.orm.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Created by sewoo on 2016. 12. 28..
 *
 * 응용 프로그램 설정 정보에 대한 접근 방법을 제공합니다
 */
public class AppSettingHandler {
	@Autowired
	@Lazy
	private SystemRepository rep;
	/** 설정시 고정 키 / 값을 반환 모의 모드로 */
	private final Optional<Map<String, String>> mockMap;

	public AppSettingHandler() {
		this.mockMap = Optional.empty();
	}

	public AppSettingHandler(Map<String, String> mockMap) {
		this.mockMap = Optional.of(mockMap);
	}

	/** 응용 프로그램 설정 정보를 가져옵니다. */
	@Cacheable(cacheNames = "AppSettingHandler.appSetting", key = "#id")
	@Transactional(value = SystemRepository.BeanNameTx)
	public AppSetting setting(String id) {
		if (mockMap.isPresent())
			return mockSetting(id);
		AppSetting setting = AppSetting.load(rep, id);
		setting.hashCode(); // for loading
		return setting;
	}

	private AppSetting mockSetting(String id) {
		return new AppSetting(id, "category", "테스트 모의 정보", mockMap.get().get(id));
	}

	/** 응용 프로그램 설정 정보를 변경합니다. */
	@CacheEvict(cacheNames = "AppSettingHandler.appSetting", key = "#id")
	@Transactional(value = SystemRepository.BeanNameTx)
	public AppSetting update(String id, String value) {
		return mockMap.isPresent() ? mockSetting(id) : AppSetting.load(rep, id).update(rep, value);
	}

}
