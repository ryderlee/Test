package com.example.test;

import android.content.Context;
import android.content.SharedPreferences;

public class RestaurantData {

	private static final String RESTAURANTDATA_NAME= "com.example.test.RESTAURANTDATA_NAME";
	private static final String RESTAURANTDATA_ID= "com.example.test.RESTAURANTDATA_ID";
	private static final String RESTAURANTDATA_ADDRESS= "com.example.test.RESTAURANTDATA_ADDRESS";
	private static final String RESTAURANTDATA_PHONE= "com.example.test.RESTAURANTDATA_PHONE";
	private static final String RESTAURANTDATA_CUISINE= "com.example.test.RESTAURANTDATA_CUISINE";
	private static final String RESTAURANTDATA_PRICE= "com.example.test.RESTAURANTDATA_PRICE";
	private static final String RESTAURANTDATA_HOURS= "com.example.test.RESTAURANTDATA_HOURS";
	private static final String RESTAURANTDATA_PARKING= "com.example.test.RESTAURANTDATA_PARKING";
	private static final String RESTAURANTDATA_DESCRIPTION= "com.example.test.RESTAURANTDATA_DESCRIPTION";
	private static final String RESTAURANTDATA_MENU= "com.example.test.RESTAURANTDATA_MENU";
	private static final String RESTAURANTDATA_REVIEW_OVERALL= "com.example.test.RESTAURANTDATA_REVIEW_OVERALL";
	private static final String RESTAURANTDATA_REVIEW_FOOD= "com.example.test.RESTAURANTDATA_REVIEW_FOOD";
	private static final String RESTAURANTDATA_REVIEW_SERVICE= "com.example.test.RESTAURANTDATA_REVIEW_SERVICE";
	private static final String RESTAURANTDATA_REVIEW_AMBIANCE= "com.example.test.RESTAURANTDATA_REVIEW_AMBIANCE";
	private static final String RESTAURANTDATA_REVIEWS= "com.example.test.RESTAURANTDATA_REVIEWS";
	
	private static RestaurantData sInstance;
	
	private Context mApplicationContext;
	private SharedPreferences mPrefs;
	
	private RestaurantData() {}
	
	public static synchronized RestaurantData getInstance(Context mApplicationContext) {
		if (sInstance == null) {
			sInstance = new RestaurantData();
			sInstance.mApplicationContext = mApplicationContext;
			sInstance.mPrefs = mApplicationContext.getSharedPreferences("com.example.test", Context.MODE_PRIVATE);
		}
		return sInstance;
	}
	
	protected void clearAll() {
		this.setID("");
		this.setName("");
		this.setAddress("");
		this.setPhone("");
		this.setCuisine("");
		this.setPrice("");
		this.setHours("");
		this.setParking("");
		this.setDescription("");
		this.setMenu("");
		this.setReviewOverall("");
		this.setReviewFood("");
		this.setReviewService("");
		this.setReviewAmbiance("");
		this.setReviews("");
	}
	
	String setPhone(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewAmbiance(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewService(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewFood(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewOverall(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setMenu(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setDescription(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setParking(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setHours(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setPrice(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setCuisine(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setAddress(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setName(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setID(String string) {
		// TODO Auto-generated method stub
		
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
	}

	protected String setReviews(String string) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
	}

	public static void setIntance(RestaurantData rd) {
		// TODO Auto-generated method stub
		sInstance = rd;
	}

	
}