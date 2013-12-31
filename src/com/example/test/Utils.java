package com.example.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

public class Utils {

	static public String post(String url, HashMap<String, String> params) {
		URL r; 
    	try {
	    	r = new URL (url);
	    	String body = "";
	    	Iterator<Entry<String, String>> iter = params.entrySet().iterator();
	    	while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				body += entry.getKey()+"="+URLEncoder.encode(entry.getValue(), "UTF-8")+(iter.hasNext()?"&":"");
			}
	    	Log.d("com.example.test", "Post body: "+body);
	    	return Utils.post(r, body);
    	}catch (Exception e){}
    	return "";
	}
	
	static public String post(URL url, String body) {
		String line;
		StringBuilder sb = new StringBuilder();
    	try{
    		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    		
    		urlc.setRequestMethod("POST");
    		urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    		urlc.setRequestProperty("Content-Length", ""+ body.length());
    		DataOutputStream dos = new DataOutputStream(urlc.getOutputStream());
    		dos.writeBytes(body);
    		dos.flush();
    		dos.close();
    		
            BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            while((line=bfr.readLine())!=null) {
            	sb.append(line + "\n");
            }
    	} catch(Exception e){
        	Log.d("exception", e.getMessage());
        }
		return sb.toString();
	}
	
	static public String getJsonString(String s) {
    	URL r; 
    	try {
	    	r = new URL (s);
	    	return Utils.getJsonString(r);
    	}catch (Exception e){}
    	return "";
    }
	
	static public String getJsonString(URL url) {
        String line;
        StringBuilder sb = new StringBuilder();
    	try{
    		URLConnection urlc = url.openConnection();
            BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            while((line=bfr.readLine())!=null)
            {
            	sb.append(line + "\n");
            }
    	} catch(Exception e){
        	Log.d("exception", e.getMessage());
        }
		return sb.toString();
    }
	
}
