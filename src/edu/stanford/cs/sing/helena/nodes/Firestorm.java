package edu.stanford.cs.sing.helena.nodes;

import java.sql.Timestamp;


import android.support.v4.util.ArrayMap;


public class Firestorm {


	//the Timestamp is additional, local when the data was created
	private ArrayMap<Timestamp, Observation> mObservedDevices; 
	public String id;
	public String content;
	
	public Firestorm (String id){
		this.mObservedDevices = new ArrayMap<Timestamp, Observation>();
		this.id = id;
		this.content = "yes yes yes"
;	}
	public Firestorm () {
		this("default");
	}
	
	public void addObservation(Observation observed) {
		mObservedDevices.put(new Timestamp(System.currentTimeMillis()), observed);
		
	}
	public void addObservation(byte[] observed){
		mObservedDevices.put(new Timestamp(System.currentTimeMillis()), 
				new Observation(observed));
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
	
	public String getUUIDList() {
    	String ret = "test";

		return ret;
	}
}
