package com.ikky.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ikky.managers.SearchData;

public class Utils {
	
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
		if (targetDate.get(Calendar.DATE) == today.get(Calendar.DATE) && targetDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) && targetDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
			infoStr = " today";
		} else if (targetDate.get(Calendar.DATE) == tmr.get(Calendar.DATE) && targetDate.get(Calendar.MONTH) == tmr.get(Calendar.MONTH) && targetDate.get(Calendar.YEAR) == tmr.get(Calendar.YEAR)) {
			infoStr = " tomorrow";
		} else if (oneWeek.get(Calendar.DATE) - targetDate.get(Calendar.DATE) > 0 && targetDate.get(Calendar.MONTH) == oneWeek.get(Calendar.MONTH) && targetDate.get(Calendar.YEAR) == oneWeek.get(Calendar.YEAR)) {
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
		if (targetDate.get(Calendar.DATE) == today.get(Calendar.DATE) && targetDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) && targetDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
			infoStr = "today";
		} else if (targetDate.get(Calendar.DATE) == tmr.get(Calendar.DATE) && targetDate.get(Calendar.MONTH) == tmr.get(Calendar.MONTH) && targetDate.get(Calendar.YEAR) == tmr.get(Calendar.YEAR)) {
			infoStr = "tomorrow";
		} else if (oneWeek.get(Calendar.DATE) - targetDate.get(Calendar.DATE) > 0 && targetDate.get(Calendar.MONTH) == oneWeek.get(Calendar.MONTH) && targetDate.get(Calendar.YEAR) == oneWeek.get(Calendar.YEAR)) {
			infoStr = new SimpleDateFormat("EEEEEEE").format(date);
		}
		return "Table for " + noOfParticipants + ", " + infoStr + " at " + new SimpleDateFormat("h:mm aa").format(date).toLowerCase();
	}
	
}
