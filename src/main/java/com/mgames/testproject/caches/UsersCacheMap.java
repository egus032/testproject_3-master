package com.mgames.testproject.caches;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.mgames.testproject.models.User;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * @author Malyanov Dmitry
 */
@Component
public class UsersCacheMap implements UsersCache {

	private static final Logger log = LoggerFactory.getLogger(UsersCacheMap.class);

	private final Cache<String, User> users = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.DAYS)
			.removalListener((RemovalNotification<String, User> rn) -> {
				onUserExpired(rn.getKey(), rn.getValue());
			})
			.build();

	@Override
	public void clear() {
		users.invalidateAll();
	}

	@Override
	public User get(String userKey) {
		return users.getIfPresent(userKey);
	}

	@Override
	public long getSize() {
		return users.size();
	}

	@Override
	public void put(String userKey, User user) {
		users.put(userKey, user);
	}

	@Override
	public void remove(String userKey) {
		users.invalidate(userKey);
	}

	private void onUserExpired(String socialId, User user) {
//        log.info("USER_CACHE: EXPIRED; user: " + socialId);
	}

}
