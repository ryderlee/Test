package com.example.test;

import android.content.Context;
import android.content.SharedPreferences;

public class RestaurantData {

	private String restaurantName;
	private String restaurantID;
	private String restaurantAddress;
	private String restaurantPhone;
	private String restaurantCuisine;
	private String restaurantPrice;
	private String restaurantHours;
	private String restaurantParking;
	private String restaurantDescription;
	private String restaurantMenu;
	private String restaurantReviewOverall;
	private String restaurantReviewFood;
	private String restaurantReviewService;
	private String restaurantReviewAmbiance;
	private String restaurantReviews;
	
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
	
	//private Context mApplicationContext;
	//private SharedPreferences mPrefs;
	
	private RestaurantData() {}
	
	public static synchronized RestaurantData getInstance() {
		if (sInstance == null) {
			sInstance = new RestaurantData();
			//sInstance.mApplicationContext = mApplicationContext;
			//sInstance.mPrefs = mApplicationContext.getSharedPreferences("com.example.test", Context.MODE_PRIVATE);
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
		this.restaurantPhone = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewAmbiance(String string) {
		// TODO Auto-generated method stub
		this.restaurantReviewAmbiance=string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewService(String string) {
		// TODO Auto-generated method stub
		this.restaurantReviewService= string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewFood(String string) {
		// TODO Auto-generated method stub
		this.restaurantReviewFood=string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setReviewOverall(String string) {
		// TODO Auto-generated method stub
		this.restaurantReviewOverall=string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setMenu(String string) {
		// TODO Auto-generated method stub
		this.restaurantMenu = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setDescription(String string) {
		// TODO Auto-generated method stub
		this.restaurantDescription = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setParking(String string) {
		// TODO Auto-generated method stub
		this.restaurantParking = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setHours(String string) {
		// TODO Auto-generated method stub
		this.restaurantHours = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setPrice(String string) {
		// TODO Auto-generated method stub
		this.restaurantPrice = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setCuisine(String string) {
		// TODO Auto-generated method stub
		this.restaurantCuisine = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setAddress(String string) {
		// TODO Auto-generated method stub
		this.restaurantAddress = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setName(String string) {
		// TODO Auto-generated method stub
		this.restaurantName = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
		
	}

	protected String setID(String string) {
		// TODO Auto-generated method stub
		this.restaurantID = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
	}

	protected String setReviews(String string) {
		// TODO Auto-generated method stub
		this.restaurantReviews = string;
		//mPrefs.edit().putString( RESTAURANTDATA_REVIEWS, string).commit();
		return string;
	}

	public static void setIntance(RestaurantData rd) {
		// TODO Auto-generated method stub
		sInstance = rd;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getRestaurantID() {
		return restaurantID;
	}

	public String getRestaurantAddress() {
		return restaurantAddress;
	}

	public String getRestaurantPhone() {
		return restaurantPhone;
	}

	public String getRestaurantCuisine() {
		return restaurantCuisine;
	}

	public String getRestaurantPrice() {
		return restaurantPrice;
	}

	public String getRestaurantHours() {
		return restaurantHours;
	}

	public String getRestaurantParking() {
		return restaurantParking;
	}

	public String getRestaurantDescription() {
		return restaurantDescription;
	}

	public String getRestaurantMenu() {
		return restaurantMenu;
	}

	public String getRestaurantReviewOverall() {
		return restaurantReviewOverall;
	}

	public String getRestaurantReviewFood() {
		return restaurantReviewFood;
	}

	public String getRestaurantReviewService() {
		return restaurantReviewService;
	}

	public String getRestaurantReviewAmbiance() {
		return restaurantReviewAmbiance;
	}

	public String getRestaurantReviews() {
		return restaurantReviews;
	}
	
}