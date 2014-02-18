package com.ikky.managers;

import com.ikky.activities.BookingActivity;
import com.ikky.activities.GuestInfoActivity;
import com.ikky.constants.Constants;

import android.app.Activity;
import android.content.Intent;

public class BookingManager {

	private static BookingManager sInstance;
	
	private Activity mActivity;
	private Activity mBookingTopActivity;
	
	private int mNumOfPeople;
	private int mTimeSlotId;
	
	public static synchronized BookingManager getInstance(Activity mActivity) {
		if (sInstance == null) {
			sInstance = new BookingManager();
		}
		sInstance.mActivity = mActivity;
		return sInstance;
	}
	
	public int getNumOfPeople() {
		return mNumOfPeople;
	}
	
	public int getTimeSlotId() {
		return mTimeSlotId;
	}

	public void book(int numOfPeople, int timeSlotId) {
		setBookingInfo(numOfPeople, timeSlotId);		
		
		Intent intent = new Intent();
		intent.setClass(mActivity, BookingActivity.class);
		mActivity.startActivity(intent);
//		mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	public void bookAsGuest(int numOfPeople, int timeSlotId) {
		setBookingInfo(numOfPeople, timeSlotId);
		
		Intent intent = new Intent();
		intent.setClass(mActivity, GuestInfoActivity.class);
		mActivity.startActivity(intent);
//		mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	public void setBookingInfo(int numOfPeople, int timeSlotId) {
		mBookingTopActivity = mActivity;
		
		mNumOfPeople = numOfPeople;
		mTimeSlotId = timeSlotId;
	}
	
	public void guestProceed() {
		Intent intent = new Intent();
		intent.setClass(mActivity, BookingActivity.class);
		mActivity.startActivity(intent);
//		mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		mActivity.finish();
	}
	
	public void cancelGatherGuestInfo() {
		backToRestaurantInfo(Constants.ACTION_GUEST_CANCEL);
	}
	
	public void completeBooking() {
		backToRestaurantInfo(Constants.ACTION_BOOK_SUCCESS);
	}
	
	public void cancelBooking() {
		backToRestaurantInfo(Constants.ACTION_BOOK_CANCEL);
	}
	
	
	private void backToRestaurantInfo(String action) {
		Intent intent = new Intent();
		intent.setClass(mActivity, mBookingTopActivity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("ACTION", action);
		mActivity.startActivity(intent);
	}
	
}
