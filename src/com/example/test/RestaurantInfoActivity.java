package com.example.test;


import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.*;
import android.support.v7.app.*;
//import android.view.*;

public class RestaurantInfoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		
		Intent intent = getIntent();
		RestaurantData rd = RestaurantData.getInstance();
		/*
		String RESTAURANT_ID  = intent.getStringExtra("RESTAURANT_ID");
		String RESTAURANT_NAME  = intent.getStringExtra("RESTAURANT_NAME");
		String RESTAURANT_ADDRESS  = intent.getStringExtra("RESTAURANT_ADDRESS");
		String RESTAURANT_PHONE  = intent.getStringExtra("RESTAURANT_PHONE");
		String RESTAURANT_CUISINE  = intent.getStringExtra("RESTAURANT_CUISINE");
		String RESTAURANT_PRICE  = intent.getStringExtra("RESTAURANT_PRICE");
		String RESTAURANT_HOURS  = intent.getStringExtra("RESTAURANT_HOURS");
		String RESTAURANT_PARKING  = intent.getStringExtra("RESTAURANT_PARKING");
		String RESTAURANT_DESCRIPTION  = intent.getStringExtra("RESTAURANT_DESCRIPTION");
		String RESTAURANT_MENU  = intent.getStringExtra("RESTAURANT_MENU");
		String RESTAURANT_REVIEW_OVERALL  = intent.getStringExtra("RESTAURANT_REVIEW_OVERALL");
		String RESTAURANT_REVIEW_FOOD = intent.getStringExtra("RESTAURANT_REVIEW_FOOD");
		String RESTAURANT_REVIEW_SERVICE  = intent.getStringExtra("RESTAURANT_REVIEW_SERVICE");
		String RESTAURANT_REVIEW_AMBIENCE = intent.getStringExtra("RESTAURANT_REVIEW_AMBIENCE");
		*/
		
		
		
		
		
		String RESTAURANT_LICNO = rd.getRestaurantID();
		TextView tv_RESTAURANT_NAME = (TextView) findViewById(R.id.RESTAURANT_NAME);
		TextView tv_RESTAURANT_ADDRESS = (TextView) findViewById(R.id.RESTAURANT_ADDRESS);
		TextView tv_RESTAURANT_PHONE= (TextView) findViewById(R.id.RESTAURANT_PHONE);
		TextView tv_RESTAURANT_CUISINE = (TextView) findViewById(R.id.RESTAURANT_CUISINE);
		TextView tv_RESTAURANT_PRICE= (TextView) findViewById(R.id.RESTAURANT_PRICE);
		TextView tv_RESTAURANT_HOURS= (TextView) findViewById(R.id.RESTAURANT_HOURS);
		TextView tv_RESTAURANT_PARKING= (TextView) findViewById(R.id.RESTAURANT_PARKING);
		TextView tv_RESTAURANT_DESCRIPTION = (TextView) findViewById(R.id.RESTAURANT_DESCRIPTION);
		TextView tv_RESTAURANT_MENU= (TextView) findViewById(R.id.RESTAURANT_MENU);
		TextView tv_RESTAURANT_REVIEW_OVERALL = (TextView) findViewById(R.id.RESTAURANT_REVIEW_OVERALL);
		TextView tv_RESTAURANT_REVIEW_FOOD = (TextView) findViewById(R.id.RESTAURANT_REVIEW_FOOD);
		TextView tv_RESTAURANT_REVIEW_SERVICE = (TextView) findViewById(R.id.RESTAURANT_REVIEW_SERVICE);
		TextView tv_RESTAURANT_REVIEW_AMBIENCE = (TextView) findViewById(R.id.RESTAURANT_REVIEW_AMBIENCE);
		tv_RESTAURANT_NAME.setText(rd.getRestaurantName());
		tv_RESTAURANT_ADDRESS.setText(rd.getRestaurantAddress());
		tv_RESTAURANT_PHONE.setText(rd.getRestaurantPhone());
		tv_RESTAURANT_CUISINE.setText(rd.getRestaurantCuisine());
		tv_RESTAURANT_PRICE.setText(rd.getRestaurantPrice());
		tv_RESTAURANT_HOURS.setText(rd.getRestaurantHours());
		tv_RESTAURANT_PARKING.setText(rd.getRestaurantParking());
		tv_RESTAURANT_DESCRIPTION.setText(rd.getRestaurantDescription());
		tv_RESTAURANT_MENU.setText(rd.getRestaurantMenu());
		tv_RESTAURANT_REVIEW_OVERALL.setText(rd.getRestaurantReviewOverall());
		tv_RESTAURANT_REVIEW_FOOD.setText(rd.getRestaurantReviewFood());
		tv_RESTAURANT_REVIEW_SERVICE.setText(rd.getRestaurantReviewService());
		tv_RESTAURANT_REVIEW_AMBIENCE.setText(rd.getRestaurantReviewAmbiance());
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurant_info, menu);
		return true;
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

	@Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getStringExtra("ACTION");
    	if (action.equals(Constants.ACTION_LOGIN_SUCCESS)) {
            Log.d("example", "ready to reserve as user");
    	} else if (action.equals(Constants.ACTION_RESERVE_AS_GUEST)) {
    		Log.d("example", "ready to reserve as guest");
    	}
    }
}
