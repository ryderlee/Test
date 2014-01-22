package com.example.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.test.AppConfigsHelper.ApiConfigs;
import com.example.test.AppConfigsHelper.ApiResource;

import android.text.TextUtils;
import android.util.Log;

public class ServerUtils {

	static public String submitRequest(String apiName, String... keyValuePairs) {
		ApiConfigs apiConfigs = AppConfigsHelper.getInstance().getApiConfigs();
		HashMap<String, ApiResource> apiResources = apiConfigs.getApiResources();
		if (apiResources.containsKey(apiName)) {
			ApiResource apiResource = apiResources.get(apiName);
			String uri = apiConfigs.getApiServerUri()+apiResource.getUri();
			String body = "";
			for (String keyValuePair : keyValuePairs) {
				String[] arr = keyValuePair.split("=", 2);
				if (arr.length > 1 && !TextUtils.isEmpty(arr[1]) && !arr[1].equals("null")) {
					if (apiResource.getUriParameters().contains(arr[0])) {
						uri = uri.replace(":"+arr[0], arr[1]);
					} else {
						try {
							body += arr[0]+"="+URLEncoder.encode(arr[1], "UTF8")+"&";
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (apiResource.getMethod().equals("GET") && !TextUtils.isEmpty(body)) {
	    		uri += "?" + body;
	    	}
			Log.d("ServerUtils", String.format("Api Name: %s, Method: %s, Uri: %s", apiName, apiResource.getMethod(), uri));
	    	return ServerUtils.submit(uri, apiResource.getMethod(), body);
		}
		return "";
	}
	
	static private String submit(String uri, String method, String body) {
		Log.d("ServerUtils", String.format("Request Body: %s", body));
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpRequestBase requestBase = null;
			if (method.equals("GET")) {
				requestBase = new HttpGet(uri);
			} else if (method.equals("POST")) {
				requestBase = new HttpPost(uri);
				((HttpPost) requestBase).setEntity(new StringEntity(body, "UTF8"));
				requestBase.addHeader("Content-Type", "application/x-www-form-urlencoded");
			} else if (method.equals("PUT")) {
				requestBase = new HttpPut(uri);
				((HttpPut) requestBase).setEntity(new StringEntity(body, "UTF8"));
				requestBase.addHeader("Content-Type", "application/x-www-form-urlencoded");
			}
			HttpResponse response = httpClient.execute(requestBase);
			String responseBody = ServerUtils.convertToString(response.getEntity().getContent());
			Log.d("ServerUtils", String.format("Response Status: %s", response.getStatusLine().toString()));
			Log.v("ServerUtils", String.format("Response Body:%s", responseBody));
			return responseBody;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	static private String convertToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
}
