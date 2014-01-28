package com.example.test;

import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

public class CustomActivity extends ActionBarActivity {

	@Override
	public void onStart() {
		Log.d("CustomActivity", "onStart");
		super.onStart();
		getWindow().getDecorView().findViewById(android.R.id.content).setFocusableInTouchMode(true);
		setupUI(getWindow().getDecorView().findViewById(android.R.id.content));
	}
	
	public void setupUI(View view) {
		
		final GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				Log.d("CustomActivity", "onSingleTapUp");
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
					Log.d("CustomActivity", "MotionEvent: "+event+" | View: "+view);
					detector.onTouchEvent(event);
					return false;
				}
	    	});
	    } else if (EditText.class.isInstance(view)) {
	    	view.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					Log.d("CustomActivity", "OnFocusChange hasFocus: "+hasFocus);
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
		Log.d("CustomActivity", "Try to hide keyboard");
		getWindow().getDecorView().findViewById(android.R.id.content).requestFocus();
	    InputMethodManager imm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (imm != null && this.getCurrentFocus() != null) {
	    	Log.d("CustomActivity", "Hide Keyboard");
	    	imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	    }
	}
	
	public void showSoftKeyboard() {
		Log.d("CustomActivity", "Try to show keyboard");
		InputMethodManager imm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (imm != null) {
	    	Log.d("CustomActivity", "Show Keyboard");
	    	imm.showSoftInput(this.getCurrentFocus(), 0);
	    }
	}
	
}
