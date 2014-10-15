package edu.stanford.cs.sing.helena.nodes;

import java.io.Serializable;
import java.util.ArrayList;

import android.support.v4.util.ArrayMap;
import android.util.Log;

public class ObservationArray implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6864943749678836676L;
	public  ArrayList<Observation> mArrayList;
	private ArrayMap<String, Observation> mObservMap;
	private ObservAdapter mObservAdapter;
	
	public ObservationArray(){
		mArrayList = new ArrayList<Observation>();
		mObservMap = new ArrayMap<String, Observation>();
	}
	public void addAddapter(ObservAdapter fa){
		this.mObservAdapter = fa;
	}

	public void addObservation(Observation o){
		mArrayList.add(o);
		if(mObservAdapter != null){
			mObservAdapter.notifyDataSetChanged();
		} else {
			Log.d("ObervationArray", "ObservationAdapter == null");
		}
		
	}
	public void add(Observation observed) {
		addObservation(observed);
		
	}
	


}
