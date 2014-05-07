package com.ikky.managers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import com.ikky.activities.R;
import com.ikky.activities.RestaurantInfoActivity;
import com.ikky.ui.PhotoView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		tv_BOOKING_DATE_TIME.setText(new SimpleDateFormat("EEEE, MMMM dd").format(SearchData.getInstance().getChosenDate()));
		tv_BOOKING_NO_OF_PARTICIPANTS.setText(Integer.toString(SearchData.getInstance().getNumberOfReservation())+" People");
		
		TextView tv_RESTAURANT_NAME = (TextView) v.findViewById(R.id.RESTAURANT_MINI_NAME);
		TextView tv_RESTAURANT_ADDRESS = (TextView) v.findViewById(R.id.RESTAURANT_MINI_ADDRESS);
		tv_RESTAURANT_NAME.setText(rd.getRestaurantName());
		tv_RESTAURANT_ADDRESS.setText(new SimpleDateFormat("HH:mm").format(SearchData.getInstance().getChosenDate()));
		Typeface typefaceRobotoRegular = Typeface.createFromAsset(this.mActivity.getAssets(), "fonts/Roboto-Regular.ttf");
		tv_RESTAURANT_NAME.setTypeface(typefaceRobotoRegular);
		tv_RESTAURANT_ADDRESS.setTypeface(typefaceRobotoRegular);
		
		LinearLayout container = (LinearLayout) v.findViewById(R.id.noOfParticipantsContainer);
		ImageView headIcon = (ImageView) container.getChildAt(0);
		if (SearchData.getInstance().getIsVip()) {
			headIcon.setImageResource(R.drawable.ic_vip);
		} else {
			headIcon.setImageResource(R.drawable.ic_people);
		}
		
		
		PhotoView photoView = (PhotoView) v.findViewById(R.id.bookingInfoPhotoView);
		
		try {
			URL imageUrl = new URL("http://ikky-phpapp-env.elasticbeanstalk.com/images/fullscreen/01.png");
			if (!RestaurantData.getInstance().getRestaurantImages().isEmpty()) {
				imageUrl = new URL(RestaurantData.getInstance().getRestaurantImages().get(0));
			}
			photoView.setImageURL(imageUrl, true, null);
		}
		catch(MalformedURLException mfe){}
		
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
