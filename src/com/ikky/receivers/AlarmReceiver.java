package com.ikky.receivers;

import com.ikky.activities.R;
import com.ikky.activities.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.app.*;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	 public void onReceive(Context context, Intent intent) {
	   try {
	     Toast.makeText(context, "Alarm received", Toast.LENGTH_LONG).show();
	     Bundle extras = intent.getExtras();
		 Log.d("bookingID", extras.getString("bookingID"));
	     buildNotification(context, extras.getString("bookingID"));
	    } catch (Exception e) {
	     Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
	     e.printStackTrace();
	 
	    }
	 }
	
	private void buildNotification(Context context, String key){
		  NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		  Notification.Builder builder = new Notification.Builder(context);
		  Intent intent = new Intent(context, MainActivity.class);
		  intent.putExtra("source", "notification");
		  intent.putExtra("notificationType", "bookingNotification");
		  intent.putExtra("bookingID", key);
		  intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		  Log.d("bookingID", key);
		  PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		  builder.setSmallIcon(R.drawable.ic_launcher)
		  .setContentTitle("Booking Reminder")
		  .setContentText("You have a booking in 15 mins")
		  .setContentInfo("")
		  .setTicker("Ikky")
		  .setLights(0xFFFF0000, 500, 500) //setLights (int argb, int onMs, int offMs)
		  .setContentIntent(pendingIntent)
		  .setAutoCancel(true);
		  
		  Notification notification = builder.getNotification();
		  notification.flags |= Notification.FLAG_AUTO_CANCEL;
		  notificationManager.notify(R.drawable.ic_launcher, notification);
		 }
}
