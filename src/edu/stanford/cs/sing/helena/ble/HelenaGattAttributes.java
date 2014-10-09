package edu.stanford.cs.sing.helena.ble;

import java.util.HashMap;

public class HelenaGattAttributes {

	private static HashMap<String, String> attributes = new HashMap<String, String>();
	public static final String UUID_LISTED_DEVICE = "00002003-0000-1000-8000-00805f9b34fb";
	public static final String HELENA_SERVICE = "00001978-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    


    static {
        attributes.put(HELENA_SERVICE, "Helena Service");
        attributes.put(UUID_LISTED_DEVICE, "Seen Device");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

}
