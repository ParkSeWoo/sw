package com.nhis.comm.context.orm;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Created by sewoo on 2016. 12. 28..
 * Orm 관련 유틸리티를 제공합니다.
 */
public abstract class OrmUtils {

	/** 지정된 클래스의 엔티티 정보를 반환합니다 (ID 개념 포함) */
	@SuppressWarnings("unchecked")
	public static <T> JpaEntityInformation<T, Serializable> entityInformation(EntityManager em, Class<T> clazz) {
		return (JpaEntityInformation<T, Serializable>) JpaEntityInformationSupport.getEntityInformation(clazz, em);
	}
}
