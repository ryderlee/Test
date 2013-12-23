package com.example.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class BookingPicker extends LinearLayout {

	static final private int MAX_PEOPLE = 20;
	
	private NumberPicker mNumOfReservationPicker;
	private NumberPicker mDatePicker;
	private NumberPicker mTimePicker;
	
	private ArrayList<Date> mDates;
	private ArrayList<Date> mTimes;
	
	private OnValueChangeListener mOnValueChangeListener;
	
	public BookingPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_booking_picker, this, true);
		
		mNumOfReservationPicker = (NumberPicker) getChildAt(0);
		mDatePicker = (NumberPicker) getChildAt(1);
		mTimePicker = (NumberPicker) getChildAt(2);
		
		mDates = new ArrayList<Date>();
		mTimes = new ArrayList<Date>();
		mOnValueChangeListener = null;
	
		
		String numOfReservationStrings[] = new String[MAX_PEOPLE];
		numOfReservationStrings[0] = "1 person";
		for (int i=1; i<MAX_PEOPLE; i++) {
			numOfReservationStrings[i] = (i+1)+" people";
		}
		mNumOfReservationPicker.setMinValue(1);
		mNumOfReservationPicker.setMaxValue(MAX_PEOPLE);
		mNumOfReservationPicker.setWrapSelectorWheel(false);
		mNumOfReservationPicker.setDisplayedValues(numOfReservationStrings);
		mNumOfReservationPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				bookingValueChanged();
			}
		});
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
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

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		String timeStrings[] = new String[48];
		for (int i=0; i<48; i++) {
			timeStrings[i] = (cal.get(Calendar.HOUR)==0?12:cal.get(Calendar.HOUR))+":"+String.format("%02d", cal.get(Calendar.MINUTE))+(cal.get(Calendar.AM_PM)==Calendar.AM?" am":" pm");
			mTimes.add(cal.getTime());
			cal.add(Calendar.MINUTE, 30);
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
		mTimePicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
					validateDateTime();
				}
			}
		});
	}
	
	public void setOnValueChangeListener(OnValueChangeListener listener) {
		mOnValueChangeListener = listener;
	}
	
	public void bookingValueChanged() {
		if (mOnValueChangeListener != null) {
			int numOfReservation = mNumOfReservationPicker.getValue();
			
			Calendar dateCal = Calendar.getInstance();
			Calendar timeCal = Calendar.getInstance();
			dateCal.setTime(mDates.get(mDatePicker.getValue()));
			timeCal.setTime(mTimes.get(mTimePicker.getValue()));
			dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
			dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			
			mOnValueChangeListener.onValueChange(numOfReservation, dateCal.getTime());
		}
	}
	
	public void validateDateTime() {
		Calendar dateCal = Calendar.getInstance();
		Calendar timeCal = Calendar.getInstance();
		dateCal.setTime(mDates.get(mDatePicker.getValue()));
		timeCal.setTime(mTimes.get(mTimePicker.getValue()));
		dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		
		Calendar now = Calendar.getInstance();
		if (dateCal.before(now)) {
			for (int i=0; i<mTimes.size(); i++) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(mTimes.get(i));
				if (cal.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY) || (cal.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.MINUTE) > now.get(Calendar.MINUTE))) {
					mTimePicker.setValue(i);
					bookingValueChanged();
					break;
				}
			}
		}
	}
	
	public BookingPicker(Context context) {
		this(context, null);
	}
	
	public static interface OnValueChangeListener {
		public abstract void onValueChange(int numOfReservation, Date date);
	}
	
	public void setDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		for (int i=0; i<mTimes.size(); i++) {
			Calendar timeCal = Calendar.getInstance();
			timeCal.setTime(mTimes.get(i));
			if (timeCal.get(Calendar.HOUR_OF_DAY) > cal.get(Calendar.HOUR_OF_DAY) || (timeCal.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY) && timeCal.get(Calendar.MINUTE) > cal.get(Calendar.MINUTE))) {
				mTimePicker.setValue(i);
				break;
			}
		}
		for (int i=0; i<mDates.size(); i++) {
			Calendar dateCal = Calendar.getInstance();
			dateCal.setTime(mDates.get(i));
			cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
			if (cal.equals(dateCal)) {
				mDatePicker.setValue(i);
				break;
			}
		}
		bookingValueChanged();
	}
	
	public void setNumOfReservation(int numOfReservation) {
		mNumOfReservationPicker.setValue(numOfReservation);
		bookingValueChanged();
	}
	
	public int getNumOfReservation() {
		return mNumOfReservationPicker.getValue();
	}
	
	public Date getDate() {
		Calendar dateCal = Calendar.getInstance();
		Calendar timeCal = Calendar.getInstance();;
		dateCal.setTime(mDates.get(mDatePicker.getValue()));
		timeCal.setTime(mTimes.get(mTimePicker.getValue()));
		dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE)); 
		return dateCal.getTime();
	}

}
