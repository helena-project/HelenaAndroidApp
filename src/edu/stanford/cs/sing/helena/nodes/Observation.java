package edu.stanford.cs.sing.helena.nodes;
import edu.stanford.cs.sing.common.helper.TimeWorks;


public class Observation {
	public String observed;
	public String mSeenTime;
	public String mManufaturer="None";
	public int manufacturerInt=0;
	
	public Observation(byte data){
		this.observed = String.valueOf(data);
		this.mSeenTime = TimeWorks.getCurrentTimeStamp();
	}
			
	public String getObserved(){
		return observed;
	}
	

	private String makeObserved(byte data){		
		return String.valueOf(data);
	}

}
