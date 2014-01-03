package com.example.test;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	protected void login(boolean reserve) {
		this.mLoginTopActivity = this.mActivity;
		if (UserData.getInstance().isLogin()) {
			this.backToTopActivity(Constants.ACTION_LOGIN_SUCCESS);
		} else {
			Intent intent = new Intent();
			intent.setClass(this.mActivity, SigninOptionActivity.class);
			intent.putExtra("RESERVE", reserve);
			this.mActivity.startActivity(intent);
			this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	
	protected void loginSuccess(JSONObject userJson) {
		UserData.getInstance().setLogin(true);
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
		this.backToTopActivity(Constants.ACTION_LOGIN_SUCCESS);
	}
	
	protected void bookAsGuest() {
		this.backToTopActivity(Constants.ACTION_BOOK_AS_GUEST);
	}
	
	protected void showBookings(String bookingID) {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, UserProfileActivity.class);
		this.mActivity.startActivity(intent);
		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	protected void showProfile() {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, UserProfileActivity.class);
		this.mActivity.startActivity(intent);
		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	protected void logout() {
		UserData.getInstance().clearAll();
		this.backToTopActivity(Constants.ACTION_LOGOUT);
	}
	
	private void backToTopActivity(String action) {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, this.mLoginTopActivity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("ACTION", action);
		this.mActivity.startActivity(intent);
		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
}
