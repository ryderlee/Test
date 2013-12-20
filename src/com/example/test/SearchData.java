package com.example.test;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.SharedPreferences;

import java.util.Date;
import java.util.Calendar;

public class SearchData {

	
	private static SearchData sInstance;
	
	private Context mApplicationContext;
	private SharedPreferences mPrefs;
	
	private static final String SEARCHDATA_DATETIME= "com.example.test.SEARCHDATA_DATETIME";
	private static final String SEARCHDATA_NUMBEROFRESERVATION = "com.example.test.USERDATA_NUMBEROFRESERVATION";
	
	private	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
	private	static Date d;
	private static String defaultDateTimeString = "19000101 000001";
	private SearchData() {}
	
	public static synchronized SearchData getInstance(Context mApplicationContext) {
		try{
				dateFormat.parse(defaultDateTimeString);
			
			
				sInstance = new SearchData();
				sInstance.mApplicationContext = mApplicationContext;
				sInstance.mPrefs = mApplicationContext.getSharedPreferences("com.example.test", Context.MODE_PRIVATE);
			}catch(ParseException e){
				
			}
		return sInstance;
	}
	
	protected void clearAll() {
		try{
			this.setDateTime(dateFormat.parse(defaultDateTimeString));
			this.setNumberOfReservation(0);
		}catch(ParseException e){
		}
	}

	protected Date setDateTime(Date d) {
		// TODO Auto-generated method stub
		mPrefs.edit().putString( SEARCHDATA_DATETIME, dateFormat.format(d)).commit();
		return d;
		
	}

	protected int setNumberOfReservation(int i) {
		// TODO Auto-generated method stub
		mPrefs.edit().putInt( SEARCHDATA_NUMBEROFRESERVATION, i).commit();
		return i;
		
	}

	public static void setIntance(SearchData rd) {
		// TODO Auto-generated method stub
		sInstance = rd;
	}

	
}