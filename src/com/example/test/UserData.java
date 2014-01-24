package com.example.test;

import java.util.Calendar;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class UserData {

	private static final String USERDATA_LOGIN = "com.example.test.USERDATA_LOGIN";
	private static final String USERDATA_USERID = "com.example.test.USERDATA_USERID";
	private static final String USERDATA_FIRSTNAME = "com.example.test.USERDATA_FIRSTNAME";
	private static final String USERDATA_LASTNAME = "com.example.test.USERDATA_LASTNAME";
	private static final String USERDATA_EMAIL = "com.example.test.USERDATA_EMAIL";
	private static final String USERDATA_PHONE = "com.example.test.USERDATA_PHONE";
	private static final String USERDATA_TOKEN = "com.example.test.USERDATA_TOKEN";
	private static final String USERDATA_SESSIONID = "com.example.test.USERDATA_SESSIONID";
	
	private static UserData sInstance;
	
	private SharedPreferences mPrefs;
	
	private UserData() {}
	
	public static synchronized UserData getInstance() {
		return sInstance;
	}
	
	public static synchronized void init(Context context) {
		if (sInstance == null) {
			sInstance = new UserData();
			sInstance.mPrefs = context.getSharedPreferences("com.example.test", Context.MODE_PRIVATE);
		}
	}
	
	protected void clearAll() {
		this.setLogin(false);
		this.setUserId("");
		this.setFirstName("");
		this.setLastName("");
		this.setEmail("");
		this.setPhone("");
		this.setToken("");
	}
	
	protected Boolean isLogin() {
		return mPrefs.getBoolean(USERDATA_LOGIN, false);
	}
	
	protected void setLogin(Boolean isLogin) {
		mPrefs.edit().putBoolean(USERDATA_LOGIN, isLogin).commit();
	}
	
	protected String getUserId() {
		return mPrefs.getString(USERDATA_USERID, "");
	}
	
	protected void setUserId(String userId) {
		mPrefs.edit().putString(USERDATA_USERID, userId).commit();
	}
	
	protected String getFirstName() {
		return mPrefs.getString(USERDATA_FIRSTNAME, "");
	}
	
	protected void setFirstName(String firstName) {
		mPrefs.edit().putString(USERDATA_FIRSTNAME, firstName).commit();
	}
	
	protected String getLastName() {
		return mPrefs.getString(USERDATA_LASTNAME, "");
	}

	protected void setLastName(String lastName) {
		mPrefs.edit().putString(USERDATA_LASTNAME, lastName).commit();
	}
	
	protected String getFullName() {
		return getFirstName() + " " + getLastName();
	}
	
	protected String getEmail() {
		return mPrefs.getString(USERDATA_EMAIL, "");
	}
	
	protected void setEmail(String email) {
		mPrefs.edit().putString(USERDATA_EMAIL, email.toLowerCase()).commit();
	}
	
	protected String getPhone() {
		return mPrefs.getString(USERDATA_PHONE, "");
	}
	
	protected void setPhone(String phone) {
		mPrefs.edit().putString(USERDATA_PHONE, phone).commit();
	}
	
	protected String getToken() {
		return mPrefs.getString(USERDATA_TOKEN, "");
	}
	
	protected void setToken(String token) {
		mPrefs.edit().putString(USERDATA_TOKEN, token).commit();
	}
	
	protected String getSessionId() {
		return mPrefs.getString(USERDATA_SESSIONID, "");
	}
	
	protected void newSessionId() {
		String sessionId = UUID.randomUUID().toString();
		mPrefs.edit().putString(USERDATA_SESSIONID, sessionId).commit();
	}
	
}
