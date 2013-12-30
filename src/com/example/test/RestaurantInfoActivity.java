package com.example.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.test.BookingPicker.OnValueChangeListener;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v4.app.*;
import android.support.v7.app.*;

public class RestaurantInfoActivity extends ActionBarActivity {
	
	private RestaurantInfoRequestTask mRestaurantInfoRequestTask = null;

	private View mInfoView;
	private View mProgressView;
	private RadioGroup mRadioGroup;
	
	private View mMenuView;
	private View mDetailView;
	private View mReviewsView;
	
	
	private TimeSlotsRequestTask mTimeSlotsRequestTask = null;
	private Date mTargetTime;
	private int mTargetNoOfParticipants;
	
	private View mTimeSlotsView;
	private BookingPicker mBookingPicker;
	private Button mPickerButton;
	private ProgressBar mPickerProgressBar;
	private HorizontalScrollView mTimeSlotsScrollView;
	private LinearLayout mTimeSlotsContainer;
	
	private ArrayList<Date> mAvailableTimeSlots;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mInfoView = findViewById(R.id.RESTAURANT_INFO_VIEW);
		mProgressView = findViewById(R.id.RESTAURANT_INFO_PROGRESS);
		
		mMenuView = findViewById(R.id.RESTAURANT_INFO_MENU_VIEW);
		mDetailView = findViewById(R.id.RESTAURANT_INFO_DETAIL_VIEW);
		mReviewsView = findViewById(R.id.RESTAURANT_INFO_REVIEWS_VIEW);
		
		
		mTimeSlotsView = (View) findViewById(R.id.timeSlotsView);
		mBookingPicker = (BookingPicker) findViewById(R.id.bookingPicker);
		mPickerButton = (Button) findViewById(R.id.pickerButton);
		mPickerProgressBar = (ProgressBar) findViewById(R.id.pickerProgressBar);
		mTimeSlotsScrollView = (HorizontalScrollView) findViewById(R.id.timeSlotsScrollView);
		mTimeSlotsContainer = (LinearLayout) findViewById(R.id.timeSlotsContainer);
		
		mTargetTime = SearchData.getInstance().getDate();
		mTargetNoOfParticipants = SearchData.getInstance().getNumberOfReservation();
		
