package edu.stanford.cs.sing.helena.nodes;

import java.sql.Timestamp;


import android.support.v4.util.ArrayMap;
import android.text.format.Time;


public class Firestorm {


	//the Timestamp is additional, local when the data was created
	private ArrayMap<Timestamp, Observation> mObservedDevices; 
	public String id;
	public String lastUpdated;
	
	public Firestorm (String id){
		this.mObservedDevices = new ArrayMap<Timestamp, Observation>();
		this.id = id;
		this.lastUpdated = getTimeNow();	
		}
	public Firestorm () {
		this("default");
	}
	
	private String getTimeNow(){
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		return today.format("%k:%M:%S");
	}
	public void addObservation(Observation observed) {
		mObservedDevices.put(new Timestamp(System.currentTimeMillis()), observed);
		this.lastUpdated = getTimeNow();	
	}
	public void addObservation(byte[] observed){
		addObservation(new Observation(observed));
		
	}
	public void removeObservation(Observation observation){
		mObservedDevices.remove(observation);
	}
	
	public int numberOfObservation(){
		return mObservedDevices.size();
	}
	
	public String toString(){
		return this.id;
	}
	
}
