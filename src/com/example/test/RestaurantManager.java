package com.example.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;

public class RestaurantManager {

	private static RestaurantManager sInstance;
	
	private Activity mActivity;
	//private Activity mLoginTopActivity;
	
	public static synchronized RestaurantManager getInstance(Activity mActivity) {
		if (sInstance == null) {
			sInstance = new RestaurantManager();
		}
		sInstance.mActivity = mActivity;
		return sInstance;
	}
	
	protected void showMain(String id) {
		
		String json = Utils.getJsonString("http://10.0.2.2:8888/restaurantInfo.json");
		JSONObject jso;
		Intent intent = new Intent();
		//RestaurantData rd = RestaurantData.getInstance(mActivity);
		try {
			jso = new JSONObject(json);
			intent.putExtra("RESTAURANT_NAME", jso.getString("RESTAURANT_NAME"));
			intent.putExtra("RESTAURANT_ADDRESS", jso.getString("RESTAURANT_ADDRESS"));
			intent.putExtra("RESTAURANT_PHONE", jso.getString("RESTAURANT_PHONE"));
			intent.putExtra("RESTAURANT_CUISINE", jso.getString("RESTAURANT_CUISINE"));
			intent.putExtra("RESTAURANT_PRICE", jso.getString("RESTAURANT_PRICE"));
			intent.putExtra("RESTAURANT_HOURS",jso.getString("RESTAURANT_HOURS") );
			intent.putExtra("RESTAURANT_PARKING", jso.getString("RESTAURANT_PARKING"));
			intent.putExtra("RESTAURANT_DESCRIPTION", jso.getString("RESTAURANT_DESCRIPTION"));
			intent.putExtra("RESTAURANT_MENU", jso.getString("RESTAURANT_MENU"));
			intent.putExtra("RESTAURANT_REVIEW_OVERALL", jso.getString("RESTAURANT_REVIEW_OVERALL"));
			intent.putExtra("RESTAURANT_REVIEW_FOOD", jso.getString("RESTAURANT_REVIEW_FOOD"));
			intent.putExtra("RESTAURANT_REVIEW_SERVICE", jso.getString("RESTAURANT_REVIEW_SERVICE"));
			intent.putExtra("RESTAURANT_REVIEW_AMBIENCE", jso.getString("RESTAURANT_REVIEW_AMBIENCE"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		intent.setClass(this.mActivity, RestaurantInfoActivity.class);
		//RestaurantData.setIntance(rd);
		intent.putExtra("ACTION", "SHOWBOOKING");
		this.mActivity.startActivity(intent);
		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	
	
	
	private void backToTopActivity(String action) {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, this.mActivity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("ACTION", action);
		this.mActivity.startActivity(intent);
		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
}
