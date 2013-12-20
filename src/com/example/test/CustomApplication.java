package com.example.test;

import android.app.Application;

public class CustomApplication extends Application {

	public void onCreate() {
		super.onCreate();
		
		initSingletons();
	}
	
	protected void initSingletons() {
		UserData.init(getApplicationContext());
	}
	
}
