/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgames.testproject.caches;

import com.mgames.testproject.models.User;

/**
 * <p>
 * @author Constantine Tretyak
 */
public interface UsersCache {

	public void clear();

	public User get(String userKey);

	public long getSize();

	public void put(String userKey, User user);

	public void remove(String userKey);

}
