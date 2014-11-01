package edu.stanford.cs.sing.helena.nodes;

import java.sql.Timestamp;

import android.support.v4.util.ArrayMap;
import android.text.format.Time;


public class Firestorm {


	//the Timestamp is additional, local when the data was created
	public ArrayMap<Timestamp, Observation> mObservedDevices; 
	public String id;
	public String lastUpdated;
	private ObservationArray mObservationArray;
	
	public Firestorm (String id){
		this.mObservedDevices = new ArrayMap<Timestamp, Observation>();
		this.mObservationArray = new ObservationArray();
		this.id = id;
		this.lastUpdated = getTimeNow();	
		}

	
	private String getTimeNow(){
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		return today.format("%k:%M:%S");
	}
	

	public void addObservation(byte obs){
		Observation observed = new Observation(obs);
		mObservedDevices.put(new Timestamp(System.currentTimeMillis()), observed);
		mObservationArray.add(observed);
		this.lastUpdated = getTimeNow();	
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
	
	public ObservationArray getObservationList(){
		return this.mObservationArray;
	}
	
}
