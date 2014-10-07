package edu.stanford.cs.sing.helena;

import java.util.Observable;
import java.sql.Timestamp;

import android.support.v4.util.ArrayMap;


public class Firestorm extends Observable {

	private String mMAC;
	//the Timestamp is additional, local when the data was created
	private ArrayMap<Timestamp, Observation> mObservedDevices; 
	
	public Firestorm ( String mac) {
		this.mMAC = mac;
		this.mObservedDevices = new ArrayMap<Timestamp, Observation>();
	}
	
	public void addObservation(Observation observed) {
		mObservedDevices.put(new Timestamp(System.currentTimeMillis()), observed);
		notifyObservers(observed);
	}
	
	public void removeObservation(Observation observation){
		mObservedDevices.remove(observation);
	}
}
