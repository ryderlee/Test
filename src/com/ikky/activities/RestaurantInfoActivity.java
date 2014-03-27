package com.ikky.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ikky.activities.R;
import com.ikky.base.BaseActivity;
import com.ikky.constants.Constants;
import com.ikky.helpers.ServerUtils;
import com.ikky.helpers.Utils;
import com.ikky.managers.BookingManager;
import com.ikky.managers.RestaurantData;
import com.ikky.managers.SearchData;
import com.ikky.managers.UserManager;
import com.ikky.ui.BookingPicker;
import com.ikky.ui.PhotoView;
import com.ikky.ui.BookingPicker.OnValueChangeListener;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class RestaurantInfoActivity extends BaseActivity {
	
	private RestaurantInfoRequestTask mRestaurantInfoRequestTask = null;
	
	private ScrollView mInfoView;
	private View mProgressView;
	private RadioGroup mRadioGroup;
	
	private View mViewWrapper;
	private View mMenuView;
	private View mDetailView;
	private View mReviewsView;
	
	
	private ArrayList<Date> mAvailableTimeSlots;
	
	private TimeSlotsRequestTask mTimeSlotsRequestTask = null;
	private Date mTargetTime;
	private int mTargetNoOfParticipants;
	
	private View mTimeSlotsView;
	private BookingPicker mBookingPicker;
	private Button mPickerButton;
	private ProgressBar mPickerProgressBar;
	private HorizontalScrollView mTimeSlotsScrollView;
	private LinearLayout mTimeSlotsContainer;
	
	private ViewPager mViewPager;
	private int mViewPagerHeight;
	private ArrayList<String> mImageUrls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);
		
		mAvailableTimeSlots = new ArrayList<Date>();
		
		
		mInfoView = (ScrollView) findViewById(R.id.RESTAURANT_INFO_VIEW);
		mProgressView = findViewById(R.id.RESTAURANT_INFO_PROGRESS);
		
		mViewWrapper = findViewById(R.id.RESTAURANT_INFO_WRAPPER);
		
		mMenuView = findViewById(R.id.RESTAURANT_INFO_MENU_VIEW);
		mDetailView = findViewById(R.id.RESTAURANT_INFO_DETAIL_VIEW);
		mReviewsView = findViewById(R.id.RESTAURANT_INFO_REVIEWS_VIEW);
		
		
		mTimeSlotsView = (View) findViewById(R.id.timeSlotsView);
		mBookingPicker = (BookingPicker) findViewById(R.id.bookingPicker);
		mPickerButton = (Button) findViewById(R.id.pickerButton);
		mPickerProgressBar = (ProgressBar) findViewById(R.id.pickerProgressBar);
		mTimeSlotsScrollView = (HorizontalScrollView) findViewById(R.id.timeSlotsScrollView);
		mTimeSlotsContainer = (LinearLayout) findViewById(R.id.timeSlotsContainer);
		
		mTargetTime = SearchData.getInstance().getSearchDate();
		mTargetNoOfParticipants = SearchData.getInstance().getNumberOfReservation();
		
		mBookingPicker.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(int noOfParticipants, Date date) {
				SearchData.getInstance().setNumberOfReservation(noOfParticipants);
				SearchData.getInstance().setSearchDate(date);
				mPickerButton.setText(Utils.getLongBookingInfo());
			}
		});
		
		mRadioGroup = (RadioGroup) findViewById(R.id.RESTAURANT_INFO_TAB);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				showView(checkedId);
			}
		});
        mRadioGroup.check(R.id.infoRadioButton);
        
        mViewPager = (ViewPager) findViewById(R.id.fullScreenAlbumPager);
        mViewPager.setOffscreenPageLimit(3);
		FullScreenAlbumAdapter adapter = new FullScreenAlbumAdapter();
		mViewPager.setAdapter(adapter);
		
		mViewPagerHeight = mViewPager.getLayoutParams().height;
		
		mRestaurantInfoRequestTask = new RestaurantInfoRequestTask();
		mRestaurantInfoRequestTask.execute((Void) null);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Log.d("RestaurantInfoActivity", "onResume");
	    getTimeslots(true);
	}
	
	private void switchFullScreen(Boolean on) {
		getWindow().setFlags(on?WindowManager.LayoutParams.FLAG_FULLSCREEN:0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mInfoView.setFillViewport(on);
		if (on) {
			getActionBar().hide();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mViewPager.setLayoutParams(params);
		} else {
			getActionBar().show();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mViewPagerHeight);
			mViewPager.setLayoutParams(params);
		}
		mViewWrapper.setVisibility(on?View.GONE:View.VISIBLE);
		mViewPager.getAdapter().notifyDataSetChanged();
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
	
	private void updateRestaurantInfo(ArrayList<String> timeSlots) {
		TextView nameView = (TextView) findViewById(R.id.RESTAURANT_INFO_NAME);
		TextView addressView = (TextView) findViewById(R.id.RESTAURANT_INFO_ADDRESS);
		TextView phoneView = (TextView) findViewById(R.id.RESTAURANT_INFO_PHONE);
		TextView cuisineView = (TextView) findViewById(R.id.RESTAURANT_INFO_CUSINE);
		TextView priceView = (TextView) findViewById(R.id.RESTAURANT_INFO_PRICE);
		TextView hoursView = (TextView) findViewById(R.id.RESTAURANT_INFO_HOURS);
		TextView parkingView = (TextView) findViewById(R.id.RESTAURANT_INFO_PARKING);
		TextView descriptionView = (TextView) findViewById(R.id.RESTAURANT_INFO_DESCRIPTION);

		RestaurantData rd = RestaurantData.getInstance();
		
		nameView.setText(rd.getRestaurantName());
		addressView.setText(rd.getRestaurantAddress());
		phoneView.setText(rd.getRestaurantPhone());
		cuisineView.setText(rd.getRestaurantCuisine());
		priceView.setText(rd.getRestaurantPrice());
		hoursView.setText(rd.getRestaurantHours());
		parkingView.setText(rd.getRestaurantParking());
		descriptionView.setText(rd.getRestaurantDescription());
		
		mAvailableTimeSlots.clear();
		int min;
		String timeSlotStr;
		String timeSlotArr[];
		Calendar timeSlot;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar currentTime = Calendar.getInstance();
		for (int i=0; i<timeSlots.size(); i++) {
			timeSlotStr = timeSlots.get(i);
			timeSlotArr = timeSlotStr.split(":");
			timeSlot = Calendar.getInstance();
			timeSlot.setTime(SearchData.getInstance().getSearchDate());
			timeSlot.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSlotArr[0]));
			timeSlot.set(Calendar.MINUTE, Integer.parseInt(timeSlotArr[1]));
			timeSlot.set(Calendar.SECOND, 0);
			Log.d("currentTime", ""+ sdf.format(currentTime.getTime()));
			Log.d("addingTime", ""+ sdf.format(timeSlot.getTime()));
			if(currentTime.compareTo(timeSlot) < 0){
				mAvailableTimeSlots.add(timeSlot.getTime());
				Log.d("add","" + timeSlot.getTime());
			}else{
				Log.d("not adding","" + timeSlot.getTime());
			}
		}
        displayTimeSlots();
	}
	
	public void pickerButton_onClick(View view) {
		if (mBookingPicker.getVisibility()==View.VISIBLE) {
			mBookingPicker.setVisibility(View.GONE);
			mTimeSlotsView.setVisibility(View.VISIBLE);
			getTimeslots(false);
		} else {
			mBookingPicker.setVisibility(View.VISIBLE);
			mTimeSlotsView.setVisibility(View.GONE);
		}
	}
	
	private void getTimeslots(Boolean force) {
		if (force || (mBookingPicker.getDate().compareTo(mTargetTime) != 0 || mBookingPicker.getNoOfParticipants() != mTargetNoOfParticipants)) {
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
	}
	
	private void displayTimeSlots() {
		mTimeSlotsContainer.removeAllViews();
		
		for (int i=0; i<mAvailableTimeSlots.size(); i++) {
			Date d = mAvailableTimeSlots.get(i);
			LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.view_common_button, mTimeSlotsContainer, true);
			Button timeSlotButton = (Button) mTimeSlotsContainer.getChildAt(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 14, 5, 14);
			timeSlotButton.setLayoutParams(params);
			timeSlotButton.setBackgroundResource(R.drawable.common_button_background);
			timeSlotButton.setText(new SimpleDateFormat("h:mm aa").format(d).toLowerCase());
			timeSlotButton.setTag(d);
			timeSlotButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SearchData.getInstance().setChosenDate((Date)v.getTag());
					reserveButton_onClick(v);
				}
			});
		}
		
		mTimeSlotsScrollView.setVisibility(View.VISIBLE);
		mPickerProgressBar.setVisibility(View.GONE);
	}
	
	private void updateTimeSlots(ArrayList<String> timeslots) {
		mAvailableTimeSlots.clear();
		for (int i=0; i<timeslots.size(); i++) {
			String timeSlotStr = timeslots.get(i);
			String timeSlotArr[] = timeSlotStr.split(":");
			Calendar timeSlot = Calendar.getInstance();
			timeSlot.setTime(SearchData.getInstance().getSearchDate());
			timeSlot.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSlotArr[0]));
			timeSlot.set(Calendar.MINUTE, Integer.parseInt(timeSlotArr[1]));
			timeSlot.set(Calendar.SECOND, 0);
			mAvailableTimeSlots.add(timeSlot.getTime());
		}
	}

	@Override
	public void onBackPressed(){
		super.onBackPressed();
//		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public void reserveButton_onClick(View view) {
		UserManager.getInstance(this).login(true);
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
		
		ArrayList<String> mBookingSlots;
		
		@Override
		protected Boolean doInBackground(Void... params) {
		// TODO: attempt authentication against a network service.
			try {
				// Simulate network access.
				Thread.sleep(0);
			} catch (InterruptedException e) {
				return false;
			}
		
			String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(SearchData.getInstance().getChosenDate());
        	String jsonString = ServerUtils.submitRequest("getMerchantDetail", "mid="+RestaurantData.getInstance().getRestaurantID(), "booking_datetime="+datetime, "no_of_participants="+SearchData.getInstance().getNumberOfReservation());
			try {
				JSONObject json = new JSONObject(jsonString);
				
				RestaurantData rd = RestaurantData.getInstance();
				
				rd.setName(json.getString("RESTAURANT_NAME"));
				rd.setAddress(json.getString("RESTAURANT_ADDRESS"));
				rd.setPhone(json.getString("RESTAURANT_PHONE"));
				rd.setCuisine(json.getString("RESTAURANT_CUISINE"));
				rd.setPrice(json.getString("RESTAURANT_PRICE"));
				rd.setHours(json.getString("RESTAURANT_HOURS"));
				rd.setParking(json.getString("RESTAURANT_PARKING"));
				rd.setDescription(json.getString("RESTAURANT_DESCRIPTION"));
				rd.setMenu(json.getString("RESTAURANT_MENU"));
				rd.setReviewOverall(json.getString("RESTAURANT_REVIEW_OVERALL"));
				rd.setReviewFood(json.getString("RESTAURANT_REVIEW_FOOD"));
				rd.setReviewService(json.getString("RESTAURANT_REVIEW_SERVICE"));
				rd.setReviewAmbiance(json.getString("RESTAURANT_REVIEW_AMBIANCE"));
				rd.setReviews(json.getString("RESTAURANT_REVIEWS"));
				
				mBookingSlots = new ArrayList<String>();
				JSONObject jsonObj = json.getJSONObject("RESTAURANT_BOOKING_SLOTS");
				for (Iterator iter = jsonObj.keys(); iter.hasNext();) {
					String timeslotStr = iter.next().toString();
					if (jsonObj.getInt(timeslotStr) == 1) {
						mBookingSlots.add(timeslotStr.substring(0, 2) + ":" + timeslotStr.substring(2));
					}
				}
				Collections.sort(mBookingSlots);
				
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
				updateRestaurantInfo(mBookingSlots);
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
				Thread.sleep(0);
			} catch (InterruptedException e) {
				return false;
			}
			
			String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(SearchData.getInstance().getChosenDate());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d=SearchData.getInstance().getChosenDate();
			Calendar calObj= Calendar.getInstance();
			calObj.setTime(d);
        	String jsonString = ServerUtils.submitRequest("getAvailableTimeslots", "mid="+RestaurantData.getInstance().getRestaurantID(), "booking_datetime="+datetime, "no_of_participants="+SearchData.getInstance().getNumberOfReservation());
        	Calendar currentCal = Calendar.getInstance();
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONObject jsonObj = json.getJSONObject("RESTAURANT_BOOKING_SLOTS");
				
				ArrayList<String> bookingSlots = new ArrayList<String>();
				for (Iterator iter = jsonObj.keys(); iter.hasNext();) {
					String timeslotStr = iter.next().toString();
					if (jsonObj.getInt(timeslotStr) == 1) {
						calObj.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeslotStr.substring(0, 2)));
						calObj.set(Calendar.MINUTE, Integer.parseInt(timeslotStr.substring(2)));
						Log.d("currentTime", ""+ sdf.format(currentCal.getTime()));
						Log.d("addingTime", ""+ sdf.format(calObj.getTime()));
						if(currentCal.compareTo(calObj) <0){
							
                                bookingSlots.add(timeslotStr.substring(0, 2) + ":" + timeslotStr.substring(2));
							
							
						}
					}
				}
				Collections.sort(bookingSlots);
				
				updateTimeSlots(bookingSlots);
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
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
	
	
	
	private class FullScreenAlbumAdapter extends PagerAdapter {
		
		public FullScreenAlbumAdapter() {
	        mImageUrls = new ArrayList<String>();
	        mImageUrls.add("http://www.foodnut.com/i/Yung-Kee-Restaurant-Hong-Kong/Yung-Kee-Restaurant-Hong-Kong-1.jpg");
	        mImageUrls.add("http://www.12hk.com/area/Admiralty/LippoCtr_PHOT0582.jpg");
	        mImageUrls.add("http://www.eclectic-cool.com/wp-content/uploads/2011/04/hong-kong-street-signs.jpg");
	        mImageUrls.add("http://www.discoverhongkong.com/common/images/hotel/1315_image_COMP00050993_photo_1.jpg");
	        mImageUrls.add("http://annatam.com/wp-content/uploads/2013/12/cf7434fef207fe283c95be624f5db1b5.jpg");
	        mImageUrls.add("http://therakeonline.com/wp-content/uploads/2012/09/Where-The-Rake-Stays-Upper-House-Hong-Kong-Semi-private-booth.jpg");
	        mImageUrls.add("http://farm4.staticflickr.com/3255/2460544629_e3fa24bb40_o.jpg");
	        mImageUrls.add("http://www.yuantravel.com/wp-content/gallery/general-about-hong-kong/hongkong_2141.jpg");
	        mImageUrls.add("https://pbs.twimg.com/media/BbZpkd6CEAAB2GL.jpg");
	        mImageUrls.add("https://pbs.twimg.com/media/BbhSydpCAAAU-J5.jpg");
	        mImageUrls.add("http://fc04.deviantart.net/fs51/i/2009/307/a/3/Hong_Kong__Tallest_Building_by_thehardheadedsavior.jpg");
	    }

		@Override
		public int getCount() {
			return mImageUrls.size();
		}
 
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (RelativeLayout) object;
		}
		
		@Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        PhotoView photoView;
	  
	        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View viewLayout = inflater.inflate(R.layout.view_fullscreen_image, container, false);
	        
	        photoView = (PhotoView) viewLayout.findViewById(R.id.fullScreenImagePhotoView);
	        
			try {
				URL imageUrl = new URL(mImageUrls.get(position));
				photoView.setImageURL(imageUrl, true, null);
			}
			catch(MalformedURLException mfe){}
			
			viewLayout.findViewById(R.id.fullScreenImageCloseButton).setVisibility((getWindow().getAttributes().flags&WindowManager.LayoutParams.FLAG_FULLSCREEN)==0?View.GONE:View.VISIBLE);
			viewLayout.findViewById(R.id.fullScreenImageCloseButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					switchFullScreen(false);
				}
			});
			
			photoView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WindowManager.LayoutParams params = getWindow().getAttributes();
					if ((params.flags&WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0) {
						switchFullScreen(true);
					}
				}
			});
	  
	        ((ViewPager) container).addView(viewLayout);
	  
	        return viewLayout;
		}
		
		@Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);
		}
		
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}
		
	}
	
	
}
