package com.ikky.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.util.Log;
import android.util.TypedValue;

import java.net.*;

import org.json.*;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.ikky.activities.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ikky.base.BaseActivity;
import com.ikky.constants.Constants;
import com.ikky.helpers.ServerUtils;
import com.ikky.helpers.Utils;
import com.ikky.managers.RestaurantManager;
import com.ikky.managers.SearchData;
import com.ikky.managers.UserData;
import com.ikky.managers.UserManager;
import com.ikky.ui.BookingPicker;
import com.ikky.ui.PhotoView;

import java.util.*;

import android.view.*;

public class MainActivity extends BaseActivity {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private View mSearchView;
	private Button mBookingPickerButton;
	private BookingPicker mBookingPicker;
	private EditText mKeywordEditText;
	private ListView mListView;
	private LinearLayout mListViewFooter;
	private TextView mDistanceValueText;
	private SeekBar mDistanceSeekBar;
	private ImageButton mSwitchViewButton;
	private Boolean mMapLoaded;
	
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	
	private RelativeLayout mMiniInfoLoading;
	
	private ListViewAdapter<RestaurantResultItem> mAdapter;
	private MiniInfoAdapter mMiniInfoAdapter;
	private ArrayList<Marker> mResultMarkers;

	private SearchRestaurantTask mSearchRestaurantTask = null;
	private int mListViewVisibleThreshold = 0;
    private boolean mLoading;
    private boolean mMoreToLoad;
    private boolean mInitSearch;
	
	private int mPage;
	private String mKeyword;
	
	private final int mPageSize = 100;
	
	private boolean mIsListView;
	private boolean mCanSwitchView;
	
	private float mDistance;
	private RelativeLayout mGoogleMapContainer;
	private GoogleMap mGoogleMap;
	private LocationClient mLocationClient;
	private Location mTargetLocation;
	private double mMinLat;
	private double mMinLng;
	private double mMaxLat;
	private double mMaxLng;
	private Marker mSelectedMarker;
	
	private ViewPager mViewPager;
	
	private int mPrevNumOfPeople;
	private Date mPrevSearchDate;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater inflater = LayoutInflater.from(this.getBaseContext());
		View actionBarView = inflater.inflate(R.layout.actionbar_view, null);
        
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(actionBarView);
        
        mInitSearch = true;
        mIsListView = true;
        mCanSwitchView = true;
        mMapLoaded = false;
        
