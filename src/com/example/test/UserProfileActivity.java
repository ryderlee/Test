package com.example.test;

import android.os.Bundle;
import android.support.v7.app.*;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class UserProfileActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		usernameTextView.setText(UserData.getInstance(this.getApplicationContext()).getFullName());
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

}
