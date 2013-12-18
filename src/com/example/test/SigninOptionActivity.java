package com.example.test;

import android.os.Bundle;
import android.support.v7.app.*;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class SigninOptionActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin_option);
		
		Intent intent = getIntent();
		Boolean reserve = intent.getBooleanExtra("RESERVE", false);
		
//		findViewById(R.id.signupButton).setVisibility(reserve ? View.GONE : View.VISIBLE);
		findViewById(R.id.textOR).setVisibility(reserve ? View.GONE : View.VISIBLE);
		findViewById(R.id.guestButton).setVisibility(reserve ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signin_option, menu);
		return true;
	}
	
	public void signinButton_onClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SigninActivity.class);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public void fbSigninButton_onClick(View view) {
		
	}
	
	public void signupButton_onClick(View view) {
		
	}
	
	public void guestButton_onClick(View view) {
		UserManager.getInstance(this).reserveAsGuest();
	}
	
}
