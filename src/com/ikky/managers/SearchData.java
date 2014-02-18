package com.ikky.managers;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class SearchData {

	private static SearchData sInstance;
	
	private SearchData() {}
	
	private Date mSearchDate;
	private int mNoOfParticipants;
	private Date mChosenDate;
	
	public static synchronized SearchData getInstance() {
		if (sInstance == null) {
			sInstance = new SearchData();
			sInstance.clearAll();
		}
		return sInstance;
	}
	
	public void clearAll() {
		// Set to default values
		mSearchDate = Calendar.getInstance().getTime();
		mChosenDate = Calendar.getInstance().getTime();
		mNoOfParticipants = 2;
	}
	
	public Date getSearchDate(){
		return mSearchDate;
	}
	
	public Date getChosenDate(){
		return mChosenDate;
	}
	
	public int getNumberOfReservation(){
		return mNoOfParticipants;
	}
	

	public void setSearchDate(Date d) {
		Log.d("com.example.test", "set search date");
		mSearchDate = d;
		mChosenDate = d;
	}
	
	public void setChosenDate(Date d) {
		mChosenDate = d;
	}

	public void setNumberOfReservation(int i) {
		mNoOfParticipants = i;
	}
	
}