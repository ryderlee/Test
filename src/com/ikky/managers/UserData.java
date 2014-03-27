package com.ikky.managers;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserData {

	private static final String USERDATA_LOGIN = "com.example.test.USERDATA_LOGIN";
	private static final String USERDATA_USERID = "com.example.test.USERDATA_USERID";
	private static final String USERDATA_FIRSTNAME = "com.example.test.USERDATA_FIRSTNAME";
	private static final String USERDATA_LASTNAME = "com.example.test.USERDATA_LASTNAME";
	private static final String USERDATA_EMAIL = "com.example.test.USERDATA_EMAIL";
	private static final String USERDATA_PHONE = "com.example.test.USERDATA_PHONE";
	private static final String USERDATA_TOKEN = "com.example.test.USERDATA_TOKEN";
	private static final String USERDATA_SESSIONID = "com.example.test.USERDATA_SESSIONID";

	private static final String USERDATA_FB_ID = "com.example.test.USERDATA_FB_ID";
	private static final String USERDATA_FB_USERNAME = "com.example.test.USERDATA_FB_USERNAME";
	private static final String USERDATA_FB_FIRSTNAME = "com.example.test.USERDATA_FB_FIRSTNAME";
	private static final String USERDATA_FB_LASTNAME = "com.example.test.USERDATA_FB_LASTNAME";
	private static final String USERDATA_FB_TOKEN = "com.example.test.USERDATA_FB_TOKEN";
	private static final String USERDATA_FB_EXPIRE_TS = "com.example.test.USERDATA_FB_EXPIRE_TS";
	private static final String USERDATA_FB_LAST_UPDATE_TS = "com.example.test.USERDATA_FB_LAST_UPDATE_TS";
	
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
	
	public void clearAll() {
		this.setLogin(false);
		this.setUserId("");
		this.setFirstName("");
		this.setLastName("");
		this.setEmail("");
		this.setPhone("");
		this.setToken("");
	}
	
	public Boolean isLogin() {
		return mPrefs.getBoolean(USERDATA_LOGIN, false);
	}
	
	public void setLogin(Boolean isLogin) {
		mPrefs.edit().putBoolean(USERDATA_LOGIN, isLogin).commit();
	}
	
	public String getUserId() {
		return mPrefs.getString(USERDATA_USERID, "");
	}
	
	public void setUserId(String userId) {
		mPrefs.edit().putString(USERDATA_USERID, userId).commit();
	}
	
	public String getFirstName() {
		return mPrefs.getString(USERDATA_FIRSTNAME, "");
	}
	
	public void setFirstName(String firstName) {
		mPrefs.edit().putString(USERDATA_FIRSTNAME, firstName).commit();
	}
	
	public String getLastName() {
		return mPrefs.getString(USERDATA_LASTNAME, "");
	}

	public void setLastName(String lastName) {
		mPrefs.edit().putString(USERDATA_LASTNAME, lastName).commit();
	}
	
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}
	
	public String getEmail() {
		return mPrefs.getString(USERDATA_EMAIL, "");
	}
	
	public void setEmail(String email) {
		mPrefs.edit().putString(USERDATA_EMAIL, email.toLowerCase()).commit();
	}
	
	public String getPhone() {
		return mPrefs.getString(USERDATA_PHONE, "");
	}
	
	public void setPhone(String phone) {
		mPrefs.edit().putString(USERDATA_PHONE, phone).commit();
	}
	
	public String getToken() {
		return mPrefs.getString(USERDATA_TOKEN, "");
	}
	
	public void setToken(String token) {
		mPrefs.edit().putString(USERDATA_TOKEN, token).commit();
	}
	
	public String getSessionId() {
		return mPrefs.getString(USERDATA_SESSIONID, "");
	}
	
	public void newSessionId() {
		String sessionId = UUID.randomUUID().toString();
		mPrefs.edit().putString(USERDATA_SESSIONID, sessionId).commit();
	}
	
	
	public void clearFbData() {
		setFbData("", "", "", "", "", "");
	}
	
	public Boolean isUpdateFbData() {
		Long lastUpdate = mPrefs.getLong(USERDATA_FB_LAST_UPDATE_TS, 0);
		Date now = Calendar.getInstance().getTime();
		if (now.getTime() - lastUpdate > 60*5*1000) {
			mPrefs.edit().putLong(USERDATA_FB_LAST_UPDATE_TS, now.getTime()).commit();
			return true;
		}
		return false;
	}
	
	public Boolean setFbData(String fbId, String username, String firstName, String lastName, String token, String expireTs) {
		Boolean changed = false;
		if (!mPrefs.getString(USERDATA_FB_USERNAME, "").equals(username) || !mPrefs.getString(USERDATA_FB_FIRSTNAME, "").equals(firstName) || !mPrefs.getString(USERDATA_FB_LASTNAME, "").equals(lastName) || !mPrefs.getString(USERDATA_FB_TOKEN, "").equals(token) || !mPrefs.getString(USERDATA_FB_EXPIRE_TS, "").equals(expireTs)) {
			changed = true;
		}
		Editor edit = mPrefs.edit();
		edit.putString(USERDATA_FB_ID, fbId);
		edit.putString(USERDATA_FB_USERNAME, username);
		edit.putString(USERDATA_FB_FIRSTNAME, firstName);
		edit.putString(USERDATA_FB_LASTNAME, lastName);
		edit.putString(USERDATA_FB_TOKEN, token);
		edit.putString(USERDATA_FB_EXPIRE_TS, expireTs);
		edit.commit();
		return changed;
	}
	
	public String getFbId() {
		return mPrefs.getString(USERDATA_FB_ID, "");
	}
	
	public String getFbUsername() {
		return mPrefs.getString(USERDATA_FB_USERNAME, "");
	}
	
	public String getFbFirstName() {
		return mPrefs.getString(USERDATA_FB_FIRSTNAME, "");
	}
	
	public String getFbLastName() {
		return mPrefs.getString(USERDATA_FB_LASTNAME, "");
	}
	
	public String getFbToken() {
		return mPrefs.getString(USERDATA_FB_TOKEN, "");
	}
	
	public String getFbExpireTs() {
		return mPrefs.getString(USERDATA_FB_EXPIRE_TS, "");
	}
	
}
