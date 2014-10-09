package edu.stanford.cs.sing.helena.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * 
 */
public class NodeList {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Firestorm> ITEMS = new ArrayList<Firestorm>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static HashMap<String, Firestorm> ITEM_MAP = new HashMap<String, Firestorm>();

    


    public static void addItem(Firestorm item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void addItem(Firestorm item, byte[] data) {
    	addItem(item);
        item.addObservation(data);
    }
    
    public static Firestorm addItem(String item, byte[] data) {
    	Firestorm mFire = ITEM_MAP.get(item);
		if (mFire != null){
			mFire.addObservation(data);
		} else {
			mFire = new Firestorm(item);
			mFire.addObservation(data);
			addItem(mFire);
		}
		return mFire;
    }
}
