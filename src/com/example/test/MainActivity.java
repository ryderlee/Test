package com.example.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.DatePicker.*;
import android.widget.TimePicker.*;
import android.graphics.*;
import android.util.Log;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;

import org.json.*;

import com.example.test.PhotoView;
import com.example.test.R;

import java.util.*;

import android.view.*;
import android.view.animation.AnimationUtils;

public class MainActivity extends Activity {

	Button dateButton;
	Button timeButton;
	Button numberButton;
	
	DatePicker datePicker1;
	TimePicker timePicker1;
	NumberPicker numberPicker1;
	
	SeekBar numberSeekBar1;
	
	EditText dateText;
	EditText timeText;
	
	ViewFlipper vflipper;
	
	ListViewAdapter<RestaurantResultItem> adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dateText = (EditText) findViewById(R.id.dateText1);
        timeText = (EditText) findViewById(R.id.timeText1);
        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);
        numberButton = (Button) findViewById(R.id.numberButton);
        datePicker1= (DatePicker) findViewById(R.id.datePicker1);
        timePicker1= (TimePicker) findViewById(R.id.timePicker1);
        numberPicker1= (NumberPicker) findViewById(R.id.numberPicker1);
        numberSeekBar1= (SeekBar) findViewById(R.id.numberSeekBar1);
        vflipper = (ViewFlipper) findViewById(R.id.masterViewFlipper);
        
        vflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        vflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        
        datePicker1.setMinDate(System.currentTimeMillis() - 1000);
        numberPicker1.setMaxValue(12);
        numberPicker1.setMinValue(2);
        Date d= new Date();
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        
        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				Date d = new Date();
				d.setHours(hourOfDay);
				d.setMinutes(minute);
				
				SimpleDateFormat f = new SimpleDateFormat("kk:mm");
				Button tb = (Button) findViewById(R.id.timeButton);
				tb.setText(f.format(d));
				
				// TODO Auto-generated method stub
				
			}
		});
        datePicker1.init(d.getYear(), d.getMonth(), d.getDay(), new OnDateChangedListener(){
        	
        		@Override
        		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        			Date d = new Date();
        			d.setYear(year);
        			d.setMonth(monthOfYear);
        			d.setDate(dayOfMonth);
        			
        			SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy");
        			
        			
			        Button db = (Button) findViewById(R.id.dateButton);
        			db.setText(f.format(d));
        		}	
        	
        });
        
        numberSeekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
				progressChanged = progressChanged / 5;
				if(progressChanged == 0)
					progressChanged = 1;
				numberButton.setText("" + progressChanged);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        
        datePicker1.updateDate(year, mon, day);
        
        timePicker1.setCurrentHour(hour);
        timePicker1.setCurrentMinute(min);
        Date dat = new Date();
        d.setTime(cal.getTimeInMillis());
		SimpleDateFormat f = new SimpleDateFormat("kk:mm");
		Button tb = (Button) findViewById(R.id.timeButton);
		tb.setText(f.format(dat));
		
		numberSeekBar1.setProgress(2 * 5);
		
		adapter = new ListViewAdapter<RestaurantResultItem>(this, R.layout.restaurant_search_result_tablerow);
        ListView listView = (ListView)this.findViewById(R.id.searchResultListView); 
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ScrollListener());
    }

    private class RestaurantResultItem {
    	public String licno;
    	public String type;
    	public String dist;
    	public String ss;
    	public String adr;
    	public int rating;
    	public String img;
    }
    
    private class ListViewAdapter<T> extends ArrayAdapter {
		public ListViewAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(this.getContext());
				convertView = inflater.inflate(R.layout.restaurant_search_result_tablerow, parent, false);
			}
			
			TextView nameTextView = (TextView) convertView.findViewById(R.id.restaurantResult_nameTextView);
	    	TextView typeTextView = (TextView) convertView.findViewById(R.id.restaurantResult_typeTextView);
	    	TextView ratingTextView = (TextView) convertView.findViewById(R.id.restaurantResult_ratingTextView);
	    	TextView slotTextView = (TextView) convertView.findViewById(R.id.restaurantResult_slotTextView);
	    	
	    	RestaurantResultItem item = (RestaurantResultItem)this.getItem(position);
	    	nameTextView.setText(item.ss);
	    	typeTextView.setText(item.licno);
