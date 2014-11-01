package edu.stanford.cs.sing.helena.nodes;

import java.io.Serializable;
import java.util.ArrayList;

import android.support.v4.util.ArrayMap;


public class FireArray implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6784177937875749632L;
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
	


	public void addDeviceData(String key, byte data){

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
	
	public Firestorm get(int possition){
		return mArrayList.get(possition);
	}
	
}
