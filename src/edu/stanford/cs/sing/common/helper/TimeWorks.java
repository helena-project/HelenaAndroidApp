package edu.stanford.cs.sing.common.helper;

import java.sql.Timestamp;

import android.text.format.Time;

public class TimeWorks {

	public static String timestampToString(Timestamp timestamp , String format) {
		Time today = new Time(Time.getCurrentTimezone());
		today.set(timestamp.getTime());
		return today.format(format);
	}

}
