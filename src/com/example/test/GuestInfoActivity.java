package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class GuestInfoActivity extends CustomActivity {

	private EditText mGuestFirstNameEditText;
	private EditText mGuestLastNameEditText;
	private EditText mGuestEmailEditText;
	private EditText mGuestPhoneEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest_info);
		
		mGuestFirstNameEditText = (EditText) findViewById(R.id.guestFirstNameEditText);
		mGuestFirstNameEditText.setText(UserData.getInstance().getFirstName());
		mGuestLastNameEditText = (EditText) findViewById(R.id.guestLastNameEditText);
		mGuestLastNameEditText.setText(UserData.getInstance().getLastName());
		mGuestEmailEditText = (EditText) findViewById(R.id.guestEmailEditText);
		mGuestEmailEditText.setText(UserData.getInstance().getEmail());
		mGuestPhoneEditText = (EditText) findViewById(R.id.guestPhoneEditText);
		mGuestPhoneEditText.setText(UserData.getInstance().getPhone());
		
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
		final String firstName = mGuestFirstNameEditText.getText().toString();
		final String lastName = mGuestLastNameEditText.getText().toString();
		final String email = mGuestEmailEditText.getText().toString();
		final String phone = mGuestPhoneEditText.getText().toString();
		final UserData ud = UserData.getInstance();
		final BookingManager bm = BookingManager.getInstance(this);
		
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
			if (UserData.getInstance().getEmail().isEmpty()) {
				ud.newSessionId();
				ud.setFirstName(firstName);
				ud.setLastName(lastName);
				ud.setEmail(email);
				ud.setPhone(phone);
				bm.guestProceed();
			} else if (!UserData.getInstance().getEmail().equals(email)) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setTitle("Warning");
				alertBuilder.setMessage("Email updated, confirm to proceed?");
				alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ud.newSessionId();
						ud.setFirstName(firstName);
						ud.setLastName(lastName);
						ud.setEmail(email);
						ud.setPhone(phone);
						bm.guestProceed();
					}
				});
				alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mGuestEmailEditText.requestFocus();
					}
				});
				alertBuilder.create().show();
			} else {
				ud.setFirstName(firstName);
				ud.setLastName(lastName);
				ud.setPhone(phone);
				bm.guestProceed();
			}
		}
	}

}
