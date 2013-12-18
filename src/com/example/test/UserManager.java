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
		if (UserData.getInstance(this.mActivity.getApplicationContext()).isLogin()) {
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
		UserData.getInstance(this.mActivity.getApplicationContext()).setLogin(true);
		try {
			UserData.getInstance(this.mActivity.getApplicationContext()).setUserId(userJson.getString("uid"));
			UserData.getInstance(this.mActivity.getApplicationContext()).setFirstName(userJson.getString("first_name"));
            UserData.getInstance(this.mActivity.getApplicationContext()).setLastName(userJson.getString("last_name"));
            UserData.getInstance(this.mActivity.getApplicationContext()).setEmail(userJson.getString("email"));
            UserData.getInstance(this.mActivity.getApplicationContext()).setPhone(userJson.getString("phone"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.backToTopActivity(Constants.ACTION_LOGIN_SUCCESS);
	}
	
	protected void reserveAsGuest() {
		this.backToTopActivity(Constants.ACTION_RESERVE_AS_GUEST);
	}
	
	protected void showProfile() {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, UserProfileActivity.class);
		this.mActivity.startActivity(intent);
		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	protected void logout() {
		UserData.getInstance(this.mActivity.getApplicationContext()).clearAll();
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
