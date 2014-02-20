package com.ikky.activities;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.ikky.activities.R;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.ikky.base.BaseActivity;
import com.ikky.helpers.ServerUtils;
import com.ikky.managers.RestaurantManager;
import com.ikky.managers.UserData;
import com.ikky.managers.UserManager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SigninOptionActivity extends BaseActivity {
	
	Boolean mReserve;
	
	private FbLoginTask mFbLoginTask = null;
	
	private View mLoginStatusView;
	private View mLoginOptionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_signin_option);
		
		Intent intent = getIntent();
		mReserve = intent.getBooleanExtra("RESERVE", false);
		
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginOptionView = findViewById(R.id.signinOptionView);
		
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
	
	@Override
	protected void fbOnLogin() {
		showProgress(true);
	}
	
	@Override
	protected void fbOnUserInfoCallback(String email, String fbId, String username, String firstName, String lastName, String token, String expireTs) {
		mFbLoginTask = new FbLoginTask(email, fbId, username, firstName, lastName, token, expireTs);
		mFbLoginTask.execute((Void) null);
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginOptionView.setVisibility(View.VISIBLE);
			mLoginOptionView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginOptionView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginOptionView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void loginDone(JSONObject userJson) {
		UserManager.getInstance(this).loginSuccess(userJson);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class FbLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		String mEmail;
		String mFbId;
		String mUsername;
		String mFirstName;
		String mLastName;
		String mToken;
		String mExpireTs;
		
		public FbLoginTask(String email, String fbId, String username, String firstName, String lastName, String token, String expireTs) {
			mEmail = email;
			mFbId = fbId;
			mUsername = username;
			mFirstName = firstName;
			mLastName = lastName;
			mToken = token;
			mExpireTs = expireTs;
		}
		
		private JSONObject userJson;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(0);
			} catch (InterruptedException e) {
				return false;
			}
			
        	String jsonString = ServerUtils.submitRequest("fbLogin", "snId=1", "email="+mEmail, "snUserId="+mFbId, "username="+mUsername, "firstName="+mFirstName, "lastName="+mLastName, "token="+mToken, "expireTs="+mExpireTs);
        	JSONObject json;
			try {
				json = new JSONObject(jsonString);
				if (json.getBoolean("result")) {
					// Update user id
					userJson = json.getJSONObject("user");
					return true;
				}
			} catch (JSONException e) {
			}
			
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mFbLoginTask = null;

			if (success) {
				loginDone(userJson);
			} else {
				Session.getActiveSession().closeAndClearTokenInformation();
				UserData.getInstance().clearFbData();
				showProgress(false);
				Toast.makeText(getApplicationContext(), "Login with Facebook failed", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			mFbLoginTask = null;
			showProgress(false);
		}
	}
	
	
}
