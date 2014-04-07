package com.ikky.managers;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.ikky.activities.SigninOptionActivity;
import com.ikky.activities.UserProfileActivity;
import com.ikky.constants.Constants;

import android.app.Activity;
import android.content.Intent;

public class UserManager {

	private static UserManager sInstance;
	
	private Activity mActivity;
	private Activity mLoginTopActivity;
	
	public static synchronized UserManager getInstance(Activity mActivity) {
		if (sInstance == null) {
			sInstance = new UserManager();
		}
		sInstance.mActivity = mActivity;
		return sInstance;
	}
	
	public void login(boolean reserve) {
		this.mLoginTopActivity = this.mActivity;
		if (UserData.getInstance().isLogin()) {
			this.backToTopActivity(Constants.ACTION_LOGIN_SUCCESS);
		} else {
			Intent intent = new Intent();
			intent.setClass(this.mActivity, SigninOptionActivity.class);
			intent.putExtra("RESERVE", reserve);
			this.mActivity.startActivity(intent);
		}
	}
	
	public void loginSuccess(JSONObject userJson) {
		UserData.getInstance().setLogin(true);
		UserData.getInstance().newSessionId();
		try {
			UserData.getInstance().setUserId(userJson.getString("user_id"));
			UserData.getInstance().setFirstName(userJson.getString("first_name"));
            UserData.getInstance().setLastName(userJson.getString("last_name"));
            UserData.getInstance().setEmail(userJson.getString("email"));
            UserData.getInstance().setPhone(userJson.getString("phone"));
            UserData.getInstance().setToken(userJson.getString("token"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		this.backToTopActivity(Constants.ACTION_LOGIN_SUCCESS);
	}
	
	public void bookAsGuest() {
		this.backToTopActivity(Constants.ACTION_BOOK_AS_GUEST);
	}
	
	public void showBookings(String bookingID) {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, UserProfileActivity.class);
		this.mActivity.startActivity(intent);
	}
	
	public void showProfile() {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, UserProfileActivity.class);
		this.mActivity.startActivity(intent);
	}
	
	public void logout() {
		UserData.getInstance().clearAll();
		UserData.getInstance().clearFbData();
		Session.getActiveSession().closeAndClearTokenInformation();
		this.backToTopActivity(Constants.ACTION_LOGOUT);
	}
	
	private void backToTopActivity(String action) {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, this.mLoginTopActivity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("ACTION", action);
		this.mActivity.startActivity(intent);
	}
	
}
