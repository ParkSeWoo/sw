package com.nhis.comm.model.member;

import com.nhis.comm.ValidationException;
import com.nhis.comm.context.Dto;
import com.nhis.comm.context.actor.Actor;
import com.nhis.comm.context.orm.OrmActiveRecord;
import com.nhis.comm.context.orm.OrmRepository;
import com.nhis.comm.model.constraints.IdStr;
import com.nhis.comm.model.constraints.Name;
import com.nhis.comm.model.constraints.OutlineEmpty;
import com.nhis.comm.model.constraints.Password;
import com.nhis.comm.util.Validator;
import lombok.*;
import org.hibernate.criterion.MatchMode;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Optional;

/**
 * Created by sewoo on 2017. 1. 2..
 *
 */

@Entity
@Data
@ToString(callSuper = false, exclude = { "password" })
@EqualsAndHashCode(callSuper = false)
public class Staff extends OrmActiveRecord<Staff> {
	/** ID */
	@Id
	@IdStr
	private String id;
	/** 이름 */
	@Name
	private String name;
	/** 암호화 */
	@Password
	private String password;

	public Actor actor() {
		return new Actor(id, name, Actor.ActorRoleType.Internal);
	}

	/** 암호를 변경합니다. */
	public Staff change(final OrmRepository rep, final PasswordEncoder encoder, final ChgPassword p) {
		return p.bind(this, encoder.encode(p.plainPassword)).update(rep);
	}

	/** 사원 정보를 변경합니다. */
	public Staff change(final OrmRepository rep, ChgStaff p) {
		return p.bind(this).update(rep);
	}

	/** 직원을 가져옵니다. */
	public static Optional<Staff> get(final OrmRepository rep, final String id) {
		return rep.get(Staff.class, id);
	}

	/** 직원을 가져옵니다.(예외포함) */
	public static Staff load(final OrmRepository rep, final String id) {
		return rep.load(Staff.class, id);
	}

	/** 직원을 찾습니다. */
	public static List<Staff> find(final OrmRepository rep, final FindStaff p) {
		return rep.tmpl().find(Staff.class,
				(criteria) -> criteria.like(new String[] { "id", "name" }, p.keyword, MatchMode.ANYWHERE)
						.sort("id").result());
	}

	/** 직원을 등록합니다. */
	public static Staff register(final OrmRepository rep, final PasswordEncoder encoder, RegStaff p) {
		Validator.validate((v) -> v.checkField(!get(rep, p.id).isPresent(), "id", ValidationException.ErrorKeys.DuplicateId));
		return p.create(encoder.encode(p.plainPassword)).save(rep);
	}

	/** 검색 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindStaff implements Dto {
		private static final long serialVersionUID = 1l;
		@OutlineEmpty
		private String keyword;
	}

	/** 등록 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegStaff implements Dto {
		private static final long serialVersionUID = 1l;

		@IdStr
		private String id;
		@Name
		private String name;
		/** 비밀번호 (해시 안된것) */
		@Password
		private String plainPassword;

		public Staff create(String password) {
			Staff m = new Staff();
			m.setId(id);
			m.setName(name);
			m.setPassword(password);
			return m;
		}
	}

	/** 변경 매개 변수 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChgStaff implements Dto {
		private static final long serialVersionUID = 1l;
		@Name
		private String name;

		public Staff bind(final Staff m) {
			m.setName(name);
			return m;
		}
	}

	/** 비밀번호 변경 매개 변수 */
	@Value
	public static class ChgPassword implements Dto {
		private static final long serialVersionUID = 1l;
		@Password
		private String plainPassword;

		public Staff bind(final Staff m, String password) {
			m.setPassword(password);
			return m;
		}
	}

}