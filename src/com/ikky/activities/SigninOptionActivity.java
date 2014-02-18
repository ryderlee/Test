package com.ikky.activities;

import java.util.Arrays;

import com.ikky.activities.R;
import com.facebook.widget.LoginButton;
import com.ikky.base.BaseActivity;
import com.ikky.managers.RestaurantManager;
import com.ikky.managers.UserManager;

import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SigninOptionActivity extends BaseActivity {
	
	Boolean mReserve;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_signin_option);
		
		Intent intent = getIntent();
		mReserve = intent.getBooleanExtra("RESERVE", false);
		
//		findViewById(R.id.signupButton).setVisibility(reserve ? View.GONE : View.VISIBLE);
		findViewById(R.id.textOR).setVisibility(mReserve ? View.GONE : View.VISIBLE);
		findViewById(R.id.guestButton).setVisibility(mReserve ? View.VISIBLE : View.GONE);
		findViewById(R.id.bookingInfoView).setVisibility(mReserve ? View.VISIBLE : View.GONE);
		
		RestaurantManager.getInstance(this).displayMiniBlock(findViewById(R.id.bookingInfoView));
		
		LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fbSigninButton);
		fbLoginButton.setReadPermissions(Arrays.asList("email"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signin_option, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	/* As there are more than 1 way to start this SigninOptionActivity, (ie. there could be multiple possible parents)
	    	   Use code to decide UP to which Activity instead of specify in AndroidManifest.xml */
	    	Intent intent = new Intent();
	    	if (mReserve) {
	    		intent.setClass(this, RestaurantInfoActivity.class);
	    	} else {
	    		intent.setClass(this, MainActivity.class);
	    	}
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			this.startActivity(intent);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void signinButton_onClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SigninActivity.class);
		this.startActivity(intent);
	}
	
	public void signupButton_onClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SignupActivity.class);
		this.startActivity(intent);
	}
	
	public void guestButton_onClick(View view) {
		UserManager.getInstance(this).bookAsGuest();
	}
	
	
}
