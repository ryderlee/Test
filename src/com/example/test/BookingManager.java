package com.example.test;

import android.app.Activity;
import android.content.Intent;

public class BookingManager {

	private static BookingManager sInstance;
	
	private Activity mActivity;
	private Activity mBookingTopActivity;
	
	private int mNumOfPeople;
	private int mTimeSlotId;
	private String mFirstName;
	private String mLastName;
	private String mEmail;
	private String mPhone;
	
	public static synchronized BookingManager getInstance(Activity mActivity) {
		if (sInstance == null) {
			sInstance = new BookingManager();
		}
		sInstance.mActivity = mActivity;
		return sInstance;
	}
	
	protected String getGuestFirstName() {
		return mFirstName;
	}
	
	protected String getGuestLastName() {
		return mLastName;
	}
	
	protected String getGuestEmail() {
		return mEmail;
	}
	
	protected String getGuestPhone() {
		return mPhone;
	}
	
	protected int getNumOfPeople() {
		return mNumOfPeople;
	}
	
	protected int getTimeSlotId() {
		return mTimeSlotId;
	}

	protected void book(int numOfPeople, int timeSlotId) {
		setBookingInfo(numOfPeople, timeSlotId);		
		
		Intent intent = new Intent();
		intent.setClass(mActivity, BookingActivity.class);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	protected void bookAsGuest(int numOfPeople, int timeSlotId) {
		setBookingInfo(numOfPeople, timeSlotId);
		
		Intent intent = new Intent();
		intent.setClass(mActivity, GuestInfoActivity.class);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	protected void setBookingInfo(int numOfPeople, int timeSlotId) {
		mBookingTopActivity = mActivity;
		
		mNumOfPeople = numOfPeople;
		mTimeSlotId = timeSlotId;
	}
	
	protected void setGuestInfo(String firstName, String lastName, String email, String phone) {
		mFirstName = firstName;
		mLastName = lastName;
		mEmail = email;
		mPhone = phone;
		
		Intent intent = new Intent();
		intent.setClass(mActivity, BookingActivity.class);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		mActivity.finish();
	}
	
	protected void cancelGatherGuestInfo() {
		backToRestaurantInfo(Constants.ACTION_GUEST_CANCEL);
	}
	
	protected void completeBooking() {
		backToRestaurantInfo(Constants.ACTION_BOOK_SUCCESS);
	}
	
	protected void cancelBooking() {
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
