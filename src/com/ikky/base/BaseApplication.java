package com.ikky.base;

import com.ikky.helpers.AppConfigsHelper;
import com.ikky.managers.UserData;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
	private static Context mContext;

	public void onCreate() {
		super.onCreate();
		BaseApplication.mContext = this.getApplicationContext();
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
