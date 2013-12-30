package com.example.test;

import java.util.Calendar;
import java.util.Date;

public class SearchData {

	private static SearchData sInstance;
	
	private SearchData() {}
	
	private Date mSearchDate;
	private int mNoOfParticipants;
	private Date mChosenDate;
	
	public static synchronized SearchData getInstance() {
		if (sInstance == null) {
			sInstance = new SearchData();
		}
		return sInstance;
	}
	
	protected void clearAll() {
		// Set to default values
		mSearchDate = Calendar.getInstance().getTime();
		mChosenDate = Calendar.getInstance().getTime();
		setNumberOfReservation(2);
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
	

	protected void setSearchDate(Date d) {
		mSearchDate = d;
		mChosenDate = d;
	}
	
	protected void setChosenDate(Date d) {
		mChosenDate = d;
	}

	protected void setNumberOfReservation(int i) {
		mNoOfParticipants = i;
	}
	
}