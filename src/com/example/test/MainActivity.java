package com.example.test;

import java.util.Date;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import android.util.Log;

import java.net.*;

import org.json.*;

import android.support.v7.app.*;

import com.example.test.PhotoView;
import com.example.test.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

import android.view.*;

public class MainActivity extends ActionBarActivity {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	private Button mShowSearchButton;
	private View mSearchView;
	private Button mBookingPickerButton;
	private BookingPicker mBookingPicker;
	private EditText mKeywordEditText;
	private ListView mListView;
	private LinearLayout mListViewFooter;
	
	private ListViewAdapter<RestaurantResultItem> mAdapter;

	private SearchRestaurantTask mSearchRestaurantTask = null;
	private int mListViewVisibleThreshold = 0;
    private boolean mLoading;
    private boolean mMoreToLoad;
	
	private int mPage;
	private String mKeyword;
	
	private final int mPageSize = 100;
	
	private boolean mIsListView;
	
	private int mDistance = 5;
	private LinearLayout mGoogleMapContainer;
	private GoogleMap mGoogleMap;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private LocationListener mLocationListener;
	private Location mTargetLocation;
	private Location mCurrentLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mIsListView = true;
        
        mShowSearchButton = (Button) findViewById(R.id.showSearchButton);
        mSearchView = findViewById(R.id.searchView);
        mBookingPickerButton = (Button) findViewById(R.id.bookingPickerButton);
        mBookingPicker = (BookingPicker) findViewById(R.id.bookingPicker);
        mKeywordEditText = (EditText) findViewById(R.id.keywordEditText);
        mGoogleMapContainer= (LinearLayout) findViewById(R.id.googleMapContainer);
        
