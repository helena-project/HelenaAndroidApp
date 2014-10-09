package edu.stanford.cs.sing.helena.nodes;
import java.sql.Timestamp;

import edu.stanford.cs.sing.common.helper.ByteWork;

public class Observation {
	public String observed;
	public Timestamp timestamp;
	
	public Observation(byte[] data){
		this.observed = makeObserved(data);
		this.timestamp = makeTimestamp(
				(long)ByteWork.convertFourUnsignedBytesToLong(
						ByteWork.getBytes(data,4,7))
						);
				
	}
	
	
	public String getObserved(){
		return observed;
	}
	
	public Timestamp getTimestamp(){
		return timestamp;
	}
	
	private Timestamp makeTimestamp(long data){
		return new Timestamp(data);
	}
	
	private String makeObserved(byte[] d){
		byte [] data = ByteWork.getBytes(d, 0, 3);
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		for(byte byteChar : data)
			stringBuilder.append(String.format("%02X ", byteChar));
		
		return stringBuilder.toString();
	}

}
