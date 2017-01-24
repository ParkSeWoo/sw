package com.nhis.api.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by sewoo on 2017. 1. 11..
 * API TestR
 */

@Data
@Entity
public class Member {
	@Id
	@GeneratedValue
	Long Id;
	String name;
	String userName;
	String remark;
	public Member() {}
	public Member(String name, String userName, String remakr) {
		this.name = name;
		this.userName = userName;
		this.remark = remakr;
	}
}
