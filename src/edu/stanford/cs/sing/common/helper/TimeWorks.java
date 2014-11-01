package edu.stanford.cs.sing.common.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.Time;

public class TimeWorks {

	public static String timestampToString(Timestamp timestamp , String format) {
		Time today = new Time(Time.getCurrentTimezone());
		today.set(timestamp.getTime());
		return today.format(format);
	}

	/**
	 * 
	 * @return HH:mm:ss formate date as string
	 */
	public static String getCurrentTimeStamp(){
	    try {

	        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	        String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

	        return currentTimeStamp;
	    } catch (Exception e) {
	        e.printStackTrace();

	        return null;
	    }
	}
}
