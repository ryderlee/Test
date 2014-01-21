package com.example.test;

import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import android.util.Log;

import java.net.*;

import org.json.*;

import android.support.v7.app.*;

import com.example.test.PhotoView;
import com.example.test.R;

import java.util.*;

import android.view.*;

public class MainActivity extends CustomActivity {
	
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
	
	private final int mPageSize = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent() != null)
	        onNewIntent(getIntent());
        mShowSearchButton = (Button) findViewById(R.id.showSearchButton);
        mSearchView = findViewById(R.id.searchView);
        mBookingPickerButton = (Button) findViewById(R.id.bookingPickerButton);
        mBookingPicker = (BookingPicker) findViewById(R.id.bookingPicker);
        mKeywordEditText = (EditText) findViewById(R.id.keywordEditText);
        
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
		
		search(true);
		
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // same as using a normal menu
        switch(item.getItemId()) {
        case R.id.action_user:
        	UserManager.getInstance(this).login(false);
            break;
        }
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	if (keyCode == KeyEvent.KEYCODE_BACK) {
//    		moveTaskToBack(true);
//    		return true;
//    	}
//    	return super.onKeyDown(keyCode, event);
//    }
    

    
    private class RestaurantResultItem {
    	public String licno;
    	public String type;
    	public String dist;
    	public String ss;
    	public String adr;
    	public int rating;
    	public String img;
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
        	
        	String s = ServerUtils.submitRequest("getRestaurantList", "p="+mPage, "k="+mKeyword);
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
				mAdapter.addAll(results);
				mAdapter.notifyDataSetChanged();
			} else {
			}
		}
		
	}
    
}
