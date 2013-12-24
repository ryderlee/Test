package com.example.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

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
		RestaurantData rd = RestaurantData.getInstance();
		try {
			jso = new JSONObject(json);
			/*
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
			*/
			rd.setID(id);
			rd.setName(jso.getString("RESTAURANT_NAME"));
			rd.setAddress(jso.getString("RESTAURANT_ADDRESS"));
			rd.setPhone(jso.getString("RESTAURANT_PHONE"));
			rd.setCuisine(jso.getString("RESTAURANT_CUISINE"));
			rd.setPrice(jso.getString("RESTAURANT_PRICE"));
			rd.setHours(jso.getString("RESTAURANT_HOURS"));
			rd.setParking(jso.getString("RESTAURANT_PARKING"));
			rd.setDescription(jso.getString("RESTAURANT_DESCRIPTION"));
			rd.setMenu(jso.getString("RESTAURANT_MENU"));
			rd.setReviewOverall(jso.getString("RESTAURANT_REVIEW_OVERALL"));
			rd.setReviewFood(jso.getString("RESTAURANT_REVIEW_FOOD"));
			rd.setReviewService(jso.getString("RESTAURANT_REVIEW_SERVICE"));
			rd.setReviewAmbiance(jso.getString("RESTAURANT_REVIEW_AMBIANCE"));
			rd.setReviews(jso.getString("RESTAURANT_REVIEWS"));
			
			
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
	public void displayMiniBlock(View v){
		
		
		RestaurantData rd = RestaurantData.getInstance();

		TextView tv_RESTAURANT_NAME = (TextView) v.findViewById(R.id.RESTAURANT_MINI_NAME);
		TextView tv_RESTAURANT_ADDRESS = (TextView) v.findViewById(R.id.RESTAURANT_MINI_ADDRESS);
		TextView tv_RESTAURANT_PHONE= (TextView) v.findViewById(R.id.RESTAURANT_MINI_PHONE);
		TextView tv_RESTAURANT_CUISINE = (TextView) v.findViewById(R.id.RESTAURANT_MINI_CUISINE);
		TextView tv_RESTAURANT_PRICE= (TextView) v.findViewById(R.id.RESTAURANT_MINI_PRICE);
		TextView tv_RESTAURANT_HOURS= (TextView) v.findViewById(R.id.RESTAURANT_MINI_HOURS);
		TextView tv_RESTAURANT_PARKING= (TextView) v.findViewById(R.id.RESTAURANT_MINI_PARKING);
		TextView tv_RESTAURANT_DESCRIPTION = (TextView) v.findViewById(R.id.RESTAURANT_MINI_DESCRIPTION);
		TextView tv_RESTAURANT_MENU= (TextView) v.findViewById(R.id.RESTAURANT_MINI_MENU);
		TextView tv_RESTAURANT_REVIEW_OVERALL = (TextView) v.findViewById(R.id.RESTAURANT_MINI_REVIEW_OVERALL);
		TextView tv_RESTAURANT_REVIEW_FOOD = (TextView) v.findViewById(R.id.RESTAURANT_MINI_REVIEW_FOOD);
		TextView tv_RESTAURANT_REVIEW_SERVICE = (TextView) v.findViewById(R.id.RESTAURANT_MINI_REVIEW_SERVICE);
		TextView tv_RESTAURANT_REVIEW_AMBIENCE = (TextView) v.findViewById(R.id.RESTAURANT_MINI_REVIEW_AMBIENCE);
		tv_RESTAURANT_NAME.setText(rd.getRestaurantName());
		tv_RESTAURANT_ADDRESS.setText(rd.getRestaurantAddress());
		tv_RESTAURANT_PHONE.setText(rd.getRestaurantPhone());
		tv_RESTAURANT_CUISINE.setText(rd.getRestaurantCuisine());
		tv_RESTAURANT_PRICE.setText(rd.getRestaurantPrice());
		tv_RESTAURANT_HOURS.setText(rd.getRestaurantHours());
		tv_RESTAURANT_PARKING.setText(rd.getRestaurantParking());
		tv_RESTAURANT_DESCRIPTION.setText(rd.getRestaurantDescription());
		tv_RESTAURANT_MENU.setText(rd.getRestaurantMenu());
		tv_RESTAURANT_REVIEW_OVERALL.setText(rd.getRestaurantReviewOverall());
		tv_RESTAURANT_REVIEW_FOOD.setText(rd.getRestaurantReviewFood());
		tv_RESTAURANT_REVIEW_SERVICE.setText(rd.getRestaurantReviewService());
		tv_RESTAURANT_REVIEW_AMBIENCE.setText(rd.getRestaurantReviewAmbiance());
		
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
