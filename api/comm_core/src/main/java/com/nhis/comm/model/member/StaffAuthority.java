package com.nhis.comm.model.member;

import com.nhis.comm.context.orm.OrmActiveRecord;
import com.nhis.comm.context.orm.OrmRepository;
import com.nhis.comm.model.constraints.IdStr;
import com.nhis.comm.model.constraints.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by sewoo on 2017. 1. 3..
 *
 *
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StaffAuthority extends OrmActiveRecord<StaffAuthority> {
	private static final long serialVersionUID = 1l;

	/** ID */
	@Id
	@GeneratedValue
	private Long id;
	/** 직원 ID */
	@IdStr
	private String staffId;
	/** 권한 명칭. ( "접두어 ROLE_"을 부여하십시오) */
	@Name
	private String authority;

	/** ID의 권한 목록을 반환합니다. */
	public static List<StaffAuthority> find(final OrmRepository rep, String staffId) {
		return rep.tmpl().find("from StaffAuthority where staffId=?1", staffId);
	}

}
