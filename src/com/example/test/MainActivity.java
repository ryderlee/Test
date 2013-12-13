package com.example.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.DatePicker.*;
import android.widget.TimePicker.*;
import android.graphics.*;
import android.util.Log;

import java.io.*;
import java.net.*;

import org.json.*;

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
		
		
    }

    public void search(){
    	String str="http://10.0.2.2:8888/restaurant?keyword=&page=0";
    	String type, dist, licno, ss, adr;
    	
    	try{
        	URL req = new URL( "http://giverny.org/hotels/corniche/restaurant-room.jpg");
        		
		mIcon= BitmapFactory.decodeStream(req.openConnection().getInputStream());
        }
        catch(MalformedURLException mfe){}
        catch(IOException ioe){}	
    	
        try{
            URL url=new URL(str);
            URLConnection urlc=url.openConnection();
            BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=bfr.readLine())!=null)
            {
            	sb.append(line + "\n");
            }
	        JSONArray jsa=new JSONArray(sb.toString());
            for(int i=0;i<jsa.length();i++) {
	               JSONObject jo=(JSONObject)jsa.get(i);
               			licno= jo.getString("LICNO");
               			type= jo.getString("TYPE");
               			dist= jo.getString("DIST");
               			ss= jo.getString("SS");
               			adr= jo.getString("ADR");
               			
				        this.addRestaurantResultItem(ss, licno , 1);
                        //    title=jo.getString("deal_title");  //tag name "deal_title",will return value that we save in title string
                        //des=jo.getString("deal_description");
               			Log.i("search",type);
	        }
        }
        catch(Exception e){
        	Log.d("exception", e.getMessage());
        }
    	
    }
    
    Bitmap mIcon;
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
    			
    			Log.i("view","" + typeTextView.getText());
    			View tv = v.getRootView();
    			TextView restaurantInfoTextView = (TextView) tv.findViewById(R.id.restaurantInfo_textView);
    			restaurantInfoTextView.setText(typeTextView.getText());
    			vflipper.showNext();
		    	
    		}
    	}));
    	t.addView(rowView);
    	
    	
    }
    
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