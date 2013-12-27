package com.example.test;

import java.util.Calendar;
import java.util.Date;

public class SearchData {

	private static SearchData sInstance;
	
	private SearchData() {}
	
	private Date mSearchDate;
	private int mNoOfParticipants;
	
	public static synchronized SearchData getInstance() {
		if (sInstance == null) {
			sInstance = new SearchData();
		}
		return sInstance;
	}
	
	protected void clearAll() {
		// Set to default values
		mSearchDate = Calendar.getInstance().getTime();
		setNumberOfReservation(2);
	}
	
	public Date getDate(){
		return mSearchDate;
	}
	
	public int getNumberOfReservation(){
		return mNoOfParticipants;
	}
	

	protected void setDate(Date d) {
		mSearchDate = d;
	}

	protected void setNumberOfReservation(int i) {
		mNoOfParticipants = i;
	}
	
}