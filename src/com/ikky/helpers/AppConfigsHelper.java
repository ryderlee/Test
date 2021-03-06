package com.ikky.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

public class AppConfigsHelper {
	
	static private AppConfigsHelper sInstance;
	static private AssetManager sAssetManager;
	
	private HashMap<String, ApiConfigs> mApiConfigs;
	private String mEnvironment;
	private String mApiVersion;
	
	private String mNamespace;
	
	
	public static synchronized AppConfigsHelper getInstance() {
		return sInstance;
	}
	
	public static synchronized void init(Context context) {
		if (sInstance == null) {
			sInstance = new AppConfigsHelper();
			sAssetManager = context.getAssets();
			try {
				sInstance.parseXml();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ApiConfigs getApiConfigs() {
		return mApiConfigs.get(mApiVersion);
	}
	
	private void parseXml() throws XmlPullParserException, IOException {
		mApiConfigs = new HashMap<String, ApiConfigs>();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(sAssetManager.open("configs/app.xml"), null);
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, mNamespace, "configs");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("api")) {
				readApi(parser);
			} else if (name.equals("core")) {
				readCore(parser);
			} else {
				skip(parser);
			}
		}
	}
	
	private void readCore(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, mNamespace, "core");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("api-version")) {
				mApiVersion = readText(parser, name);
			} else if (name.equals("environment")) {
				mEnvironment = readText(parser, name);
			}
		}
	}
	
	private void readApi(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, mNamespace, "api");
		ApiConfigs apiConfigs = new ApiConfigs();
		String apiVersion = parser.getAttributeValue(mNamespace, "version");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("uri")) {
				apiConfigs.setApiServerUris(readApiServerUris(parser));
			} else if (name.equals("resource")) {
				apiConfigs.getApiResources().put(parser.getAttributeValue(mNamespace, "name"), readApiResource(parser));
			} else {
				skip(parser);
			}
		}
		mApiConfigs.put(apiVersion, apiConfigs);
	}
	
	private HashMap<String, String> readApiServerUris(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, mNamespace, "uri");
		HashMap<String, String> apiServerUris = new HashMap<String, String>();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			apiServerUris.put(name, readText(parser, name));
		}
		return apiServerUris;
	}
	
	private ApiResource readApiResource(XmlPullParser parser) throws XmlPullParserException, IOException {
		ApiResource apiResource = new ApiResource();
		parser.require(XmlPullParser.START_TAG, mNamespace, "resource");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName(); 
			if (name.equals("uri")) {
				apiResource.setUri(readText(parser, name));
			} else if (name.equals("method")) {
				apiResource.setMethod(readText(parser, name));
			} else {
				skip(parser);
			}
		}
		return apiResource;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	    	switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	        	depth--;
	            break;
	        case XmlPullParser.START_TAG:
	        	depth++;
	            break;
	        }
	    }
	}
	
	private String readText(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, mNamespace, name);
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    parser.require(XmlPullParser.END_TAG, mNamespace, name);
	    return result;
	}
	
	
	
	//	Api Data Objects
	public class ApiConfigs {
		private HashMap<String, String> mApiServerUris;
		private HashMap<String, ApiResource> mApiResources;
		public ApiConfigs() {
			mApiResources = new HashMap<String, ApiResource>();
		}
		public String getApiServerUri() {
			return mApiServerUris.get(mEnvironment);
		}
		private void setApiServerUris(HashMap<String, String> mApiServerUris) {
			this.mApiServerUris = mApiServerUris;
		}
		public HashMap<String, ApiResource> getApiResources() {
			return mApiResources;
		}
	}
	
	public class ApiResource {
		private String mUri;
		private String mMethod;
		private ArrayList<String> mUriParameters;
		public ApiResource() {
			mUriParameters = new ArrayList<String>();
		}
		public String getUri() {
			return mUri;
		}
		public void setUri(String mUri) {
			Pattern pattern = Pattern.compile("/:(\\w+)(/|$)");
			Matcher matcher = pattern.matcher(mUri);
			while (matcher.find()) {
				mUriParameters.add(matcher.group(1));
			}
			this.mUri = mUri;
		}
		public String getMethod() {
			return mMethod;
		}
		public void setMethod(String mMethod) {
			this.mMethod = mMethod;
		}
		public ArrayList<String> getUriParameters() {
			return mUriParameters;
		}
	}
	
}
