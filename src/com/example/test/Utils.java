package com.example.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class Utils {

	static public String getJsonString(String s){
    	URL r; 
    	try {
    	r = new URL (s);
    	return Utils.getJsonString(r);
    	}catch (Exception e){}
    	return "";
    }
	
	static public String getJsonString(URL url){
        String line;
        StringBuilder sb = new StringBuilder();
    	try{
    		URLConnection urlc=url.openConnection();
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
