package com.ikky.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.ikky.activities.R;
import com.ikky.base.BaseActivity;
import com.ikky.helpers.ServerUtils;
import com.ikky.helpers.Utils;
import com.ikky.managers.UserData;
import com.ikky.managers.UserManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class UserProfileActivity extends BaseActivity {

	private RadioGroup mTab;
	private RadioButton mBookingsRadioButton;
	private ListView mBookingListView;
	private ProgressBar mProgressBar;
	private TextView mEmptyTextView;
	
	private FbLoginTask mFbLoginTask = null;
	
	private LoginButton mFbLoginButton;
	
	private ArrayAdapter<BookingItem> mAdapter;
	
	private BookingListTask mBookingListTask = null;
	private BookingUpdateTask mBookingUpdateTask = null;
	
	private View mLoginStatusView;
	private View mProfileView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		LayoutInflater inflater = LayoutInflater.from(this.getBaseContext());
		View actionBarView = inflater.inflate(R.layout.actionbar_view2, null);
        
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(actionBarView);
        Button upButton = (Button) getActionBar().getCustomView().findViewById(R.id.myUpButton);
        upButton.setText(getTitle());
        final Activity act = this;
        upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavUtils.navigateUpFromSameTask(act);
			}
		});
		
		mLoginStatusView = findViewById(R.id.login_status);
		mProfileView = findViewById(R.id.profileView);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mEmptyTextView = (TextView) findViewById(R.id.emptyTextView);
		mEmptyTextView.setTypeface(mTypefaceRobotoRegular);
		
		TextView usernameTextView = (TextView) getActionBar().getCustomView().findViewById(R.id.usernameTextView);
		usernameTextView.setTypeface(mTypefaceRobotoMedium);
		usernameTextView.setText(UserData.getInstance().getFullName());
		
		mFbLoginButton = (LoginButton) findViewById(R.id.fbSigninButton);
		
		mAdapter = new BookingListAdapter<BookingItem>(this, R.id.bookingsListView);
		mBookingListView = (ListView) findViewById(R.id.bookingsListView);
		mBookingListView.setAdapter(mAdapter);
		
		mBookingsRadioButton = (RadioButton) findViewById(R.id.bookingsRadioButton);
		
		mTab = (RadioGroup) findViewById(R.id.userProfileTab);
		mTab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.pointsRadioButton:
					loadPoints();
					break;
				case R.id.favouriteRadioButton:
					loadFavourites();
					break;
				case R.id.bookingsRadioButton:
					loadBookings();
					break;
				default:
					break;
				}
			}
		});
		mBookingsRadioButton.setChecked(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
		return true;
	}
	
	public void aboutButton_onClick(View view) {
	}
	
	public void signoutButton_onClick(View view) {
		UserManager.getInstance(this).logout();
	}
	
	private void loadPoints() {
		mProgressBar.setVisibility(View.GONE);
		mBookingListView.setVisibility(View.GONE);
		mEmptyTextView.setVisibility(View.VISIBLE);
		
		mEmptyTextView.setText("You haven't earned any points yet.");
	}
	
	private void loadFavourites() {
		mProgressBar.setVisibility(View.GONE);
		mBookingListView.setVisibility(View.GONE);
		mEmptyTextView.setVisibility(View.VISIBLE);
		
		mEmptyTextView.setText("No favourite restaurants yet.");
	}
	
	private void loadBookings() {
		mProgressBar.setVisibility(View.VISIBLE);
		mBookingListView.setVisibility(View.GONE);
		mEmptyTextView.setVisibility(View.GONE);
		
		mBookingListTask = new BookingListTask();
		mBookingListTask.execute((Void) null);
	}
	
	private void cancel(int bookingId) {
		mProgressBar.setVisibility(View.VISIBLE);
		mBookingListView.setVisibility(View.GONE);
		mEmptyTextView.setVisibility(View.GONE);
		
		mBookingUpdateTask = new BookingUpdateTask(bookingId, -1);
		mBookingUpdateTask.execute((Void) null);
	}
	
	private void confirm(int bookingId) {
		mProgressBar.setVisibility(View.VISIBLE);
		mBookingListView.setVisibility(View.GONE);
		mEmptyTextView.setVisibility(View.GONE);
		
		mBookingUpdateTask = new BookingUpdateTask(bookingId, 1);
		mBookingUpdateTask.execute((Void) null);
	}
	
	private class BookingItem {
		public int bookingId;
		public String restaurantName;
		public int noOfParticipants;
		public Date bookingDate;
		public int status;
	}
	
	@Override
	protected void fbOnSessionOpened() {
		mFbLoginButton.setVisibility(View.GONE);
		if (UserData.getInstance().getFbToken().isEmpty()) {
			this.showProgress(true);
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

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mProfileView.setVisibility(View.VISIBLE);
			mProfileView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProfileView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	@Override
	protected void fbOnUserInfoCallback(String email, String fbId, String username, String firstName, String lastName, String token, String expireTs) {
		mFbLoginTask = new FbLoginTask(email, fbId, username, firstName, lastName, token, expireTs);
		mFbLoginTask.execute((Void) null);
	}
	
	private class BookingListAdapter<T> extends ArrayAdapter<T> {
		public BookingListAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(this.getContext());
				convertView = inflater.inflate(R.layout.view_booking_row, parent, false);
			}
	    	
	    	BookingItem item = (BookingItem) this.getItem(position);
	    	TextView restaurantNameTextView = (TextView) convertView.findViewById(R.id.restaurantNameTextView);
	    	TextView bookingInfoTextView = (TextView) convertView.findViewById(R.id.bookingInfoTextView);
	    	restaurantNameTextView.setTypeface(mTypefaceRobotoRegular);
	    	restaurantNameTextView.setText(item.restaurantName);
	    	bookingInfoTextView.setTypeface(mTypefaceRobotoRegular);
	    	bookingInfoTextView.setText(Utils.getLongBookingInfo(item.bookingDate, item.noOfParticipants));
	    	
	    	convertView.setTag(item.bookingId);
	    	
	    	ImageButton cancelButton = (ImageButton) convertView.findViewById(R.id.cancelButton);
	    	
	    	cancelButton.setVisibility(item.status==0?View.VISIBLE:View.GONE);
	    	
	    	cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					cancel((Integer)((View)view.getParent()).getTag());
				}
			});
	    	
			return convertView;
		}
	}
	
	private class BookingListTask extends AsyncTask<Void, Void, Boolean> {

    	private ArrayList<BookingItem> results;
    	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Simulate network access.
				Thread.sleep(0);
			} catch (InterruptedException e) {
				return false;
			}
			
	    	results = new ArrayList<BookingItem>();
	    	
        	String s = ServerUtils.submitRequest("getBookingList", "userID="+UserData.getInstance().getUserId());
        	
	        try{
		        JSONArray jsa=new JSONArray(s);
	            for (int i=0;i<jsa.length();i++) {
               		JSONObject jo=(JSONObject)jsa.get(i);
               		BookingItem item = new BookingItem();
               		item.bookingId = jo.getInt("booking_id");
               		item.restaurantName = jo.getString("SS");
               		item.noOfParticipants = jo.getInt("no_of_participants");
               		item.bookingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jo.getString("booking_ts"));
               		item.status = jo.getInt("status");
           			results.add(item);
		        }
	        }
	        catch(Exception e){
	        	Log.d("exception", e.getMessage());
	        }
			
			return true;
		}
		

		@Override
		protected void onPostExecute(final Boolean success) {
			mBookingListTask = null;
			if (success) {
				mAdapter.clear();
				mAdapter.addAll(results);
				mAdapter.notifyDataSetChanged();
				mBookingListView.setVisibility(View.VISIBLE);
				mBookingListView.setSelection(0);
				mProgressBar.setVisibility(View.GONE);
			} else {
			}
		}
		
	}
	
	private class BookingUpdateTask extends AsyncTask<Void, Void, Boolean> {
		
		int mBookingId;
		int mStatus;
		
		public BookingUpdateTask(int bookingId, int status) {
			mBookingId = bookingId;
			mStatus = status;
		};
		
		@Override
		protected Boolean doInBackground(Void... params) {
        	ServerUtils.submitRequest("updateBooking", "status="+mStatus, "bid="+mBookingId);
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			mBookingUpdateTask = null;
			if (success) {
				loadBookings();
			} else {
			}
		}
	}
	
	public class FbLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		String mEmail;
		String mFbId;
		String mUsername;
		String mFirstName;
		String mLastName;
		String mToken;
		String mExpireTs;
		
		public FbLoginTask(String email, String fbId, String username, String firstName, String lastName, String token, String expireTs) {
			mEmail = email;
			mFbId = fbId;
			mUsername = username;
			mFirstName = firstName;
			mLastName = lastName;
			mToken = token;
			mExpireTs = expireTs;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(0);
			} catch (InterruptedException e) {
				return false;
			}
			
        	String jsonString = ServerUtils.submitRequest("fbLogin", "snId=1", "userId="+UserData.getInstance().getUserId(), "email="+mEmail, "snUserId="+mFbId, "username="+mUsername, "firstName="+mFirstName, "lastName="+mLastName, "token="+mToken, "expireTs="+mExpireTs);
        	JSONObject json;
			try {
				json = new JSONObject(jsonString);
				if (json.getBoolean("result")) {
					return true;
				}
			} catch (JSONException e) {
			}
			
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mFbLoginTask = null;

			if (!success) {
				mFbLoginButton.setVisibility(View.VISIBLE);
				Session.getActiveSession().closeAndClearTokenInformation();
				UserData.getInstance().clearFbData();
				Toast.makeText(getApplicationContext(), "Login with Facebook failed", Toast.LENGTH_LONG).show();
			}
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			mFbLoginTask = null;
			showProgress(false);
		}
	}
}