		mBookingPicker.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(int noOfParticipants, Date date) {
				Calendar target = Calendar.getInstance();
				target.setTime(date);
				Calendar today = Calendar.getInstance();
				Calendar tmr = Calendar.getInstance();
				tmr.add(Calendar.DATE, 1);
				String dateStr = "";
				if (today.get(Calendar.YEAR) == target.get(Calendar.YEAR) && today.get(Calendar.MONTH) == target.get(Calendar.MONTH) && today.get(Calendar.DATE) == target.get(Calendar.DATE)) {
					dateStr = "today";
				} else if (tmr.get(Calendar.YEAR) == target.get(Calendar.YEAR) && tmr.get(Calendar.MONTH) == target.get(Calendar.MONTH) && tmr.get(Calendar.DATE) == target.get(Calendar.DATE)) {
					dateStr = "tomorrow";
				} else {
					dateStr = new SimpleDateFormat("EEE, d MMM").format(date);
				}
				String timeStr = new SimpleDateFormat("h:mm aa").format(date).toLowerCase();
				String bookingStr = "Table for " + noOfParticipants + ", " + dateStr + " at " + timeStr;
				mPickerButton.setText(bookingStr);
			}
		});
		mBookingPicker.setDate(mTargetTime);
		mBookingPicker.setNoOfParticipants(mTargetNoOfParticipants);
		
		
		mRestaurantInfoRequestTask = new RestaurantInfoRequestTask();
		mRestaurantInfoRequestTask.execute((Void) null);

		mRadioGroup = (RadioGroup) findViewById(R.id.RESTAURANT_INFO_TAB);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				showView(checkedId);
			}
		});
        mRadioGroup.check(R.id.infoRadioButton);
	}
	
	private void showView(int viewId) {
		mMenuView.setVisibility(View.GONE);
		mDetailView.setVisibility(View.GONE);
		mReviewsView.setVisibility(View.GONE);
		
		switch (viewId) {
		case R.id.menuRadioButton:
			mMenuView.setVisibility(View.VISIBLE);
			break;
		case R.id.infoRadioButton:
			mDetailView.setVisibility(View.VISIBLE);
			break;
		case R.id.reviewsRadioButton:
			mReviewsView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurant_info, menu);
		return true;
	}
	
	private void updateRestaurantInfo(JSONObject json) {
		try {
			TextView nameView = (TextView) findViewById(R.id.RESTAURANT_INFO_NAME);
			TextView addressView = (TextView) findViewById(R.id.RESTAURANT_INFO_ADDRESS);
			TextView phoneView = (TextView) findViewById(R.id.RESTAURANT_INFO_PHONE);
			TextView cuisineView = (TextView) findViewById(R.id.RESTAURANT_INFO_CUSINE);
			TextView priceView = (TextView) findViewById(R.id.RESTAURANT_INFO_PRICE);
			TextView hoursView = (TextView) findViewById(R.id.RESTAURANT_INFO_HOURS);
			TextView parkingView = (TextView) findViewById(R.id.RESTAURANT_INFO_PARKING);
			TextView descriptionView = (TextView) findViewById(R.id.RESTAURANT_INFO_DESCRIPTION);
			nameView.setText(json.getString("RESTAURANT_NAME"));
			addressView.setText(json.getString("RESTAURANT_ADDRESS"));
			phoneView.setText(json.getString("RESTAURANT_PHONE"));
			cuisineView.setText(json.getString("RESTAURANT_CUISINE"));
			priceView.setText(json.getString("RESTAURANT_PRICE"));
			hoursView.setText(json.getString("RESTAURANT_HOURS"));
			parkingView.setText(json.getString("RESTAURANT_PARKING"));
			descriptionView.setText(json.getString("RESTAURANT_DESCRIPTION"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void pickerButton_onClick(View view) {
		if (mBookingPicker.getVisibility()==View.VISIBLE) {
			mBookingPicker.setVisibility(View.GONE);
			mTimeSlotsView.setVisibility(View.VISIBLE);
			if (mBookingPicker.getDate().compareTo(mTargetTime) != 0 || mBookingPicker.getNoOfParticipants() != mTargetNoOfParticipants) {
				mTargetTime = mBookingPicker.getDate();
				mTargetNoOfParticipants = mBookingPicker.getNoOfParticipants();
				
				mTimeSlotsScrollView.setVisibility(View.GONE);
				mPickerProgressBar.setVisibility(View.VISIBLE);
				
				if (mTimeSlotsRequestTask != null) {
					mTimeSlotsRequestTask.cancel(true);
				}
				mTimeSlotsRequestTask = new TimeSlotsRequestTask();
				mTimeSlotsRequestTask.execute((Void) null);
			}
		} else {
			mBookingPicker.setVisibility(View.VISIBLE);
			mTimeSlotsView.setVisibility(View.GONE);
		}
	}
	
	private void displayTimeSlots() {
		mTimeSlotsContainer.removeAllViews();
		
		mTimeSlotsScrollView.setVisibility(View.VISIBLE);
		mPickerProgressBar.setVisibility(View.GONE);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public void reserveButton_onClick(View view) {
		UserManager.getInstance(this).login(true);
	}
	
	public void albumButton_onClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, FullScreenAlbumActivity.class);
		startActivity(intent);
	}

	@Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getStringExtra("ACTION");
        if (action != null) {
	    	if (action.equals(Constants.ACTION_LOGIN_SUCCESS)) {
	            int numOfPeople = 1;
	            int timeSlotId = 1;
	            BookingManager.getInstance(this).book(numOfPeople, timeSlotId);
	    	} else if (action.equals(Constants.ACTION_BOOK_AS_GUEST)) {
	            int numOfPeople = 1;
	            int timeSlotId = 1;
	    		BookingManager.getInstance(this).bookAsGuest(numOfPeople, timeSlotId);
	    	} else if (action.equals(Constants.ACTION_GUEST_CANCEL)) {
	    		UserManager.getInstance(this).login(true);
	    	} else if (action.equals(Constants.ACTION_BOOK_CANCEL)) {
	    		
	    	} else if (action.equals(Constants.ACTION_BOOK_SUCCESS)) {
	    		
	    	}
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void hideProgress() {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(View.GONE);
						}
					});

			mInfoView.animate().setDuration(shortAnimTime)
					.alpha(1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mInfoView.setVisibility(View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(View.GONE);
			mInfoView.setVisibility(View.VISIBLE);
		}
	}
	
	
	private class RestaurantInfoRequestTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			
			String jsonString = Utils.getJsonString("http://10.0.2.2:8888/index.php/merchants/1");
			try {
				JSONObject json = new JSONObject(jsonString);
				updateRestaurantInfo(json);
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRestaurantInfoRequestTask = null;

			if (success) {
				hideProgress();
			} else {
			}
		}

		@Override
		protected void onCancelled() {
			mRestaurantInfoRequestTask = null;
			hideProgress();
		}
	}
	
	private class TimeSlotsRequestTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			
			return true;
		}
		

		@Override
		protected void onPostExecute(final Boolean success) {
			mTimeSlotsRequestTask = null;

			if (success) {
				displayTimeSlots();
			} else {
			}
		}
		
	}
}