        mSearchView = findViewById(R.id.searchView);
        mBookingPickerButton = (Button) getActionBar().getCustomView().findViewById(R.id.actionBarPicker);
        mBookingPickerButton.setTypeface(mTypefaceRobotoRegular);
        mBookingPickerButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSearchButton_onClick(v);
			}
		});
        mArrowUp = (ImageView) findViewById(R.id.arrowUp);
        mArrowDown = (ImageView) findViewById(R.id.arrowDown);
        final Activity act = this;
        getActionBar().getCustomView().findViewById(R.id.actionBarUser).setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		UserManager.getInstance(act).login(false);
        		cancelButton_onClick(v);
        	}
        });
        mBookingPicker = (BookingPicker) findViewById(R.id.bookingPicker);
        mKeywordEditText = (EditText) findViewById(R.id.keywordEditText);
        mDistanceValueText = (TextView) findViewById(R.id.distanceValueText);
        mDistanceValueText.setTypeface(mTypefaceRobotoRegular);
        mDistanceSeekBar = (SeekBar) findViewById(R.id.distanceSeekBar);
        mGoogleMapContainer= (RelativeLayout) findViewById(R.id.googleMapContainer);
        mSwitchViewButton = (ImageButton) getActionBar().getCustomView().findViewById(R.id.actionBarMap);
        mSwitchViewButton.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		switchView(v);
        		cancelButton_onClick(v);
        	}
        });
        
        mMiniInfoLoading = (RelativeLayout) findViewById(R.id.miniInfoLoading);
        
        mBookingPicker.setOnValueChangeListener(new BookingPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(int numOfPeople, Date date) {
				SearchData.getInstance().setNumberOfReservation(numOfPeople);
				SearchData.getInstance().setSearchDate(date);
				mBookingPickerButton.setText(Utils.getLongBookingInfo());
			}
		});
        mPrevNumOfPeople = mBookingPicker.getNoOfParticipants();
        mPrevSearchDate = mBookingPicker.getDate();
        
        mDistanceSeekBar.setMax(9);
        mDistanceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDistance = (progress + 1) / 10.0f;
				mDistanceValueText.setText(((progress + 1) * 100) + "m");
			}
		});
        mDistanceSeekBar.setProgress(2);
        mDistance = 0.3f;
		mDistanceValueText.setText("300m");

        ProgressBar progressFooter = new ProgressBar(this, null, android.R.attr.progressBarStyle);
		LinearLayout.LayoutParams footerParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressFooter.setLayoutParams(footerParam);
        mListViewFooter = new LinearLayout(this);
		mListViewFooter.setGravity(Gravity.CENTER);
		mListViewFooter.addView(progressFooter);
		
		mResultMarkers = new ArrayList<Marker>();

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
		
		mMiniInfoAdapter = new MiniInfoAdapter();
    	mViewPager = (ViewPager) findViewById(R.id.miniInfoPager);
    	mViewPager.setOffscreenPageLimit(5);
		mViewPager.setAdapter(mMiniInfoAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Marker marker = mResultMarkers.get(position);
				highlightMarker(marker);
				moveToMarker(marker);
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
		try {
			initMap();
		} catch (Exception e) {
		}
    }
    @Override
	public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }
    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }
    private void initMap() {
    	mLocationClient = new LocationClient(this, new ConnectionCallbacks() {
			@Override
			public void onDisconnected() {
			}
			@Override
			public void onConnected(Bundle connectionHint) {
				if (mInitSearch) {
					mInitSearch = false;
					search(true);
				}
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
		
    	float scale = getBaseContext().getResources().getDisplayMetrics().density;
		mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
		mGoogleMap.setPadding(0, 0, 0, (int)(125*scale));
    	mGoogleMap.setBuildingsEnabled(true);
    	mGoogleMap.setMyLocationEnabled(true);
    	mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				mMapLoaded = true;
				Location lastLocation = mLocationClient.getLastLocation();
				mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));
			}
		});
    	mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				highlightMarker(marker);
				mViewPager.setCurrentItem(mResultMarkers.indexOf(marker));
				return true;
			}
		});
    }
    private void highlightMarker(Marker marker) {
		if (mSelectedMarker != null) {
			mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue));
		}
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red));
		mSelectedMarker = marker;
    }
    private void moveToMarker(Marker marker) {
    	LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
    	LatLng markerLatLng = marker.getPosition();
    	if (!bounds.contains(markerLatLng)) {
			mGoogleMap.stopAnimation();
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(markerLatLng.latitude, markerLatLng.longitude), mGoogleMap.getCameraPosition().zoom));
    	}
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
	    	if (action.equals(Constants.ACTION_ALREADY_LOGIN)) {
	            Log.d("example", "login success and show profile now");
	    		UserManager.getInstance(this).showProfile();
	    	}
    	}
    }
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	if (keyCode == KeyEvent.KEYCODE_BACK) {
//    		moveTaskToBack(true);
//    		return true;
//    	}
//    	return super.onKeyDown(keyCode, event);
//    }
    
    
    public void switchView(View view) {
    	if (!mCanSwitchView) {
    		return;
    	}
    	mCanSwitchView = false;
    	mIsListView = !mIsListView;
    	final float scale = getBaseContext().getResources().getDisplayMetrics().density;
    	if (!mIsListView) {
    		mSwitchViewButton.setImageResource(R.drawable.ic_launcher_list_view);
    		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSwitchViewButton.getLayoutParams();
    		params.width = (int) (21.5*scale + 0.5F);
    		params.height = (int) (18*scale + 0.5F);
    		mSwitchViewButton.setLayoutParams(params);
    		AnimatorSet flipRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);
    		AnimatorSet flipRightIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_in);
    		flipRightOut.setTarget(mListView);
    		flipRightIn.setTarget(mGoogleMapContainer);
    		mGoogleMapContainer.setVisibility(View.VISIBLE);
    		flipRightOut.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
				@Override
				public void onAnimationEnd(Animator animation) {
					mListView.setVisibility(View.GONE);
					mCanSwitchView = true;
				}
				@Override
				public void onAnimationCancel(Animator arg0) {
				}
			});
    		flipRightOut.start();
    		flipRightIn.start();
    	} else {
    		mSwitchViewButton.setImageResource(R.drawable.ic_launcher_map);
    		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSwitchViewButton.getLayoutParams();
    		params.width = (int) (26*scale + 0.5F);
    		params.height = (int) (26*scale + 0.5F);
    		mSwitchViewButton.setLayoutParams(params);
    		AnimatorSet flipLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_out);
    		AnimatorSet flipLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_in);
    		flipLeftOut.setTarget(mGoogleMapContainer);
    		flipLeftIn.setTarget(mListView);
    		mListView.setVisibility(View.VISIBLE);
    		flipLeftOut.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
				@Override
				public void onAnimationEnd(Animator animation) {
					mGoogleMapContainer.setVisibility(View.GONE);
					mCanSwitchView = true;
				}
				@Override
				public void onAnimationCancel(Animator arg0) {
				}
			});
    		flipLeftOut.start();
    		flipLeftIn.start();
    	}
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
    	public ArrayList<String> timeslots;
    	public ArrayList<String> vipTimeslots;
    	public String cuisine;
    }
    
    private class ListViewAdapter<T> extends ArrayAdapter<T> {
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
			nameTextView.setTypeface(mTypefaceRobotoRegular);
			TextView addressTextView = (TextView) convertView.findViewById(R.id.restaurantResult_addressTextView);
	    	addressTextView.setTypeface(mTypefaceRobotoRegular);
	    	
	    	TextView cuisineTextView = (TextView) convertView.findViewById(R.id.restaurantResult_cusine);
	    	cuisineTextView.setTypeface(mTypefaceRobotoRegular);
	    	
	    	RestaurantResultItem item = (RestaurantResultItem)this.getItem(position);
	    	nameTextView.setText(item.ss);
	    	addressTextView.setText(item.adr);
	    	cuisineTextView.setText(item.cuisine);
	    	
	    	final float scale = getContext().getResources().getDisplayMetrics().density;
	    	int widthInDp = (int) (50 * scale + 0.5f);
	    	int heightInDp = (int) (17 * scale + 0.5f);
	    	Iterator<String> iter = item.timeslots.iterator();
	    	LinearLayout timeslotsContainer = (LinearLayout) convertView.findViewById(R.id.restaurantResult_timeslotsContainer);
	    	timeslotsContainer.removeAllViews();
	    	for (int i=0; i<4; i++) {
	    		RelativeLayout timeslotWrap = new RelativeLayout(getContext());
	    		timeslotWrap.setLayoutParams(new LayoutParams(widthInDp, heightInDp));
	    		TextView timeslot = new TextView(getContext());
	    		timeslot.setWidth(widthInDp);
	    		timeslot.setHeight(heightInDp);
	    		timeslot.setGravity(Gravity.CENTER);
	    		timeslot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
	    		timeslot.setTextColor(Color.WHITE);
	    		String viewString = "-";
	    		Boolean isVip = false;
	    		if (iter.hasNext()) {
	    			timeslot.setBackgroundColor(Color.argb(255, 14, 180, 88));
	    			String timeslotString = iter.next();
	    			Log.d("ServerUtils", "vipTimeslots:"+item.vipTimeslots.toString());
	    			if (item.vipTimeslots.contains(timeslotString)) {
	    				isVip = true;
	    			}
	    			viewString = timeslotString.substring(0, 2) + ":" + timeslotString.substring(2);
	    		} else {
	    			timeslot.setBackgroundColor(Color.argb(255, 214, 214, 214));
	    		}
	    		timeslot.setText(viewString);
	    		timeslotWrap.addView(timeslot);
	    		if (isVip) {
	    			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (11.5*scale+0.5f), (int) (12*scale+0.5f));
	    			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    			params.setMargins(0, (int)(-5*scale-0.5f), (int)(-5*scale-0.5f), 0);
	    			ImageView vipStar = new ImageView(getContext());
    				vipStar.setLayoutParams(params);
    				vipStar.setImageResource(R.drawable.ic_listing_star);
    				timeslotWrap.addView(vipStar);
	    		}
	    		timeslotsContainer.addView(timeslotWrap);
	    		
	    		if (i<3) {
	    			LinearLayout paddingView = new LinearLayout(getContext());
	    			paddingView.setOrientation(LinearLayout.HORIZONTAL);
	    			LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
	    			params.weight = 1;
	    			paddingView.setLayoutParams(params);
	    			timeslotsContainer.addView(paddingView);
	    		}
	    	}
	    	convertView.setTag(item.licno);
	    	
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
    	if (mSearchView.getVisibility()==View.VISIBLE) {
    		cancelButton_onClick(view);
    	} else {
	    	mSearchView.setVisibility(View.VISIBLE);
	    	mKeywordEditText.setText("");
	    	mSearchView.bringToFront();
	    	mKeywordEditText.requestFocus();
	    	mArrowUp.setVisibility(View.VISIBLE);
	    	mArrowDown.setVisibility(View.GONE);
    	}
    }
    public void searchButton_onClick(View view) {
    	mArrowUp.setVisibility(View.GONE);
    	mArrowDown.setVisibility(View.VISIBLE);
    	mSearchView.setVisibility(View.GONE);
    	mKeyword = mKeywordEditText.getText().toString();
    	mPrevNumOfPeople = SearchData.getInstance().getNumberOfReservation();
    	mPrevSearchDate = SearchData.getInstance().getSearchDate();
    	search(true);
    }
    public void cancelButton_onClick(View view) {
    	SearchData.getInstance().setSearchDate(mPrevSearchDate);
    	SearchData.getInstance().setNumberOfReservation(mPrevNumOfPeople);
    	mBookingPickerButton.setText(Utils.getLongBookingInfo());
    	hideSoftKeyboard();
    	mArrowUp.setVisibility(View.GONE);
    	mArrowDown.setVisibility(View.VISIBLE);
    	mSearchView.setVisibility(View.GONE);
    }
    public void bookingPickerButton_onClick(View view) {
    	mBookingPicker.setVisibility(mBookingPicker.isShown()?View.GONE:View.VISIBLE);
    }
    
    public void searchHere(View view) {
    	LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
    	mMinLat = bounds.northeast.latitude<bounds.southwest.latitude?bounds.northeast.latitude:bounds.southwest.latitude;
    	mMaxLat = bounds.northeast.latitude>bounds.southwest.latitude?bounds.northeast.latitude:bounds.southwest.latitude;
    	mMinLng = bounds.northeast.longitude<bounds.southwest.longitude?bounds.northeast.longitude:bounds.southwest.longitude;
    	mMaxLng = bounds.northeast.longitude>bounds.southwest.longitude?bounds.northeast.longitude:bounds.southwest.longitude;
    	search(true, true);
    }

    public void search(Boolean cleanup) {
    	search(cleanup, false);
    }
    
    public void search(Boolean cleanup, Boolean visibleArea) {
    	mTargetLocation = mLocationClient.getLastLocation();
    	if (mListView.getFooterViewsCount() == 0) {
    		mListView.addFooterView(mListViewFooter);
    	}
    	if (mSearchRestaurantTask != null) {
    		mSearchRestaurantTask.cancel(true);
    	}
    	if (cleanup) {
    		mPage = 0;
    		mMoreToLoad = true;
    		if (mGoogleMap != null) {
    			mGoogleMap.clear();
    		}
        	mAdapter.clear();
        	mSelectedMarker = null;
        	mResultMarkers.clear();
        	mAdapter.notifyDataSetChanged();
        	mMiniInfoAdapter.notifyDataSetChanged();
        	mMiniInfoLoading.setVisibility(View.VISIBLE);
    	} else {
    		mPage++;
    	}
    	mLoading = true;
    	mSearchRestaurantTask = new SearchRestaurantTask(visibleArea);
    	mSearchRestaurantTask.execute((Void) null);
    }
    
    private class SearchRestaurantTask extends AsyncTask<Void, Void, Boolean> {

    	private ArrayList<RestaurantResultItem> results;
    	private Boolean mVisibleArea;
    	
    	public SearchRestaurantTask(Boolean visibleArea) {
    		mVisibleArea = visibleArea;
    	}
    	
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
	    		String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(SearchData.getInstance().getChosenDate());
	    		if (mVisibleArea) {
	    			s = ServerUtils.submitRequest("getRestaurantList", "p="+mPage, "k="+mKeyword, "rpp="+mPageSize, "latmin="+mMinLat, "lngmin="+mMinLng, "latmax="+mMaxLat, "lngmax="+mMaxLng, "booking_datetime="+datetime, "user_id="+UserData.getInstance().getUserId(), "no_of_participants="+SearchData.getInstance().getNumberOfReservation());
	    		} else {
	    			s = ServerUtils.submitRequest("getRestaurantList", "p="+mPage, "k="+mKeyword, "rpp="+mPageSize, "du=km", "dt="+mDistance, "lat="+mTargetLocation.getLatitude(), "lng="+mTargetLocation.getLongitude(), "booking_datetime="+datetime, "user_id="+UserData.getInstance().getUserId(), "no_of_participants="+SearchData.getInstance().getNumberOfReservation());
	    		}
	    	} else {
	    		s = ServerUtils.submitRequest("getRestaurantList", "p="+mPage, "k="+mKeyword, "rpp="+mPageSize);
	    	}
        	Log.d("com.example.test", "Result: "+s);
	        try{
		        JSONArray restaurants = new JSONArray(s);
		        if (restaurants.length() == 0 || restaurants.length() < mPageSize) {
		        	mMoreToLoad = false;
		        }
	            for(int i=0;i<restaurants.length();i++) {
	               		JSONObject jo = restaurants.getJSONObject(i);
	               		Boolean isVIP = jo.getString("is_vip").compareTo("0")==0?false:true;
	               		RestaurantResultItem item = new RestaurantResultItem();
	               		item.licno = jo.getString("LICNO");
	               		item.type = jo.getString("TYPE");
	           			item.dist = jo.getString("DIST");
	           			item.ss = jo.getString("SS");
	           			item.adr = jo.getString("ADR");
	           			item.img = jo.getString("IMAGE");
	           			item.cuisine = jo.getString("cuisine");
	           			ArrayList<String> timeslots = new ArrayList<String>();
	           			ArrayList<String> vipTimeslots = new ArrayList<String>();
	           			
	           			if (jo.has("timeslotAvailability") || jo.has("VIPTimeslotAvailability")) {
	           				JSONArray timeslotAvailability = jo.getJSONArray("timeslotAvailability");
	           				JSONArray vipTimeslotAvailability = jo.getJSONArray("VIPTimeslotAvailability");
	           				for (int j=0; j<timeslotAvailability.length(); j++) {
	           					String timeslotStr = timeslotAvailability.getString(j);
           						timeslots.add(timeslotStr);
	           				}
	           				
	           				if (isVIP) {
		           				for (int j=0; j<vipTimeslotAvailability.length(); j++) {
		           					String timeslotStr = vipTimeslotAvailability.getString(j);
		           					if (!timeslots.contains(timeslotStr)) {
		           						timeslots.add(timeslotStr);
		           					}
	           						vipTimeslots.add(timeslotStr);
		           				}
	           				}
	           				Collections.sort(timeslots);
	           				item.vipTimeslots = vipTimeslots;
	           				
	           				if (timeslots.size() > 4) {
	           					ArrayList<String> targetTimeslots = new ArrayList<String>();
	           					String targetTimeslotStr = new SimpleDateFormat("HHmm").format(SearchData.getInstance().getChosenDate());
	           					int largerTimeslotCount = 0;
	           					for (String timeslotStr : timeslots) {
	           						if (timeslotStr.compareTo(targetTimeslotStr) > 0) {
	           							largerTimeslotCount++;
	           						}
	           						targetTimeslots.add(timeslotStr);
	           						if (targetTimeslots.size() > 4) {
	           							targetTimeslots.remove(0);
	           						}
	           						if (largerTimeslotCount == 2 && targetTimeslots.size() == 4) {
	           							break;
	           						}
	           					}
	           					timeslots = targetTimeslots;
	           				}
	           				
	           			}
	           			item.timeslots = timeslots;
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
						MarkerOptions markerOptions = new MarkerOptions().position(item.latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue));
						Marker marker = mGoogleMap.addMarker(markerOptions);
						mResultMarkers.add(marker);
					}
				}
				mAdapter.addAll(results);
				mAdapter.notifyDataSetChanged();
				mMiniInfoAdapter.notifyDataSetChanged();
				mMiniInfoLoading.setVisibility(View.GONE);
				if (results.size() > 0) {
					mViewPager.setCurrentItem(0);
					highlightMarker(mResultMarkers.get(0));
					if (!mVisibleArea) {
						moveToMarker(mResultMarkers.get(0));
					}
				}
			} else {
			}
		}
		
	}
    
    private class MiniInfoAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return mAdapter.getCount();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (View) object;
		}
    	
		@Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View viewLayout = inflater.inflate(R.layout.restaurant_search_result_tablerow, container, false);
	        viewLayout.setBackgroundColor(Color.argb(217, 255, 255, 255));
			
			TextView nameTextView = (TextView) viewLayout.findViewById(R.id.restaurantResult_nameTextView);
			TextView addressTextView = (TextView) viewLayout.findViewById(R.id.restaurantResult_addressTextView);
	    	
	    	RestaurantResultItem item = mAdapter.getItem(position);
	    	nameTextView.setText(item.ss);
	    	addressTextView.setText(item.adr);
	    	final float scale = getBaseContext().getResources().getDisplayMetrics().density;
	    	int widthInDp = (int) (50 * scale + 0.5f);
	    	int heightInDp = (int) (17 * scale + 0.5f);
	    	Iterator<String> iter = item.timeslots.iterator();
	    	LinearLayout timeslotsContainer = (LinearLayout) viewLayout.findViewById(R.id.restaurantResult_timeslotsContainer);
	    	timeslotsContainer.removeAllViews();
	    	for (int i=0; i<4; i++) {
	    		TextView timeslot = new TextView(getBaseContext());
	    		timeslot.setWidth(widthInDp);
	    		timeslot.setHeight(heightInDp);
	    		timeslot.setGravity(Gravity.CENTER);
	    		timeslot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
	    		timeslot.setTextColor(Color.WHITE);
	    		String viewString = "-";
	    		if (iter.hasNext()) {
	    			timeslot.setBackgroundColor(Color.argb(255, 14, 180, 88));
	    			String timeslotString = iter.next();
	    			viewString = timeslotString.substring(0, 2) + ":" + timeslotString.substring(2);
	    		} else {
	    			timeslot.setBackgroundColor(Color.argb(255, 214, 214, 214));
	    		}
	    		timeslot.setText(viewString);
	    		timeslotsContainer.addView(timeslot);
	    		
	    		if (i<3) {
	    			LinearLayout paddingView = new LinearLayout(getBaseContext());
	    			paddingView.setOrientation(LinearLayout.HORIZONTAL);
	    			LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
	    			params.weight = 1;
	    			paddingView.setLayoutParams(params);
	    			timeslotsContainer.addView(paddingView);
	    		}
	    	}
	    	viewLayout.setTag(item.licno);
	    	
	    	PhotoView pv = ((PhotoView) viewLayout.findViewById(R.id.restaurantResult_thumbnailImageView));
	    	try{
	    		URL req = new URL(item.img);
	    		pv.setImageURL(req, true, null);
	    	}
	    	catch(MalformedURLException mfe){}
	    	
	    	viewLayout.setOnClickListener((new View.OnClickListener(){
	    		@Override
	    		public void onClick(View v){
	    			RestaurantManager.getInstance(MainActivity.this).showMain((String)v.getTag());
	    		}
	    	}));
	        
	        ((ViewPager) container).addView(viewLayout);
	        return viewLayout;
		}
		
		@Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
		
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}
    }
    
}
