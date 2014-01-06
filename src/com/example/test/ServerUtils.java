package com.example.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.example.test.AppConfigsHelper.ApiConfigs;
import com.example.test.AppConfigsHelper.ApiResource;

import android.util.Log;

public class ServerUtils {

	static public String submit(String apiName, HashMap<String, String> params) {
		ApiConfigs apiConfigs = AppConfigsHelper.getInstance().getApiConfigs();
		HashMap<String, ApiResource> apiResources = apiConfigs.getApiResources();
		if (apiResources.containsKey(apiName)) {
			ApiResource apiResource = apiResources.get(apiName);
			try {
				String uriStr = apiConfigs.getApiServerUri()+apiResource.getUri();
		    	Iterator<Entry<String, String>> iter = params.entrySet().iterator();
		    	String body = "";
		    	while (iter.hasNext()) {
					Entry<String, String> entry = iter.next();
					if (apiResource.getUriParameters().contains(entry.getKey())) {
						uriStr = uriStr.replace(":"+entry.getKey(), entry.getValue());
					} else {
						body += entry.getKey()+"="+URLEncoder.encode(entry.getValue(), "UTF-8")+(iter.hasNext()?"&":"");
					}
				}
		    	if (apiResource.getMethod().equals("GET") && !body.isEmpty()) {
		    		uriStr += "?" + body;
		    	}
		    	URL uri = new URL(uriStr);
		    	Log.d("ServerUtils", String.format("Api:%s, Method:%s, Submit to: %s", apiName, apiResource.getMethod(), uriStr));
		    	return ServerUtils.submit(uri, apiResource.getMethod(), body);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		return "";
	}
	
	static private String submit(URL url, String method, String body) {
		String line;
		StringBuilder sb = new StringBuilder();
		try {
    		HttpURLConnection urlc;
    		
			urlc = (HttpURLConnection) url.openConnection();
    		urlc.setRequestMethod(method);
    		urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    		
    		if (!method.equals("GET")) {
    			Log.d("ServerUtils", "body: " + body);
				urlc.setRequestProperty("Content-Length", ""+ body.length());
				DataOutputStream dos = new DataOutputStream(urlc.getOutputStream());
	    		dos.writeBytes(body);
	    		dos.flush();
	    		dos.close();
    		}
    		
            BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            while((line=bfr.readLine())!=null) {
            	sb.append(line + "\n");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}
