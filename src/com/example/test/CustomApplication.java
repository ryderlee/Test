package com.example.test;

import android.app.Application;
import android.content.Context;

public class CustomApplication extends Application {
	private static Context mContext;

	public void onCreate() {
		super.onCreate();
		CustomApplication.mContext = this.getApplicationContext();
		initSingletons();
	}
	
	public static Context getContext(){
		return mContext;
	}
	
	protected void initSingletons() {
		UserData.init(getApplicationContext());
		AppConfigsHelper.init(getApplicationContext());
	}
	
}
