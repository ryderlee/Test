package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class GuestInfoActivity extends Activity {

	private EditText mGuestFirstNameEditText;
	private EditText mGuestLastNameEditText;
	private EditText mGuestEmailEditText;
	private EditText mGuestPhoneEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest_info);
		
		mGuestFirstNameEditText = (EditText) findViewById(R.id.guestFirstNameEditText);
		mGuestLastNameEditText = (EditText) findViewById(R.id.guestLastNameEditText);
		mGuestEmailEditText = (EditText) findViewById(R.id.guestEmailEditText);
		mGuestPhoneEditText = (EditText) findViewById(R.id.guestPhoneEditText);
		
		RestaurantManager.getInstance(this).displayMiniBlock(findViewById(R.id.bookingInfoView));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guest_info, menu);
		return true;
	}
	
	public void guestBackButton_onClick(View view) {
		BookingManager.getInstance(this).cancelGatherGuestInfo();
	}
	
	public void guestNextButton_onClick(View view) {
		String firstName = mGuestFirstNameEditText.getText().toString();
		String lastName = mGuestLastNameEditText.getText().toString();
		String email = mGuestEmailEditText.getText().toString();
		String phone = mGuestPhoneEditText.getText().toString();
		
		Boolean valid = true;
		if (TextUtils.isEmpty(firstName)) {
			mGuestFirstNameEditText.setError("First name is required");
			mGuestFirstNameEditText.requestFocus();
			valid = false;
		} else if (TextUtils.isEmpty(lastName)) {
			mGuestLastNameEditText.setError("Last name is required");
			mGuestLastNameEditText.requestFocus();
			valid = false;
		} else if (TextUtils.isEmpty(email)) {
			mGuestEmailEditText.setError("Email is required");
			mGuestEmailEditText.requestFocus();
			valid = false;
		} else if (!email.contains("@")) {
			mGuestEmailEditText.setError("Please provide a valid email");
			mGuestEmailEditText.requestFocus();
			valid = false;
		} else if (TextUtils.isEmpty(phone)) {
			mGuestPhoneEditText.setError("Phone is required");
			mGuestPhoneEditText.requestFocus();
			valid = false;
		} else if (phone.length() < 8) {
			mGuestPhoneEditText.setError("Please provide a valid phone");
			mGuestPhoneEditText.requestFocus();
			valid = false;
		}
		
		if (valid) {
			BookingManager.getInstance(this).setGuestInfo(firstName, lastName, email, phone);
		}
	}

}
