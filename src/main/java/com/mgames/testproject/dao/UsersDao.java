package com.mgames.testproject.dao;

import com.mgames.testproject.db.handlers.UsersDBHandler;
import com.mgames.testproject.models.User;
import com.mgames.testproject.models.UserInfo;
import com.mgames.utils.db.MgDbPool;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.servlet.http.Cookie;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * @author Malyanov Dmitry
 */
@Component
public class UsersDao {

	@Autowired
	private MgDbPool pool;

	public String createAutologinKey(int userId, LocalDateTime expired) {
		String autologinKey = RandomStringUtils.randomAlphanumeric(16);
		pool.update("DELETE FROM users_autologin WHERE user_id = ?;"
				+ "INSERT INTO users_autologin(user_id, key, expired) VALUES(?, ?, ?)",
					userId,
					userId, autologinKey, Timestamp.from(expired.atZone(ZoneId.systemDefault()).toInstant())
		);
		return autologinKey;
	}

	public User createUser(UserInfo userInfo) {
		String salt = null;
		String password = userInfo.getPassword();
		if (password != null) {
			salt = RandomStringUtils.randomAlphabetic(10);
			password = User.generateSha256Password(password, salt);
		}
		return pool.query("INSERT INTO users(social_id, lname, fname, sname, email,  phone, repeatphone, "
                        + "password, bdate, sex, region, city, salt, photo) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *", new UsersDBHandler(),
						  userInfo.getSocialId(),
						  userInfo.getLname(),
						  userInfo.getFname(),
						  userInfo.getSname(),
                                                  userInfo.getEmail(),
                                                  userInfo.getPhoto(),
                                                  userInfo.getRepeatPhone(),
                                                  password,
						  userInfo.getBirthday(),
                                                  userInfo.getSex(),
						  userInfo.getRegion(),
						  userInfo.getCity(),
						  salt,
                                                  userInfo.getPhone())
				.stream().findFirst().orElse(null);
	}

	public User getUserBySocialId(String socialId) {
		return pool.query("SELECT * FROM users WHERE social_id=?", new UsersDBHandler(), socialId)
				.stream().findFirst().orElse(null);
	}

	public String getUserSocialId(Integer userId) {
		return pool.query("SELECT social_id FROM users WHERE id = ?", new ScalarHandler<String>(), userId);
	}

	public String getUserSocialId(Cookie autologinCookie) {
		if (autologinCookie == null) {
			return null;
		}
		return pool.query("SELECT social_id FROM users WHERE id = (SELECT user_id FROM users_autologin WHERE key = ? AND expired > now() LIMIT 1)",
						  new ScalarHandler<String>(),
						  autologinCookie.getValue());
	}

	public void updateLastVisit(int userId) {
		pool.update("UPDATE users SET last_visit = now() WHERE id = ?", userId);
	}

	public void updateUserInfo(int userId, UserInfo userInfo) {
		pool.update("UPDATE users SET fname = ?, lname = ?, photo = ?, bdate = ?, last_visit=NOW() WHERE id=?",
					userInfo.getFname(),
					userInfo.getLname(),
					userInfo.getPhoto(),
					userInfo.getBirthday(),
					userId);
	}

}
