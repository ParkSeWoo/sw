package com.nhis.comm.context;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.*;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by sewoo on 2016. 12. 27..
 *
 * ResourceBundle에 대한 빠른 액세스를 제공합니다.
 * <p> 본 컴포넌트는 API를 통해 레이블 목록의 제공 등 i18n 용도의 메시지 속성에서 사용하십시오.
 * <p> ResourceBundle은 단순한 문자열 변환을 목적으로하는 표준 MessageSource와는 다른 특성 (목록 개념)을
 * 가지고 있기 때문에 다른 인스턴스에서 관리하고 있습니다.
 * (spring.message 별도로 지정 [extension.messages] 할 필요가 있으므로주의하십시오)
 */

@ConfigurationProperties(prefix = "extension.messages")
public class ResourceBundleHandler {

	private String encoding = "UTF-8";
	private Map<String, ResourceBundle> bundleMap = new ConcurrentHashMap<>();

	/**
	 * 지정된 메시지 소스의 ResourceBundle를 돌려줍니다.
     * <p> basename에 확장자 (.properties)를 포함 할 필요가 없습니다.
	 */
	public ResourceBundle get(String basename) {
		return get(basename, Locale.getDefault());
	}

	public synchronized ResourceBundle get(String basename, Locale locale) {
		bundleMap.putIfAbsent(keyname(basename, locale), ResourceBundleFactory.create(basename, locale, encoding));
		return bundleMap.get(keyname(basename, locale));
	}

	private String keyname(String basename, Locale locale) {
		return basename + "_" + locale.toLanguageTag();
	}

	/**
	 * 지정된 메시지 소스 레이블 키 값의 Map를 돌려줍니다.
	 * <p> basename에 확장자 (.properties)를 포함 할 필요가 없습니다.
	 */
	public Map<String, String> labels(String basename) {
		return labels(basename, Locale.getDefault());
	}

	public Map<String, String> labels(String basename, Locale locale) {
		ResourceBundle bundle = get(basename, locale);
		return bundle.keySet().stream().collect(Collectors.toMap(
				key -> key,
				key -> bundle.getString(key)));
	}

	/**
	 * Spring의 MessageSource를 통해 ResourceBundle를 얻을 Factory.
	 * <p>속성 파일의 인코딩 지정을 가능하게 하고 있습니다.
	 */
	public static class ResourceBundleFactory extends ResourceBundleMessageSource {
		/** ResourceBundle를 가져옵니다。 */
		public static ResourceBundle create(String basename, Locale locale, String encoding) {
			ResourceBundleFactory factory = new ResourceBundleFactory();
			factory.setDefaultEncoding(encoding);
			return Optional.ofNullable(factory.getResourceBundle(basename, locale))
					.orElseThrow(() -> new IllegalArgumentException("지정된 basename 리소스 파일을 찾을 수 없습니다。[]"));
		}
	}

}
