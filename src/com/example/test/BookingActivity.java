package com.example.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BookingActivity extends Activity {
	
//	private EditText mPhoneEditText;
	private EditText mSpecialRequestEditText;
	
	private View mBookingView;
	private View mBookingFormView;
	private View mBookingCompleteView;
	
	private View mBookingStatusView;
	
	private TextView mBookingSuccessMessage;
	
	private BookingTask mBookingTask;
	
	private View mBookingInfoView;
	private View mBookingInfoViewComplete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		
//		mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
		mSpecialRequestEditText = (EditText) findViewById(R.id.specialRequestEditText);
		
		mBookingView = findViewById(R.id.bookingView);
		
		mBookingFormView = findViewById(R.id.bookingFormView);
		mBookingCompleteView = findViewById(R.id.bookingCompleteView);
		
		mBookingStatusView = findViewById(R.id.bookingStatusView);
		
		mBookingSuccessMessage = (TextView) findViewById(R.id.bookingSuccessMessage);
		mBookingSuccessMessage.setText((UserData.getInstance().isLogin()?UserData.getInstance().getFirstName():BookingManager.getInstance(this).getGuestFirstName())+", you're all set!");
		
		mBookingInfoView = (View) findViewById(R.id.bookingInfoView);
		RestaurantManager.getInstance(this).displayMiniBlock(mBookingInfoView);
		
		mBookingInfoViewComplete = (View) findViewById(R.id.bookingInfoViewComplete);
		RestaurantManager.getInstance(this).displayMiniBlock(mBookingInfoViewComplete);
		
//		if (UserData.getInstance().isLogin()) {
//			mPhoneEditText.setText(UserData.getInstance().getPhone());
//		} else {
//			mPhoneEditText.setText(BookingManager.getInstance(this).getGuestPhone());
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.booking, menu);
		return true;
	}
	
	public void cancelButton_onClick(View view) {
		BookingManager.getInstance(this).cancelBooking();
	}
	
	public void reserveButton_onClick(View view) {
//		String phoneNumber = mPhoneEditText.getText().toString();
//		// Check for a valid phone.
//		if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 8) {
//			mPhoneEditText.setError(TextUtils.isEmpty(phoneNumber)?getString(R.string.error_field_required):"Please enter a valid phone");
//			mPhoneEditText.requestFocus();
//			return;
//		}
		
		showProgress(true);
		mBookingTask = new BookingTask();
		mBookingTask.execute((Void) null);
	}
	
	public void closeButton_onClick(View view) {
		BookingManager.getInstance(this).completeBooking();
	}
	
	public void addToCalendarButton_onClick(View view) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, "Reservation at ...");
		intent.putExtra(Events.EVENT_LOCATION, "BiBo");
		intent.putExtra(Events.DESCRIPTION, "Lunch with ...");
		intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
		Calendar now = Calendar.getInstance();
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, now.getTimeInMillis());
		now.add(Calendar.HOUR, 2);
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, now.getTimeInMillis());
		intent.setData(CalendarContract.Events.CONTENT_URI);
		if (intent.resolveActivity(getPackageManager()) != null) {
		    startActivity(intent);
		}
	}
	
	public void emailFriendsButton_onClick(View view) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Reservation at ......");
		intent.putExtra(Intent.EXTRA_TEXT, "Let's eat! I reserved a table for ......");
		
		if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"));
		}
	}
	
	public void directionsToThereButton_onClick(View view) {
		double latitude = 0;
		double longitude = 0;
		String label = "Label";
		String uriBegin = "geo:" + latitude + "," + longitude;
		String query = latitude + "," + longitude + "(" + label + ")";
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		if (intent.resolveActivity(getPackageManager()) != null) {
		    startActivity(intent);
		}
	}
	
	
	public class BookingTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				// Simulate network access.
				Thread.sleep(400);
			} catch (InterruptedException e) {
				return false;
			}
			
			String userId = UserData.getInstance().isLogin()?UserData.getInstance().getUserId():"0";
			String restaurantId = RestaurantData.getInstance().getRestaurantID();
			String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(SearchData.getInstance().getChosenDate());
			String noOfParticipant = Integer.toString(SearchData.getInstance().getNumberOfReservation());
			String specialRequest = mSpecialRequestEditText.getText().toString();
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("userID", userId);
			params.put("merchantID", restaurantId);
			params.put("numberOfParticipant", noOfParticipant);
			params.put("datetime", datetime);
			params.put("specialRequest", specialRequest);
			
			String jsonString = Utils.post("http://10.0.2.2:8888/reservations", params);
			try {
				JSONObject json = new JSONObject(jsonString);
				
				
				//Start : for notification
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		        
		        Intent intent = new Intent(CustomApplication.getContext(), AlarmReceiver.class);
		        intent.putExtra("bookingID", json.getString("bookingID"));
		        // PendingIntent for AlarmManager 
		        PendingIntent pendingIntent = PendingIntent.getBroadcast(CustomApplication.getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
		        // Just in case we have already set up AlarmManager,
		        // we do cancel.
		        am.cancel(pendingIntent);

		        //Some simple code to define time of notification:
		        Calendar cal = Calendar.getInstance();
		        /* for production
		        cal.setTime(SearchData.getInstance().getChosenDate());
		        cal.add(Calendar.MINUTE, -15);
		        */
		        /*
		         * <forTesting>
		         */
		        cal.setTime(new Date());
		        cal.add(Calendar.MINUTE, 1);
		        /*
		         * </forTesting>
		         */
		        
		        Date stamp =  cal.getTime();
		        /*
		        int pos = notifyTime.indexOf(":");
		        int hour = Integer.parseInt(notifyTime.substring(0, pos));
		        int minute = Integer.parseInt(notifyTime.substring(pos+1));
		        stamp.setHours(hour);
		        stamp.setMinutes(minute);
		        stamp.setSeconds(0);
				*/
		        
		        
		        // In case it's too late notify user today
		        if(stamp.getTime() < System.currentTimeMillis())
		            stamp.setTime(stamp.getTime() + AlarmManager.INTERVAL_DAY);
		                
		        // Set one-time alarm
		        am.set(AlarmManager.RTC_WAKEUP, stamp.getTime(), pendingIntent);
		        Log.i("client notification", "alarm set");
				
				
				
				
				
				
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			
	        
			mBookingTask = null;
			showProgress(false);
			mBookingFormView.setVisibility(success?View.GONE:View.VISIBLE);
			mBookingCompleteView.setVisibility(success?View.VISIBLE:View.GONE);
			
			setTitle("Booking Confirmed");
		}

		@Override
		protected void onCancelled() {
			mBookingTask = null;
			showProgress(false);
		}
		
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);
			
			mBookingStatusView.setVisibility(View.VISIBLE);
			mBookingStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mBookingStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mBookingView.setVisibility(View.VISIBLE);
			mBookingView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mBookingView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mBookingStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mBookingView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
