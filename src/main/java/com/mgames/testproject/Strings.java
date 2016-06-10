package com.mgames.testproject;

import com.mgames.testproject.dao.CommonsDao;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Constantine Tretyak
 */
@Component
public class Strings {

	@Autowired
	private CommonsDao commonsDao;

	private HashMap<String, String> strings;

	public HashMap<String, String> getStrings() {
		return strings;
	}

	public String getStringsValue(String stringEnum) {
		return strings.get(stringEnum);
	}

	@PostConstruct
	public void reload() {
		strings = commonsDao.getStrings();
	}

}
