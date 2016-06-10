/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgames.testproject.models;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * @author Constantine Tretyak
 */
public class UserInfo {

	private static final Logger log = LoggerFactory.getLogger(UserInfo.class.getName());

	private final Timestamp birthday;

	private final String email;

	private final String lname;
	
	private final String fname;
	
	private final String sname;
        
	private final String phone;
        
	private final String repeatPhone;

	private final String password;
        
	private final String region;
	
        private final String city;

	private final String photo;

	private final int sex;

	private final String socialId;

	protected UserInfo(String socialId,
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
                                           String photo
					   ) {
		this.socialId = socialId;
		this.fname = firstName;
		this.lname = lastName;
		this.sname = secondName;
		this.sex = sex;
		this.photo = photo;
		this.birthday = birthday;
		this.email = email;
		this.password = password;
		this.region = region;
		this.city = city;
                this.phone = phone;
                this.repeatPhone = repeatPhone;
	}

	public Timestamp getBirthday() {
		return birthday;
	}

	public String getEmail() {
		return email;
	}

	public String getFname() {
		return fname;
	}

	public String getLname() {
		return lname;
	}

	public String getSname() {
	    return sname;
	}
	

	public String getPassword() {
		return password;
	}
        
	public String getRegion() {
		return region;
	}
        
	public String getCity() {
		return city;
	}
        
	public String getPhone() {
		return phone;
	}
	public String getRepeatPhone() {
		return repeatPhone;
	}

	public String getPhoto() {
		return photo;
	}

	public int getSex() {
		return sex;
	}

	public String getSocialId() {
		return socialId;
	}

	public static class UserInfoBuilder {

		public static UserInfoBuilder email(String lastName, String firstName,
						    String secondName,
						    String email, 
						    String phone, 
						    String repeatPhone, 
                                                    String password,
						    Timestamp birthday,
						    String region,
						    String city) {
			UserInfoBuilder builder = new UserInfoBuilder();
			builder.socialId = "email_" + email;
			builder.lname = lastName;
			builder.fname = firstName;
			builder.sname = secondName;
			builder.email = email;
			builder.phone = phone;
			builder.repeatPhone = repeatPhone;
			builder.password = password;
			builder.birthday = birthday;
			builder.region = region;
			builder.city = city;
			return builder;
		}

		public static UserInfoBuilder fb(String uid) {
			UserInfoBuilder builder = new UserInfoBuilder();
			builder.socialId = "fb_" + uid;
			return builder;
		}

		public static UserInfoBuilder ok(String uid) {
			UserInfoBuilder builder = new UserInfoBuilder();
			builder.socialId = "ok_" + uid;
			return builder;
		}

		public static UserInfoBuilder vk(String uid) {
			UserInfoBuilder builder = new UserInfoBuilder();
			builder.socialId = "vk_" + uid;
			return builder;
		}

		private Timestamp birthday;

		private String email;

		private String lname;
		
		private String fname;
		
		private String sname;
                
		private String phone;
                
		private String repeatPhone;

		private String password;
                
		private String region;
                
		private String city;

		private String photo;

		private int sex = 0;

		private String socialId;

		protected UserInfoBuilder() {
		}

		public UserInfo build() {
			return new UserInfo(socialId, 
                                fname, lname, sname, email,  phone,
					   repeatPhone, password, birthday,
                                           sex,
					   region,
					   city, photo);
		}

		public UserInfoBuilder withBirthday(Timestamp birthday) {
			this.birthday = birthday;
			return this;
		}

		public UserInfoBuilder withBirthday(String birthday, String pattern) {
			if (birthday != null) {
				try {
					this.birthday = new Timestamp(LocalDate.parse(birthday, DateTimeFormatter.ofPattern(pattern)).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
				} catch (DateTimeParseException ex) {
					log.warn("Unparsable date {} with pattern {}", birthday, pattern);
				}
			}
			return this;
		}

		public UserInfoBuilder withFirstName(String firstName) {
			this.fname = firstName;
			return this;
		}

		public UserInfoBuilder withLastName(String lastName) {
			this.lname = lastName;
			return this;
		}

		public UserInfoBuilder withPhoto(String photo) {
			this.photo = photo;
			return this;
		}

		public UserInfoBuilder withSex(int sex) {
			this.sex = sex;
			return this;
		}

		public UserInfoBuilder withSexMaleFemale(String maleOrfemale) {
			int sex = 0;
			switch (maleOrfemale) {
				case "male":
					withSex(2);
					break;
				case "female":
					withSex(1);
					break;
				default:
					withSex(0);
			}
			return this;
		}

	}

}
