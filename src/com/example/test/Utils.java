package com.example.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

public class Utils {

	static public String put(String url, HashMap<String, String> params) {
		URL r; 
    	try {
	    	r = new URL (url);
	    	String body = "";
	    	Iterator<Entry<String, String>> iter = params.entrySet().iterator();
	    	while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				body += entry.getKey()+"="+URLEncoder.encode(entry.getValue(), "UTF-8")+(iter.hasNext()?"&":"");
			}
	    	Log.d("com.example.test", "Put body: "+body);
	    	return Utils.put(r, body);
    	}catch (Exception e){}
    	return "";
	}
	
	static public String put(URL url, String body) {
		String line;
		StringBuilder sb = new StringBuilder();
    	try{
    		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    		
    		urlc.setRequestMethod("PUT");
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
	
	static public String getShortBookingInfo() {
		return getShortBookingInfo(SearchData.getInstance().getSearchDate(), SearchData.getInstance().getNumberOfReservation());
	}
	static public String getShortBookingInfo(Date date, int noOfParticipants) {
		Calendar today = Calendar.getInstance();
		Calendar tmr = Calendar.getInstance();
		Calendar oneWeek = Calendar.getInstance();
		tmr.add(Calendar.DATE, 1);
		oneWeek.add(Calendar.DATE, 7);
		Calendar targetDate = Calendar.getInstance();
		targetDate.setTime(date);
		
		String infoStr = new SimpleDateFormat("', on' d MMM").format(date);
		if (targetDate.get(Calendar.DATE) == today.get(Calendar.DATE)) {
			infoStr = " today";
		} else if (targetDate.get(Calendar.DATE) == tmr.get(Calendar.DATE)) {
			infoStr = " tomorrow";
		} else if (oneWeek.get(Calendar.DATE) - targetDate.get(Calendar.DATE) > 0) {
			infoStr = new SimpleDateFormat("', on' EEE").format(date);
		}
		return "Table for " + noOfParticipants + infoStr;
	}
	
	static public String getLongBookingInfo() {
		return getLongBookingInfo(SearchData.getInstance().getSearchDate(), SearchData.getInstance().getNumberOfReservation());
	}
	static public String getLongBookingInfo(Date date, int noOfParticipants) {
		Calendar today = Calendar.getInstance();
		Calendar tmr = Calendar.getInstance();
		Calendar oneWeek = Calendar.getInstance();
		tmr.add(Calendar.DATE, 1);
		oneWeek.add(Calendar.DATE, 7);
		Calendar targetDate = Calendar.getInstance();
		targetDate.setTime(date);
		
		String infoStr = new SimpleDateFormat("EEE, d MMM").format(date);
		if (targetDate.get(Calendar.DATE) == today.get(Calendar.DATE)) {
			infoStr = "today";
		} else if (targetDate.get(Calendar.DATE) == tmr.get(Calendar.DATE)) {
			infoStr = "tomorrow";
		} else if (oneWeek.get(Calendar.DATE) - targetDate.get(Calendar.DATE) > 0) {
			infoStr = new SimpleDateFormat("EEEEEEE").format(date);
		}
		return "Table for " + noOfParticipants + ", " + infoStr + " at " + new SimpleDateFormat("h:mm aa").format(date).toLowerCase();
	}
	
}