//	    	ratingTextView.setText("" + item.rating);
	    	
	    	PhotoView pv = ((PhotoView) convertView.findViewById(R.id.restaurantResult_thumbnailImageView));
	    	try{
	    		//URL req = new URL( "http://giverny.org/hotels/corniche/restaurant-room.jpg");
	    		URL req = new URL(item.img);
	    		pv.setImageURL(req, true, null);
	    	}
	    	catch(MalformedURLException mfe){}
	    	catch(IOException ioe){}
	    	
	    	
	    	
	    	convertView.setOnClickListener((new View.OnClickListener(){
	    		@Override
	    		public void onClick(View v){
	    			TextView typeTextView = (TextView) v.findViewById(R.id.restaurantResult_typeTextView);
	    			/*
	    			Log.i("view","" + typeTextView.getText());
	    			View tv = v.getRootView();
	    			TextView restaurantInfoTextView = (TextView) tv.findViewById(R.id.restaurantInfo_textView);
	    			restaurantInfoTextView.setText(typeTextView.getText());
	    			vflipper.showNext();
	    			*/
	    			
	    			Intent intent = new Intent();
	    			String json = MainActivity.this.getJsonString("http://10.0.2.2:8888/restaurantInfo.json");
	    			JSONObject jso;
	    			try {
						jso = new JSONObject(json);
		    			intent.putExtra("RESTAURANT_ID", typeTextView.getText());
		    			intent.putExtra("RESTAURANT_NAME", jso.getString("RESTAURANT_NAME"));
		    			intent.putExtra("RESTAURANT_ADDRESS", jso.getString("RESTAURANT_ADDRESS"));
		    			intent.putExtra("RESTAURANT_PHONE", jso.getString("RESTAURANT_PHONE"));
		    			intent.putExtra("RESTAURANT_CUISINE", jso.getString("RESTAURANT_CUISINE"));
		    			intent.putExtra("RESTAURANT_PRICE", jso.getString("RESTAURANT_PRICE"));
		    			intent.putExtra("RESTAURANT_HOURS",jso.getString("RESTAURANT_HOURS") );
		    			intent.putExtra("RESTAURANT_PARKING", jso.getString("RESTAURANT_PARKING"));
		    			intent.putExtra("RESTAURANT_DESCRIPTION", jso.getString("RESTAURANT_DESCRIPTION"));
		    			intent.putExtra("RESTAURANT_MENU", jso.getString("RESTAURANT_MENU"));
		    			intent.putExtra("RESTAURANT_REVIEW_OVERALL", jso.getString("RESTAURANT_REVIEW_OVERALL"));
		    			intent.putExtra("RESTAURANT_REVIEW_FOOD", jso.getString("RESTAURANT_REVIEW_FOOD"));
		    			intent.putExtra("RESTAURANT_REVIEW_SERVICE", jso.getString("RESTAURANT_REVIEW_SERVICE"));
		    			intent.putExtra("RESTAURANT_REVIEW_AMBIENCE", jso.getString("RESTAURANT_REVIEW_AMBIENCE"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			intent.setClass(MainActivity.this, RestaurantInfoActivity.class);
	    			
	    			startActivity(intent);
	    			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			    	
	    		}
	    	}));
	    	/*
	    	convertView.setOnClickListener((new View.OnClickListener(){
	    		@Override
	    		public void onClick(View v){
	    		TextView typeTextView = (TextView) v.findViewById(R.id.restaurantResult_typeTextView);
	    		
	    		Log.i("view","" + typeTextView.getText());
	    		View tv = v.getRootView();
	    		TextView restaurantInfoTextView = (TextView) tv.findViewById(R.id.restaurantInfo_textView);
	    		restaurantInfoTextView.setText(typeTextView.getText());
	    		vflipper.showNext();
	    		}
	    	}));
			*/
			return convertView;
		}
    }
    
    private class ScrollListener implements OnScrollListener {
    	
    	private int visibleThreshold = 0;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;
        
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            	search();
                loading = true;
            }
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
    	
    }
    
    public String getJsonString(String s){
    	URL r; 
    	try {
    	r = new URL (s);
    	return this.getJsonString(r);
    	}catch (Exception e){}
    	return "";
    }
    public String getJsonString(URL url){
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
    public void search(){
    	String str="http://10.0.2.2:8888/restaurant.json";
    	
        try{
            ArrayList<RestaurantResultItem> results = new ArrayList<RestaurantResultItem>();
        	String s = this.getJsonString(str);
	        JSONArray jsa=new JSONArray(s);
            for(int i=0;i<jsa.length();i++) {
               		JSONObject jo=(JSONObject)jsa.get(i);
               		RestaurantResultItem item = new RestaurantResultItem();
               		item.licno = jo.getString("LICNO");
               		item.type = jo.getString("TYPE");
           			item.dist = jo.getString("DIST");
           			item.ss = jo.getString("SS");
           			item.adr = jo.getString("ADR");
           			item.img = jo.getString("IMAGE");
           			item.rating = 1;
           			results.add(item);
	        }
            adapter.addAll(results);
        }
        catch(Exception e){
        	Log.d("exception", e.getMessage());
        }
    	
    }
    
    Bitmap mIcon;
    /*
    public void addRestaurantResultItem(String name, String type, int rating){
    	LayoutInflater inflater = LayoutInflater.from(this);
    	TableRow rowView = (TableRow)inflater.inflate(R.layout.restaurant_search_result_tablerow, null );
    	TableLayout t = (TableLayout)this.findViewById(R.id.searchResultTableLayout);
    	
    	TextView nameTextView = (TextView) rowView.findViewById(R.id.restaurantResult_nameTextView);
    	TextView typeTextView = (TextView) rowView.findViewById(R.id.restaurantResult_typeTextView);
    	TextView ratingTextView = (TextView) rowView.findViewById(R.id.restaurantResult_ratingTextView);
    	TextView slotTextView = (TextView) rowView.findViewById(R.id.restaurantResult_slotTextView);
    	
    	ImageView iv = (ImageView) rowView.findViewById(R.id.restaurantResult_thumbnailImageView);
    	nameTextView.setText(name);
    	typeTextView.setText(type);
    	ratingTextView.setText("" + rating);
    	iv.setImageBitmap(mIcon);
    	
    	rowView.setClickable(true);
    	rowView.setOnClickListener((new View.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			TextView typeTextView = (TextView) v.findViewById(R.id.restaurantResult_typeTextView);
    			//Log.i("view","" + typeTextView.getText());
    			//View tv = v.getRootView();
    			//TextView restaurantInfoTextView = (TextView) tv.findViewById(R.id.restaurantInfo_textView);
    			//restaurantInfoTextView.setText(typeTextView.getText());
    			//vflipper.showNext();
    			
    			Intent intent = new Intent();
    			intent.setClass(MainActivity.this, RestaurantInfoActivity.class);
    			String json = MainActivity.this.getJsonString("http://10.0.2.2:8888/restaurantInfo.json");
    			JSONObject jso;
    			try {
					jso = new JSONObject(json);
	    			intent.putExtra("RESTAURANT_ID", typeTextView.getText());
	    			intent.putExtra("RESTAURANT_NAME", jso.getString("RESTAURANT_NAME"));
	    			intent.putExtra("RESTAURANT_ADDRESS", jso.getString("RESTAURANT_ADDRESS"));
	    			intent.putExtra("RESTAURANT_PHONE", jso.getString("RESTAURANT_PHONE"));
	    			intent.putExtra("RESTAURANT_CUISINE", jso.getString("RESTAURANT_CUISINE"));
	    			intent.putExtra("RESTAURANT_PRICE", jso.getString("RESTAURANT_PRICE"));
	    			intent.putExtra("RESTAURANT_HOURS",jso.getString("RESTAURANT_HOURS") );
	    			intent.putExtra("RESTAURANT_PARKING", jso.getString("RESTAURANT_PARKING"));
	    			intent.putExtra("RESTAURANT_DESCRIPTION", jso.getString("RESTAURANT_DESCRIPTION"));
	    			intent.putExtra("RESTAURANT_MENU", jso.getString("RESTAURANT_MENU"));
	    			intent.putExtra("RESTAURANT_REVIEW_OVERALL", jso.getString("RESTAURANT_REVIEW_OVERALL"));
	    			intent.putExtra("RESTAURANT_REVIEW_FOOD", jso.getString("RESTAURANT_REVIEW_FOOD"));
	    			intent.putExtra("RESTAURANT_REVIEW_SERVICE", jso.getString("RESTAURANT_REVIEW_SERVICE"));
	    			intent.putExtra("RESTAURANT_REVIEW_AMBIENCE", jso.getString("RESTAURANT_REVIEW_AMBIENCE"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    			startActivity(intent);
    			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		    	
    		}
    	}));
    	t.addView(rowView);
    	
    	
    }
    */
    public void resetSearch(){
    	
    }
    public void searchButton_onClick(View view){
    	//timePicker1.setVisibility(View.GONE);
    	resetSearch();
    	search();
    }
    public void timeButton_onClick(View view){
    	//timePicker1.setVisibility(View.GONE);
    	datePicker1.setVisibility(View.GONE);
    	numberSeekBar1.setVisibility(View.GONE);
    	timePicker1.setVisibility(timePicker1.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
    }
    
    public void dateButton_onClick(View view){
    	timePicker1.setVisibility(View.GONE);
    	//datePicker1.setVisibility(View.GONE);
    	numberSeekBar1.setVisibility(View.GONE);
    	datePicker1.setVisibility(datePicker1.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
    }
    
    public void numberButton_onClick(View view){
    	timePicker1.setVisibility(View.GONE);
    	datePicker1.setVisibility(View.GONE);
    	//numberSeekBar1.setVisibility(View.GONE);
    	numberSeekBar1.setVisibility(numberSeekBar1.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
