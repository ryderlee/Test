package com.example.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class BookingPicker extends LinearLayout {

	static final private int MAX_PEOPLE = 20;
	
	private NumberPicker mNumOfPeoplePicker;
	private NumberPicker mDatePicker;
	private NumberPicker mTimePicker;
	
	private ArrayList<Date> mDates;
	
	private OnValueChangeListener mOnValueChangeListener;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm a");
	private SimpleDateFormat psdf = new SimpleDateFormat("yyyyMMdd");
	
	public BookingPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_booking_picker, this, true);
		
		mNumOfPeoplePicker = (NumberPicker) getChildAt(0);
		mDatePicker = (NumberPicker) getChildAt(1);
		mTimePicker = (NumberPicker) getChildAt(2);
		
		mDates = new ArrayList<Date>();
		mOnValueChangeListener = null;
	
		
		String numOfPeopleStrings[] = new String[MAX_PEOPLE];
		numOfPeopleStrings[0] = "1 person";
		for (int i=1; i<MAX_PEOPLE; i++) {
			numOfPeopleStrings[i] = (i+1)+" people";
		}
		mNumOfPeoplePicker.setMinValue(1);
		mNumOfPeoplePicker.setMaxValue(MAX_PEOPLE);
		mNumOfPeoplePicker.setWrapSelectorWheel(false);
		mNumOfPeoplePicker.setDisplayedValues(numOfPeopleStrings);
		mNumOfPeoplePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				bookingValueChanged();
			}
		});
		
		Calendar today = Calendar.getInstance();
		Calendar oneYearLater = Calendar.getInstance();
		oneYearLater.add(Calendar.YEAR, 1);
		ArrayList<String> dates = new ArrayList<String>();
		int idx = 0;
		while (today.getTime().before(oneYearLater.getTime())) {
			mDates.add(today.getTime());
			switch(idx) {
			case 0:
				dates.add("today");
				break;
			case 1:
				dates.add("tomorrow");
				break;
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				dates.add(new SimpleDateFormat("EEE").format(today.getTime()));
				break;
			default:
				dates.add(new SimpleDateFormat("EEE, d MMM").format(today.getTime()));
			}
			today.add(Calendar.DATE, 1);
			idx++;
		}
		String dateStrings[] = dates.toArray(new String[dates.size()]);
		mDatePicker.setMaxValue(dates.size()-1);
		mDatePicker.setWrapSelectorWheel(false);
		mDatePicker.setDisplayedValues(dateStrings);
		mDatePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				bookingValueChanged();
			}
		});

		
		String timeStrings[] = new String[48];
		for (int i=0; i<48; i++) {
			timeStrings[i] = ((i%24)/2==0?12:(i%24)/2) + ":" + (i%2==0?"00":"30") + (i<24?" am":" pm");
		}
		mTimePicker.setMaxValue(47);
		mTimePicker.setWrapSelectorWheel(false);
		mTimePicker.setDisplayedValues(timeStrings);
		mTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				bookingValueChanged();
			}
		});
	}
	
	public void setOnValueChangeListener(OnValueChangeListener listener) {
		mOnValueChangeListener = listener;
	}
	
	public void bookingValueChanged() {
		if (mOnValueChangeListener != null) {
			int numOfPeople = mNumOfPeoplePicker.getValue();
			String timeStrings[] = mTimePicker.getDisplayedValues();
			String timeString = timeStrings[mTimePicker.getValue()];
			Date date = mDates.get(mDatePicker.getValue());
			try{
				date = sdf.parse("" + psdf.format(date) + " " + timeString);
			}catch(ParseException pe){
			}
			
			
			
			mOnValueChangeListener.onValueChange(numOfPeople, date);
		}
	}
	
	public BookingPicker(Context context) {
		this(context, null);
	}
	
	public static interface OnValueChangeListener {
		public abstract void onValueChange(int numOfPeople, Date date);
	}

}
