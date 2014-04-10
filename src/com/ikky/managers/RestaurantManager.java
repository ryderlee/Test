package com.ikky.managers;

import java.text.SimpleDateFormat;

import com.ikky.activities.R;
import com.ikky.activities.RestaurantInfoActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class RestaurantManager {

	private static RestaurantManager sInstance;
	
	private Activity mActivity;
	
	public static synchronized RestaurantManager getInstance(Activity mActivity) {
		if (sInstance == null) {
			sInstance = new RestaurantManager();
		}
		sInstance.mActivity = mActivity;
		return sInstance;
	}
	
	public void showMain(String id) {
		
		RestaurantData.getInstance().setID(id);
		
		Intent intent = new Intent();
		intent.setClass(this.mActivity, RestaurantInfoActivity.class);
		intent.putExtra("ACTION", "SHOWBOOKING");
		this.mActivity.startActivity(intent);
//		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public void displayMiniBlock(View v){
		RestaurantData rd = RestaurantData.getInstance();

		TextView tv_BOOKING_DATE_TIME = (TextView) v.findViewById(R.id.RESTAURANT_MINI_DATE_TIME);
		TextView tv_BOOKING_NO_OF_PARTICIPANTS = (TextView) v.findViewById(R.id.RESTAURANT_MINI_NO_OF_PARTICIPANTS);
		tv_BOOKING_DATE_TIME.setText(new SimpleDateFormat("EEEE, MMMM dd 'at' HH:mm").format(SearchData.getInstance().getChosenDate()));
		tv_BOOKING_NO_OF_PARTICIPANTS.setText(Integer.toString(SearchData.getInstance().getNumberOfReservation()));
		
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
		Typeface typefaceRobotoRegular = Typeface.createFromAsset(this.mActivity.getAssets(), "fonts/Roboto-Regular.ttf");
		tv_RESTAURANT_NAME.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_ADDRESS.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_PHONE.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_CUISINE.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_PRICE.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_HOURS.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_PARKING.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_DESCRIPTION.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_MENU.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_REVIEW_OVERALL.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_REVIEW_FOOD.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_REVIEW_SERVICE.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_REVIEW_AMBIENCE.setTypeface(typefaceRobotoRegular);
		
	}
	
	private void backToTopActivity(String action) {
		Intent intent = new Intent();
		intent.setClass(this.mActivity, this.mActivity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("ACTION", action);
		this.mActivity.startActivity(intent);
//		this.mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
}
