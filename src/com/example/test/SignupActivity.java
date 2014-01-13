package com.example.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends Activity {
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mFirstName;
	private String mLastName;
	private String mEmail;
	private String mPhone;
	private String mPassword;
	private String mPasswordConfirm;

	// UI references.
	private EditText mFirstNameView;
	private EditText mLastNameView;
	private EditText mEmailView;
	private EditText mPhoneView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signup);

		// Set up the login form.
		mFirstNameView = (EditText) findViewById(R.id.SIGNUP_FIRSTNAME);
		mLastNameView = (EditText) findViewById(R.id.SIGNUP_LASTNAME);
		mEmailView = (EditText) findViewById(R.id.SIGNUP_EMAIL);
		mPhoneView = (EditText) findViewById(R.id.SIGNUP_PHONE);
		mPasswordView = (EditText) findViewById(R.id.SIGNUP_PASSWORD);
		mPasswordConfirmView = (EditText) findViewById(R.id.SIGNUP_PASSWORD_CONFIRM);
		
		/*mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});*/

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.SIGNUP_BUTTON).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mFirstNameView.setError(null);
		mLastNameView.setError(null);
		mEmailView.setError(null);
		mPhoneView.setError(null);
		mPasswordView.setError(null);
		mPasswordConfirmView.setError(null);

		// Store values at the time of the login attempt.
		mFirstName = mFirstNameView.getText().toString();
		mLastName = mLastNameView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPasswordConfirm = mPasswordConfirmView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mFirstName)) {
			mFirstNameView.setError(getString(R.string.error_field_required));
			focusView = mFirstNameView;
			cancel = true;
		} else
		
		if (TextUtils.isEmpty(mLastName)) {
			mLastNameView.setError(getString(R.string.error_field_required));
			focusView = mLastNameView;
			cancel = true;
		} else 
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		} else 
		
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.length() < 8) {
			mPhoneView.setError("Invalid phone number");
			focusView = mPhoneView;
			cancel = true;
		} else 
		
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 6) {
			mPasswordView.setError("At least 6 characters");
			focusView = mPasswordView;
			cancel = true;
		} else 
		
		if (TextUtils.isEmpty(mPasswordConfirm)) {
			mPasswordConfirmView.setError(getString(R.string.error_field_required));
			focusView = mPasswordConfirmView;
			cancel = true;
		} else if (!mPasswordConfirm.equals(mPassword)) {
			mPasswordConfirmView.setError("Password not match");
			focusView = mPasswordConfirmView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText("Creating Account");
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
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

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void loginDone(JSONObject userJson) {
		UserManager.getInstance(this).loginSuccess(userJson);
	}
	
	public void signupFailed() {
		Toast.makeText(getApplicationContext(), "Signup failed, please try again", Toast.LENGTH_LONG).show();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
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
			
        	String jsonString = ServerUtils.submitRequest("userSignup", "firstName="+mFirstName, "lastName="+mLastName, "email="+mEmail, "phone="+mPhone, "password="+mPassword);
			JSONObject jso;
			try {
				jso = new JSONObject(jsonString);
				if (jso.getBoolean("result")) {
					String userId = jso.getJSONObject("values").getString("userID");
					String token = jso.getJSONObject("values").getString("token");
					
					userJson = new JSONObject();
					userJson.put("user_id", userId);
					userJson.put("first_name", mFirstName);
					userJson.put("last_name", mLastName);
					userJson.put("email", mEmail);
					userJson.put("phone", mPhone);
					userJson.put("token", token);
					
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				loginDone(userJson);
			} else {
				signupFailed();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
