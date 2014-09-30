package edu.stanford.cs.sing.helena.nodes;

import java.util.ArrayList;
import java.util.UUID;

public class Node {
    public String id;
    public String content;
    private ArrayList<String> mSeenUIDS;

    public Node(String id, String content) {
        this.id = id;
        this.content = content;
        this.mSeenUIDS = new ArrayList<String>();
        for(int i=0; i<5; i++){
        	this.mSeenUIDS.add( UUID.randomUUID().toString() );
        }
    }

    public void uppDateSeen(String seenuid){
    	mSeenUIDS.add(seenuid);
    }
    @Override
    public String toString() {

        return content;
    }

	public String getUUIDList() {
    	String ret = "";
    	int i=1;
    	for (String s : this.mSeenUIDS){
    		ret+= String.valueOf(i) + ' ' + s + '\n';
    		i++;
    	}
		return ret;
	}

}
