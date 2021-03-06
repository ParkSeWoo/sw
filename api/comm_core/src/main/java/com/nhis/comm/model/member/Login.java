package com.nhis.comm.model.member;

import com.nhis.comm.ValidationException;
import com.nhis.comm.context.Dto;
import com.nhis.comm.context.orm.OrmActiveRecord;
import com.nhis.comm.context.orm.OrmRepository;
import com.nhis.comm.model.constraints.IdStr;
import com.nhis.comm.model.constraints.Password;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Optional;

/**
 * Created by sewoo on 2017. 1. 2..
 */

@Entity
@Data
@ToString(callSuper = false, exclude = { "password" })
@EqualsAndHashCode(callSuper = false)
public class Login extends OrmActiveRecord<Login> {
	private static final long serialVersionUID = 1L;

	/** 口座ID */
	@Id
	@IdStr
	private String id;
	/** ログインID */
	private String loginId;
	/** パスワード(暗号化済) */
	@Password
	private String password;

	/** ログインIDを変更します。 */
	public Login change(final OrmRepository rep, final ChgLoginId p) {
		boolean exists = rep.tmpl().get("from Login where id<>?1 and loginId=?2", id, p.loginId).isPresent();
		validate((v) -> v.checkField(!exists, "loginId", ValidationException.ErrorKeys.DuplicateId));
		return p.bind(this).update(rep);
	}

	/** パスワードを変更します。 */
	public Login change(final OrmRepository rep, final PasswordEncoder encoder, final ChgPassword p) {
		return p.bind(this, encoder.encode(p.plainPassword)).update(rep);
	}

	/** ログイン情報を取得します。 */
	public static Optional<Login> get(final OrmRepository rep, String id) {
		return rep.get(Login.class, id);
	}

	/** ログイン情報を取得します。 */
	public static Optional<Login> getByLoginId(final OrmRepository rep, String loginId) {
		return Optional.ofNullable(loginId).flatMap(lid -> rep.tmpl().get("from Login where loginId=?1", lid));
	}

	/** ログイン情報を取得します。(例外付) */
	public static Login load(final OrmRepository rep, String id) {
		return rep.load(Login.class, id);
	}

	/** ログインID変更パラメタ low: 基本はユースケース単位で切り出す */
	@Value
	public static class ChgLoginId implements Dto {
		private static final long serialVersionUID = 1l;
		@IdStr
		private String loginId;

		public Login bind(final Login m) {
			m.setLoginId(loginId);
			return m;
		}
	}

	/** パスワード変更パラメタ */
	@Value
	public static class ChgPassword implements Dto {
		private static final long serialVersionUID = 1l;
		@Password
		private String plainPassword;

		public Login bind(final Login m, String password) {
			m.setPassword(password);
			return m;
		}
	}

}
