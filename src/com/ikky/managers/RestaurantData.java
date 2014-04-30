package com.ikky.managers;

import java.util.ArrayList;

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
	private double restaurantLatitude;
	private double restaurantLongitude;
	private ArrayList<String> restaurantImages;
	
	private static RestaurantData sInstance;
	
	private RestaurantData() {}
	
	public static synchronized RestaurantData getInstance() {
		if (sInstance == null) {
			sInstance = new RestaurantData();
		}
		return sInstance;
	}
	
	public void clearAll() {
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
		this.setImages(new ArrayList<String>());
	}
	
	public void setPhone(String string) {
		this.restaurantPhone = string;
	}

	public void setReviewAmbiance(String string) {
		this.restaurantReviewAmbiance=string;
	}

	public void setReviewService(String string) {
		this.restaurantReviewService= string;
		
	}

	public void setReviewFood(String string) {
		this.restaurantReviewFood=string;
	}

	public void setReviewOverall(String string) {
		this.restaurantReviewOverall=string;
	}

	public void setMenu(String string) {
		this.restaurantMenu = string;
	}

	public void setDescription(String string) {
		this.restaurantDescription = string;
	}

	public void setParking(String string) {
		this.restaurantParking = string;
	}

	public void setHours(String string) {
		this.restaurantHours = string;
	}

	public void setPrice(String string) {
		this.restaurantPrice = string;
	}

	public void setCuisine(String string) {
		this.restaurantCuisine = string;
	}

	public void setAddress(String string) {
		this.restaurantAddress = string;
	}

	public void setName(String string) {
		this.restaurantName = string;
	}

	public void setID(String string) {
		this.restaurantID = string;
	}

	public void setReviews(String string) {
		this.restaurantReviews = string;
	}
	
	public void setLatitude(double latitude) {
		this.restaurantLatitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.restaurantLongitude = longitude;
	}
	
	public void setImages(ArrayList<String> images) {
		this.restaurantImages = images;
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
	
	public double getRestaurantLatitude() {
		return restaurantLatitude;
	}

	public double getRestaurantLongitude() {
		return restaurantLongitude;
	}
	
	public ArrayList<String> getRestaurantImages() {
		return restaurantImages;
	}
	
}