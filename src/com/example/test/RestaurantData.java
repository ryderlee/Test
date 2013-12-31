package com.example.test;

import org.json.JSONArray;

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
	
	private static RestaurantData sInstance;
	
	private RestaurantData() {}
	
	public static synchronized RestaurantData getInstance() {
		if (sInstance == null) {
			sInstance = new RestaurantData();
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
	
	protected void setPhone(String string) {
		this.restaurantPhone = string;
	}

	protected void setReviewAmbiance(String string) {
		this.restaurantReviewAmbiance=string;
	}

	protected void setReviewService(String string) {
		this.restaurantReviewService= string;
		
	}

	protected void setReviewFood(String string) {
		this.restaurantReviewFood=string;
	}

	protected void setReviewOverall(String string) {
		this.restaurantReviewOverall=string;
	}

	protected void setMenu(String string) {
		this.restaurantMenu = string;
	}

	protected void setDescription(String string) {
		this.restaurantDescription = string;
	}

	protected void setParking(String string) {
		this.restaurantParking = string;
	}

	protected void setHours(String string) {
		this.restaurantHours = string;
	}

	protected void setPrice(String string) {
		this.restaurantPrice = string;
	}

	protected void setCuisine(String string) {
		this.restaurantCuisine = string;
	}

	protected void setAddress(String string) {
		this.restaurantAddress = string;
	}

	protected void setName(String string) {
		this.restaurantName = string;
	}

	protected void setID(String string) {
		this.restaurantID = string;
	}

	protected void setReviews(String string) {
		this.restaurantReviews = string;
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