        mBookingPicker.setOnValueChangeListener(new BookingPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(int numOfPeople, Date date) {
				SearchData.getInstance().setNumberOfReservation(numOfPeople);
				SearchData.getInstance().setSearchDate(date);
				mBookingPickerButton.setText(Utils.getLongBookingInfo());
			}
		});
        mShowSearchButton.setText(Utils.getShortBookingInfo());

        ProgressBar progressFooter = new ProgressBar(this, null, android.R.attr.progressBarStyle);
		LinearLayout.LayoutParams footerParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressFooter.setLayoutParams(footerParam);
        mListViewFooter = new LinearLayout(this);
		mListViewFooter.setGravity(Gravity.CENTER);
		mListViewFooter.addView(progressFooter);

		mAdapter = new ListViewAdapter<RestaurantResultItem>(this, R.layout.restaurant_search_result_tablerow);
		mListView = (ListView)this.findViewById(R.id.searchResultListView);
		mListView.addFooterView(mListViewFooter);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	            if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mListViewVisibleThreshold) && mAdapter.getCount() > 0 && mMoreToLoad) {
	            	search(false);
	            }				
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
		
		try {
			initLocationServices();
		} catch (Exception e) {
		}
    }
    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }
    @Override
    protected void onStop() {
    	if (mLocationClient.isConnected()) {
    		mLocationClient.removeLocationUpdates(mLocationListener);
        }
        mLocationClient.disconnect();
        super.onStop();
    }
    private void initLocationServices() {
    	mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    	mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				Log.v("MainActivity", "Location Changed: "+mCurrentLocation+" to "+location);
				mCurrentLocation = location;
				mTargetLocation = mCurrentLocation; 
			}
		};
    	
    	mLocationClient = new LocationClient(this, new ConnectionCallbacks() {
			@Override
			public void onDisconnected() {
			}
			@Override
			public void onConnected(Bundle connectionHint) {
				mLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener);
				search(true);
			}
		}, new OnConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult connectionResult) {
				/*
		         * Google Play services can resolve some errors it detects.
		         * If the error has a resolution, try sending an Intent to
		         * start a Google Play services activity that can resolve
		         * error.
		         */
		        if (connectionResult.hasResolution()) {
		            try {
		                // Start an Activity that tries to resolve the error
		                connectionResult.startResolutionForResult(MainActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
		                /*
		                 * Thrown if Google Play services canceled the original
		                 * PendingIntent
		                 */
		            } catch (IntentSender.SendIntentException e) {
		                // Log the error
		                e.printStackTrace();
		            }
		        }
			}
		});
		mCurrentLocation = mLocationClient.getLastLocation();
		mTargetLocation = mCurrentLocation;
		
		mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
    	mGoogleMap.setBuildingsEnabled(true);
    	mGoogleMap.setMyLocationEnabled(true);
    	mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				Log.v("MainActivity", "onMapLoaded");
				Location lastLocation = mLocationClient.getLastLocation();
				mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
			}
		});
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // same as using a normal menu
        switch(item.getItemId()) {
        case R.id.action_user:
        	UserManager.getInstance(this).login(false);
            break;
        case R.id.action_switch_view:
        	switchView();
        	break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, R.id.action_switch_view, Menu.NONE, "");
        item.setIcon(mIsListView?R.drawable.ic_action_locate:R.drawable.ic_action_paste);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	if(intent.hasExtra("source") && intent.getStringExtra("source").equals("notification")
    			&& intent.hasExtra("notificationType") && intent.getStringExtra("notificationType").equals("bookingNotification")
    			&& intent.hasExtra("bookingID") && intent.getStringExtra("bookingID") != null
    	){
	    	String bookingID = intent.getStringExtra("bookingID");
		    Log.d("onNewIntent- bookingID", bookingID);
	    	UserManager.getInstance(this).showBookings(bookingID);
    	}else{
    		Log.d("onNewIntent", "no intent");
    	}
    	if(intent.hasExtra("ACTION")){
	        String action = intent.getStringExtra("ACTION");
	    	if (action.equals(Constants.ACTION_LOGIN_SUCCESS)) {
	            Log.d("example", "login success and show profile now");
	    		UserManager.getInstance(this).showProfile();
	    	}
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		moveTaskToBack(true);
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

    
    public void switchView() {
    	mIsListView = !mIsListView;
    	/*if (!mIsListView) {
    		AnimatorSet flipRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);
    		AnimatorSet flipRightIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_in);
    		flipRightOut.setTarget(mListView);
//    		flipRightIn.setTarget(mGoogleMapContainer);
    		flipRightOut.start();
//    		flipRightIn.start();
    		mGoogleMapContainer.setVisibility(View.VISIBLE);
    	} else {
    		AnimatorSet flipLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_out);
    		AnimatorSet flipLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_in);
//    		flipLeftOut.setTarget(mGoogleMapContainer);
    		flipLeftIn.setTarget(mListView);
//    		flipLeftOut.start();
    		flipLeftIn.start();
    		mGoogleMapContainer.setVisibility(View.GONE);
    	}*/
		mListView.setVisibility(mIsListView?View.VISIBLE:View.GONE);
		mGoogleMapContainer.setVisibility(mIsListView?View.GONE:View.VISIBLE);
    	invalidateOptionsMenu();
    }
    
    private class RestaurantResultItem {
    	public String licno;
    	public String type;
    	public String dist;
    	public String ss;
    	public String adr;
    	public int rating;
    	public String img;
    	public LatLng latlng;
    }
    
    private class ListViewAdapter<T> extends ArrayAdapter {
		public ListViewAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(this.getContext());
				convertView = inflater.inflate(R.layout.restaurant_search_result_tablerow, parent, false);
			}
			
			TextView nameTextView = (TextView) convertView.findViewById(R.id.restaurantResult_nameTextView);
			TextView addressTextView = (TextView) convertView.findViewById(R.id.restaurantResult_addressTextView);
	    	TextView slotTextView = (TextView) convertView.findViewById(R.id.restaurantResult_slotTextView);
	    	
	    	TextView licnoTextView = (TextView) convertView.findViewById(R.id.restaurantResult_licnoTextView);
	    	
	    	RestaurantResultItem item = (RestaurantResultItem)this.getItem(position);
	    	nameTextView.setText(item.ss);
	    	addressTextView.setText(item.adr);
	    	convertView.setTag(item.licno);
	    	
	    	licnoTextView.setText(item.licno);
	    	
	    	PhotoView pv = ((PhotoView) convertView.findViewById(R.id.restaurantResult_thumbnailImageView));
	    	try{
	    		URL req = new URL(item.img);
	    		pv.setImageURL(req, true, null);
	    	}
	    	catch(MalformedURLException mfe){}
	    	
	    	convertView.setOnClickListener((new View.OnClickListener(){
	    		@Override
	    		public void onClick(View v){
	    			RestaurantManager.getInstance(MainActivity.this).showMain((String)v.getTag());
	    		}
	    	}));
			return convertView;
		}
    }
    
    
    
    public void showSearchButton_onClick(View view) {
    	mSearchView.setVisibility(View.VISIBLE);
    	mShowSearchButton.setVisibility(View.INVISIBLE);
    	mBookingPicker.setVisibility(View.GONE);
    	mKeywordEditText.setText("");
    	mSearchView.bringToFront();
    	mKeywordEditText.requestFocus();
    }
    public void searchButton_onClick(View view) {
    	mSearchView.setVisibility(View.GONE);
    	mShowSearchButton.setVisibility(View.VISIBLE);
    	mShowSearchButton.setText(Utils.getShortBookingInfo());
    	mKeyword = mKeywordEditText.getText().toString();
    	search(true);
    }
    public void cancelButton_onClick(View view) {
    	mSearchView.setVisibility(View.GONE);
    	mShowSearchButton.setVisibility(View.VISIBLE);
    }
    public void bookingPickerButton_onClick(View view) {
    	mBookingPicker.setVisibility(mBookingPicker.isShown()?View.GONE:View.VISIBLE);
    }
    
    
    public void search(Boolean cleanup) {
    	if (mListView.getFooterViewsCount() == 0) {
    		mListView.addFooterView(mListViewFooter);
    	}
    	if (mSearchRestaurantTask != null) {
    		mSearchRestaurantTask.cancel(true);
    	}
    	if (cleanup) {
    		mPage = 0;
    		mMoreToLoad = true;
//    		if (mGoogleMap != null) {
//    			mGoogleMap.clear();
//    		}
        	mAdapter.clear();
        	mAdapter.notifyDataSetChanged();
    	} else {
    		mPage++;
    	}
    	mLoading = true;
    	mSearchRestaurantTask = new SearchRestaurantTask();
    	mSearchRestaurantTask.execute((Void) null);
    }
    
    private class SearchRestaurantTask extends AsyncTask<Void, Void, Boolean> {

    	private ArrayList<RestaurantResultItem> results;
    	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Simulate network access.
				Thread.sleep(0);
			} catch (InterruptedException e) {
				return false;
			}
			
	    	results = new ArrayList<RestaurantResultItem>();

	    	String s = "";
	    	if (mGoogleMap != null) {
	    		s = ServerUtils.submitRequest("getRestaurantList", "p="+mPage, "k="+mKeyword, "rpp="+mPageSize, "du=km", "dt="+mDistance, "lat="+mTargetLocation.getLatitude(), "lng="+mTargetLocation.getLongitude());
	    	} else {
	    		s = ServerUtils.submitRequest("getRestaurantList", "p="+mPage, "k="+mKeyword, "rpp="+mPageSize);
	    	}
        	Log.d("com.example.test", "Result: "+s);
	        try{
		        JSONArray jsa=new JSONArray(s);
		        if (jsa.length() == 0 || jsa.length() < mPageSize) {
		        	mMoreToLoad = false;
		        }
	            for(int i=0;i<jsa.length();i++) {
	               		JSONObject jo=(JSONObject)jsa.get(i);
	               		RestaurantResultItem item = new RestaurantResultItem();
	               		item.licno = jo.getString("LICNO");
	               		item.type = jo.getString("TYPE");
	           			item.dist = jo.getString("DIST");
	           			item.ss = jo.getString("SS");
	           			item.adr = jo.getString("ADR");
	           			item.img = jo.getString("IMAGE");
	           			item.rating = 1;
	           			item.latlng = new LatLng( jo.getDouble("lat_float"), jo.getDouble("lng_float"));
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
			mSearchRestaurantTask = null;
			mLoading = false;
			mListView.removeFooterView(mListViewFooter);
			if (success) {
				if (mGoogleMap != null) {
					for (RestaurantResultItem item : results) {
						MarkerOptions marker = new MarkerOptions().position(item.latlng).icon(BitmapDescriptorFactory.defaultMarker());
						mGoogleMap.addMarker(marker);
					}
				}
				mAdapter.addAll(results);
				mAdapter.notifyDataSetChanged();
			} else {
			}
		}
		
	}
    
}
