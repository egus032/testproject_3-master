/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgames.testproject.managers;

import com.mgames.testproject.Variables;
import com.mgames.testproject.caches.UsersCache;
import com.mgames.testproject.dao.UsersDao;
import com.mgames.testproject.models.User;
import com.mgames.testproject.models.UserInfo;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Constantine Tretyak
 */
@Component
public class UsersManager {

	private static final String SESSION_KEY = "user_social_id";

	private static final String USER_AUTOLOGIN_COOKIE_KEY = "user_autologin_" + DigestUtils.md5Hex("testproject");

	private static final Logger log = LoggerFactory.getLogger(UsersManager.class.getName());

	@Autowired
	@Qualifier(value = "usersCacheMap")
	private UsersCache usersCache;

	@Autowired
	private UsersDao usersDao;

	@Autowired
	private Variables vars;

	public void clear() {
		usersCache.clear();
	}

	public User get(String socialId) {
		if (socialId == null) {
			return null;
		}
		User user = usersCache.get(socialId);
		if (user == null) {
			user = usersDao.getUserBySocialId(socialId);
			if (user != null) {
				usersCache.put(user.getSocialId(), user);
			}
		}
		return user;
	}

	/**
	 * Method returns user from cache, but it makes a request 'social_id' from the database
	 *
	 * @param userId
	 * @return
	 */
	public User get(Integer userId) {
		return get(usersDao.getUserSocialId(userId));
	}

	public User get(HttpServletRequest request) {
		Object socialId = request.getSession().getAttribute(SESSION_KEY);
		if (socialId == null) {
			if (request.getCookies() != null) {
				Cookie autologinCookie = Arrays.asList(request.getCookies()).parallelStream().filter(c -> c.getName().equals(USER_AUTOLOGIN_COOKIE_KEY)).findAny().orElse(null);
				socialId = usersDao.getUserSocialId(autologinCookie);
			}
		}
		return get(Objects.toString(socialId));
	}

	public boolean has(HttpServletRequest request) {
		return get(request) != null;
	}

	public User registerOrUpdate(HttpSession session, HttpServletResponse response, UserInfo userInfo, boolean autologin) {
		User user = get(userInfo.getSocialId());
		if (user == null) {
			user = create(userInfo);
		} else {
			usersDao.updateUserInfo(user.getId(), userInfo);
			user.update(userInfo);
			log.info("UPDATE_USER; " + user + "; cache size: " + usersCache.getSize());
		}
		if (user != null) {
			session.setAttribute(SESSION_KEY, user.getSocialId());
			if (autologin) {
				setAutologin(response, user);
			}
		}
		return user;
	}

	public void signIn(HttpSession session, HttpServletResponse response, User user, boolean autologin) {
		if (user != null) {
			usersDao.updateLastVisit(user.getId());
			log.info("SIGNIN_USER; " + user + "; cache size: " + usersCache.getSize());
			session.setAttribute(SESSION_KEY, user.getSocialId());
			if (autologin) {
				setAutologin(response, user);
			}
		}
	}

	public void signOut(HttpSession session, HttpServletResponse response) {
		session.removeAttribute(SESSION_KEY);
		Cookie autologinCookie = new Cookie(USER_AUTOLOGIN_COOKIE_KEY, "");
		autologinCookie.setHttpOnly(true);
		autologinCookie.setPath("/");
		autologinCookie.setMaxAge(0);
		response.addCookie(autologinCookie);
	}

	private User create(UserInfo userInfo) {
		User user = usersDao.createUser(userInfo);
		if (user != null) {
			usersCache.put(userInfo.getSocialId(), user);
			log.info("CREATE_USER; " + user + "; cache size: " + usersCache.getSize());
		}
		return user;
	}

	private void setAutologin(HttpServletResponse response, User user) {
		String autologinKey = usersDao.createAutologinKey(user.getId(), LocalDateTime.now().plusSeconds(vars.USER_AUTOLOGIN_EXPIRED_SECONDS));
		Cookie autologinCookie = new Cookie(USER_AUTOLOGIN_COOKIE_KEY, autologinKey);
		autologinCookie.setHttpOnly(true);
		autologinCookie.setPath("/");
		autologinCookie.setMaxAge(vars.USER_AUTOLOGIN_EXPIRED_SECONDS);
		response.addCookie(autologinCookie);
	}

}
