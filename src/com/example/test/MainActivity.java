package com.example.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import android.text.TextUtils;
import android.util.Log;

import java.net.*;

import org.json.*;

import android.support.v7.app.*;

import com.example.test.PhotoView;
import com.example.test.R;

import java.util.*;

import android.view.*;

public class MainActivity extends ActionBarActivity {
	
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
	private String mShowSearchButtonStr;
	
	private final int mDefaultNoOfParticipants = 2;
	private final int mPageSize = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mShowSearchButton = (Button) findViewById(R.id.showSearchButton);
        mSearchView = findViewById(R.id.searchView);
        mBookingPickerButton = (Button) findViewById(R.id.bookingPickerButton);
        mBookingPicker = (BookingPicker) findViewById(R.id.bookingPicker);
        mKeywordEditText = (EditText) findViewById(R.id.keywordEditText);
        
        mBookingPicker.setOnValueChangeListener(new BookingPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(int numOfPeople, Date date) {
				SearchData.getInstance().setSearchDate(date);
				SearchData.getInstance().setNumberOfReservation(numOfPeople);
				Calendar today = Calendar.getInstance();
				Calendar tmr = Calendar.getInstance();
				Calendar oneWeek = Calendar.getInstance();
				tmr.add(Calendar.DATE, 1);
				oneWeek.add(Calendar.DATE, 7);
				Calendar targetDate = Calendar.getInstance();
				targetDate.setTime(date);
				
				String dateStr = new SimpleDateFormat("EEE, d MMM").format(date);
				mShowSearchButtonStr = new SimpleDateFormat("', on' d MMM").format(date);
				if (targetDate.get(Calendar.DATE) == today.get(Calendar.DATE)) {
					dateStr = "today";
					mShowSearchButtonStr = " today";
				} else if (targetDate.get(Calendar.DATE) == tmr.get(Calendar.DATE)) {
					dateStr = "tomorrow";
					mShowSearchButtonStr = " tomorrow";
				} else if (oneWeek.get(Calendar.DATE) - targetDate.get(Calendar.DATE) > 0) {
					dateStr = new SimpleDateFormat("EEEEEEE").format(date);
					mShowSearchButtonStr = new SimpleDateFormat("', on' EEE").format(date);
				}
				mBookingPickerButton.setText("Table for " + numOfPeople + ", " + dateStr + " at " + new SimpleDateFormat("h:mm aa").format(date).toLowerCase());
			}
		});
        mBookingPicker.setDate(Calendar.getInstance().getTime());
        mBookingPicker.setNoOfParticipants(mDefaultNoOfParticipants);
        
        mShowSearchButton.setText("Table for " + mDefaultNoOfParticipants + " today");

        ProgressBar progressFooter = new ProgressBar(this, null, android.R.attr.progressBarStyle);
		LinearLayout.LayoutParams footerParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressFooter.setLayoutParams(footerParam);
        mListViewFooter = new LinearLayout(this);
		mListViewFooter.setGravity(Gravity.CENTER);
		mListViewFooter.addView(progressFooter);

		mAdapter = new ListViewAdapter<RestaurantResultItem>(this, R.layout.restaurant_search_result_tablerow);
		mListView = (ListView)this.findViewById(R.id.searchResultListView); 
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//				Log.d("com.example.test", "onScroll: "+firstVisibleItem+","+visibleItemCount+","+totalItemCount+"|"+mLoading);
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
        String action = intent.getStringExtra("ACTION");
    	if (action.equals(Constants.ACTION_LOGIN_SUCCESS)) {
            Log.d("example", "login success and show profile now");
    		UserManager.getInstance(this).showProfile();
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
	    	TextView typeTextView = (TextView) convertView.findViewById(R.id.restaurantResult_typeTextView);
	    	TextView ratingTextView = (TextView) convertView.findViewById(R.id.restaurantResult_ratingTextView);
	    	TextView slotTextView = (TextView) convertView.findViewById(R.id.restaurantResult_slotTextView);
	    	
	    	RestaurantResultItem item = (RestaurantResultItem)this.getItem(position);
	    	nameTextView.setText(item.ss);
	    	typeTextView.setText(item.licno);
//	    	ratingTextView.setText("" + item.rating);
	    	
	    	PhotoView pv = ((PhotoView) convertView.findViewById(R.id.restaurantResult_thumbnailImageView));
	    	try{
	    		URL req = new URL(item.img);
	    		pv.setImageURL(req, true, null);
	    	}
	    	catch(MalformedURLException mfe){}
	    	
	    	convertView.setOnClickListener((new View.OnClickListener(){
	    		@Override
	    		public void onClick(View v){
	    			TextView typeTextView = (TextView) v.findViewById(R.id.restaurantResult_typeTextView);
	    			RestaurantManager.getInstance(MainActivity.this).showMain(typeTextView.getText().toString());
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
    	mShowSearchButton.setText("Table for " + mBookingPicker.getNoOfParticipants() + mShowSearchButtonStr);
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
    	if (!mListViewFooter.isShown()) {
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
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			
	    	results = new ArrayList<RestaurantResultItem>();
	    	String url = "http://10.0.2.2:8888/index.php/restaurant?p="+mPage;
	    	if (!TextUtils.isEmpty(mKeyword)) {
        		url += "&k=" + mKeyword;
        	}
	    	Log.d("com.example.test", "Url: "+url);
        	String s = Utils.getJsonString(url);
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
