package com.mgames.testproject.models;

import com.mgames.testproject.models.interfaces.JsonConvertible;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 * @author MalyanovDmitry
 */
public class User implements JsonConvertible {

	public static String generateSha256Password(String password, String salt) {
		return DigestUtils.sha256Hex(salt + DigestUtils.sha256Hex(salt + password));
	}

	private LocalDateTime birthday;

	private final String email;

	private String fname;
	
	private String sname;

	private final int id;

	private String lname;

	private final String password;

	private String photo;

	private final String salt;

	private final int sex;
        
	private String region;
        
	private String city;
        
	private String phone;
        
	private String repeatPhone;

	private final String socialId;

	public User(int id,
				String socialId,
				String firstName,
				String lastName,
				String secondName,
                                String email,
                                String phone,
                                String repeatPhone,
                                String password,
				Timestamp birthday,
                                int sex,
				String region,
				String city,
				String salt,
                                String photo) {
		this.id = id;
		this.socialId = socialId;
		this.fname = firstName;
		this.lname = lastName;
		this.sname = secondName;
                this.email = email;
                this.phone = phone;
                this.repeatPhone = repeatPhone;
		this.password = password;
		this.birthday = birthday != null ? birthday.toLocalDateTime() : null;
                this.sex = sex;
		this.photo = photo;
		this.region = region;
		this.city = city;
		this.salt = salt;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(generateSha256Password(password, salt));
	}

	public int getId() {
		return id;
	}

	public String getSocialId() {
		return socialId;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return new JSONObject()
                                .put("social_id", socialId)
				.put("lname", lname)
				.put("fname", fname)
				.put("sname", sname)
                                .put("phone", phone)
                                .put("repeatPhone", repeatPhone)
				.put("birthday", birthday)
                                .put("sex", sex)
                                .put("region", region)
                                .put("city", city)
                                .put("photo", photo);
	}

	@Override
	public String toString() {
		return "User{" + "email=" + email 
                        + ", firstName=" + fname 
			+ ", id=" + id 
                        + ", lastName=" + lname 
                        + ", secondName=" + sname 
                        + ", socialId=" + socialId 
                        + ", phone=" + phone 
                        + ", repeatPhone=" + repeatPhone 
			+ ", birthday=" + birthday 
                        + ", region=" + region
                        + ", city=" + city
                        + '}';
	}

	public void update(UserInfo userInfo) {
		this.lname = userInfo.getLname();
		this.fname = userInfo.getFname();
		this.sname = userInfo.getSname();
		this.birthday = userInfo.getBirthday() != null ? userInfo.getBirthday().toLocalDateTime() : null;
		
	}

}
