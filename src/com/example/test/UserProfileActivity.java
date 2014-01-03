package com.example.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.*;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class UserProfileActivity extends ActionBarActivity {

	private RadioGroup mTab;
	private ListView mBookingListView;
	private ProgressBar mProgressBar;
	private TextView mEmptyTextView;
	
	private ArrayAdapter<BookingItem> mAdapter;
	
	private BookingListTask mBookingListTask = null;
	private BookingUpdateTask mBookingUpdateTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mEmptyTextView = (TextView) findViewById(R.id.emptyTextView);
		
		TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		usernameTextView.setText(UserData.getInstance().getFullName());
		
		mAdapter = new BookingListAdapter<BookingItem>(this, R.id.bookingsListView);
		mBookingListView = (ListView) findViewById(R.id.bookingsListView);
		mBookingListView.setAdapter(mAdapter);
		
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
		mTab.check(R.id.bookingsRadioButton);
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
	    	restaurantNameTextView.setText(item.restaurantName);
	    	bookingInfoTextView.setText(Utils.getLongBookingInfo(item.bookingDate, item.noOfParticipants));
	    	
	    	convertView.setTag(item.bookingId);
	    	
	    	Button cancelButton = (Button) convertView.findViewById(R.id.cancelButton);
	    	Button confirmButton = (Button) convertView.findViewById(R.id.confirmButton);
	    	
	    	cancelButton.setVisibility(item.status==0?View.VISIBLE:View.GONE);
	    	confirmButton.setVisibility(item.status==0?View.VISIBLE:View.GONE);
	    	
	    	cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					cancel((Integer)((View)view.getParent()).getTag());
				}
			});
	    	confirmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					confirm((Integer)((View)view.getParent()).getTag());
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
				Thread.sleep(400);
			} catch (InterruptedException e) {
				return false;
			}
			
	    	results = new ArrayList<BookingItem>();
	    	String url = "http://10.0.2.2:8888/reservations?userID="+UserData.getInstance().getUserId();
        	String s = Utils.getJsonString(url);
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
			HashMap<String, String> postParams = new HashMap<String, String>();
			postParams.put("status", Integer.toString(mStatus));
			String url = "http://10.0.2.2:8888/reservations/"+mBookingId;
			Utils.put(url, postParams);
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
}
