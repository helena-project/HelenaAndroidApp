package edu.stanford.cs.sing.helena.nodes;

import java.util.ArrayList;


import android.support.v4.util.ArrayMap;
import android.text.format.Time;

public class FireArray {

	public  ArrayList<Firestorm> mArrayList;
	private ArrayMap<String, Firestorm> mFireMap;
	private FireAdapter mFireAdapter;
	
	public FireArray(){
		mArrayList = new ArrayList<Firestorm>();
		mFireMap = new ArrayMap<String, Firestorm>();
	}

	public void addAddapter(FireAdapter fa){
		this.mFireAdapter = fa;
	}
	private void updateAdapter(Firestorm f){
		mFireAdapter.add(f);
	}

	public void addDeviceData(String key, byte[] data){
		
		
		if(mFireMap.containsKey(key)){
			mFireMap.get(key).addObservation(data);
			

		} else {
			Firestorm fire = new Firestorm(key);
			fire.addObservation(data);
			mArrayList.add(fire);
			mFireMap.put(key, fire);
			
			}
		mFireAdapter.notifyDataSetChanged();
	}
}
