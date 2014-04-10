package com.ikky.base;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.Request.GraphUserCallback;
import com.facebook.model.GraphUser;
import com.ikky.managers.UserData;

import com.ikky.activities.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

public class BaseActivity extends ActionBarActivity {
	
	protected Typeface mTypefaceRobotoRegular;
	
	protected Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        fbOnSessionStateChange(session, state, exception);
	    }
	};
	private UiLifecycleHelper fbUiHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTypefaceRobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		fbUiHelper = new UiLifecycleHelper(this, fbStatusCallback);
		fbUiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Session fbSession = Session.getActiveSession();
	    if (fbSession != null &&
	           (fbSession.isOpened() || fbSession.isClosed()) ) {
	        fbOnSessionStateChange(fbSession, fbSession.getState(), null);
	    }
	    fbUiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    fbUiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    fbUiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    fbUiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    fbUiHelper.onSaveInstanceState(outState);
	}
	
	private void fbOnSessionStateChange(final Session session, SessionState state, Exception exception) {
		Log.i("FacebookLogin", String.format("Session:%s, SessionState:%s, Exception:%s", session, state, exception));
	    if (state.isOpened()) {
	        Log.i("FacebookLogin", "Logged in...");
	        Log.v("FacebookLogin", String.format("Token: %s, Expire: %s", session.getAccessToken(), session.getExpirationDate()));
	        final Boolean initialLogin = UserData.getInstance().getFbToken().isEmpty();
	        fbOnSessionOpened();
	        if (initialLogin || UserData.getInstance().isUpdateFbData()) {
		        Request.newMeRequest(session, new GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						Log.v("FacebookLogin", "My information: "+user);
						if (user != null) {
							if (UserData.getInstance().setFbData(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), session.getAccessToken(), Long.toString(session.getExpirationDate().getTime()/1000))) {
								if (initialLogin) {
									fbOnUserInfoCallback(user.getProperty("email").toString(), user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), session.getAccessToken(), Long.toString(session.getExpirationDate().getTime()/1000));
								} else {
									// TODO: Send updated info to server
								}
							}
						}
						fbOnUserInfoRequestComplete();
					}
				}).executeAsync();
	        }
	    } else if (state.isClosed()) {
	        Log.i("FacebookLogin", "Logged out...");
	        UserData.getInstance().clearFbData();
	    }
	}

	// Override these to hide login with facebook layout
	protected void fbOnSessionOpened() {
	}
	protected void fbOnUserInfoRequestComplete() {
	}
	protected void fbOnUserInfoCallback(String email, String fbId, String username, String firstName, String lastName, String token, String expireTs) {
	}
	

	
	@Override
	public void onStart() {
//		Log.d("CustomActivity", "onStart");
		super.onStart();
		getWindow().getDecorView().findViewById(android.R.id.content).setFocusableInTouchMode(true);
		setupUI(getWindow().getDecorView().findViewById(android.R.id.content));
	}
	
	public void setupUI(View view) {
		
		final GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
//				Log.d("CustomActivity", "onSingleTapUp");
				hideSoftKeyboard();
				return false;
			}
		});
		
	    //Set up touch listener for non-text box views to hide keyboard.
//		view.setClickable(true);
	    if (!EditText.class.isInstance(view)) {
	    	view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
//					Log.d("CustomActivity", "MotionEvent: "+event+" | View: "+view);
					detector.onTouchEvent(event);
					return false;
				}
	    	});
	    } else if (EditText.class.isInstance(view)) {
	    	view.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
//					Log.d("CustomActivity", "OnFocusChange hasFocus: "+hasFocus);
					if (hasFocus) {
						showSoftKeyboard();
					}
				}
			});
	    }

	    //If a layout container, iterate over children and seed recursion.
	    if (ViewGroup.class.isInstance(view) && !ListView.class.isInstance(view)) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            setupUI(innerView);
	        }
	    }
	}
	
	public void hideSoftKeyboard() {
//		Log.d("CustomActivity", "Try to hide keyboard");
		getWindow().getDecorView().findViewById(android.R.id.content).requestFocus();
	    InputMethodManager imm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (imm != null && this.getCurrentFocus() != null) {
//	    	Log.d("CustomActivity", "Hide Keyboard");
	    	imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	public void showSoftKeyboard() {
//		Log.d("CustomActivity", "Try to show keyboard");
		InputMethodManager imm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (imm != null) {
//	    	Log.d("CustomActivity", "Show Keyboard");
	    	imm.showSoftInput(this.getCurrentFocus(), 0);
	    }
	}
	
}
