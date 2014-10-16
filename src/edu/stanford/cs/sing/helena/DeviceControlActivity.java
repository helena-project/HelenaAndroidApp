/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.stanford.cs.sing.helena;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import edu.stanford.cs.sing.common.helper.ByteWork;
import edu.stanford.cs.sing.helena.ble.BluetoothLeService;
import edu.stanford.cs.sing.helena.ble.HelenaGattAttributes;
import edu.stanford.cs.sing.helena.nodes.FireAdapter;
import edu.stanford.cs.sing.helena.nodes.FireArray;
import edu.stanford.cs.sing.helena.nodes.Firestorm;
import edu.stanford.cs.sing.helena.nodes.ObservAdapter;






/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
	private final static String TAG = DeviceControlActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;

	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattService mHelenaService;
	private OnItemClickListener mFireListOnClickListner;
	private PopupWindow  popWindow;
	public FireArray mFirestormArray;
	private FireAdapter mFireAdapter;
	private ObservAdapter mObserverAdapter;
	private boolean mFireLitDisplay;

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
	//                        or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
				//clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				checkServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				
				dealWithData(intent.getByteArrayExtra(BluetoothLeService.BYTE_DATA));
				
			}
		}
	};

	

	private void dealWithData(byte[] data){
		byte[] device = ByteWork.getBytes(data, 0, 5);
		byte[] observed = ByteWork.getBytes(data, 6, 15);
		StringBuilder str = new StringBuilder(device.length);
		for(byte byteChar : device)
			str.append(String.format("%02X ", byteChar));
		
		mFirestormArray.addDeviceData(str.toString(),observed);
		Log.d(TAG, "Deal with data ");
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFireLitDisplay = false;
		mFirestormArray = new FireArray();
		mFireListOnClickListner= new FireListOnClickListner();
	    setContentView(R.layout.device_control_activity);

		//
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        //mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        // Create the adapter to convert the array to views
        mFireAdapter = new FireAdapter(this, mFirestormArray);
        
        addFireList();
		
		Log.d(TAG, "onCreate bindService");
	}


	public void onBackPressed(){
		Log.d(TAG,"OnBackPress");
		if(mFireLitDisplay){
			super.onBackPressed();
		} else { 
			addFireList();
		}
	}
	
    public void onShowPopup(View v){
    	 
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null,false);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.fire_list);
 
        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x - 50,size.y - 400, true );
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);
 
        // show the popup at bottom of the screen and set some margin at bottom ie,
       // popWindow.showAtLocation(getCurrentFocus(), Gravity.TOP, 0, 100); //showAtLocation(v, Gravity.BOTTOM, 0,100);
        findViewById(R.id.fire_list).post(new Runnable() {
        	   public void run() {
        		   popWindow.showAtLocation(findViewById(R.id.fire_list), Gravity.CENTER, 0, 0);
        	   }
        	});
    }
    
	private void addDetailList(AdapterView<?> parent, View view, int position, long id){
		mFireLitDisplay = false;
		Firestorm mFire = mFirestormArray.get(position); 
		((TextView) findViewById(R.id.popup_header)).setText("Observer: " + mFire.id);
		((TextView) findViewById(R.id.popup_header_columt_1)).setText(R.string.addr);
		((TextView) findViewById(R.id.popup_header_columt_2)).setText(R.string.label_last_seen);

    	mObserverAdapter = new ObservAdapter(
    			view.getContext(), mFire.getObservationList());
    	ListView listView = (ListView) findViewById(R.layout.popup_layout);
        listView.setAdapter(mObserverAdapter);
    	onShowPopup(view);
	}
	
	class FireListOnClickListner implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			addDetailList( parent,  view,  position, id);
        	Log.d(TAG, "onClick " + position);
		}
		
	}
	private void addFireList(){
		if(!mFireLitDisplay){
			Log.d(TAG, "Connected to " + mDeviceAddress );
			mFireLitDisplay = true;
			((TextView) findViewById(R.id.header_columt_1)).setText(R.string.addr);
			((TextView) findViewById(R.id.header_columt_2)).setText(R.string.number);
			((TextView) findViewById(R.id.header_columt_3)).setText(R.string.label_last_seen);
	        // Attach the adapter to a ListView
	        ListView listView = (ListView) findViewById(R.id.fire_list);
	        listView.setAdapter(mFireAdapter);
	        listView.setOnItemClickListener(mFireListOnClickListner);
	        onShowPopup(listView);
			}
	}

	@Override
	protected void onResume() {
		super.onResume();
	
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mDeviceAddress != null){
			switch(item.getItemId()) {
			case R.id.menu_connect:
				mBluetoothLeService.connect(mDeviceAddress);
				return true;
			case R.id.menu_disconnect:
				mBluetoothLeService.disconnect();
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;
			}
		} else {
			Log.d(TAG, "onOptionsItemSelected mDeviceAddress == null " );
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  // good practice to save
		//TODO save the instance
		Log.d(TAG, "onSaveInstanceState");

	  super.onSaveInstanceState(savedInstanceState);  
	}  
	
	    @Override  
	public void onRestoreInstanceState(Bundle savedInstanceState) {  
	  super.onRestoreInstanceState(savedInstanceState);  
	  Log.d(TAG, "onRestoreInstanceState");
	  // Restore UI state from the savedInstanceState.  
	  //TODO: implement restorations of the instance
	    }
	private void checkServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		String uuid;
		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			 uuid = gattService.getUuid().toString();
			 //only interested in helena service
			if(uuid.equals(HelenaGattAttributes.HELENA_SERVICE)){
				mHelenaService = gattService;
				List<BluetoothGattCharacteristic> gattCharacteristics =
						gattService.getCharacteristics();
				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					//only interested in this characteristic
					if(gattCharacteristic.getUuid().toString().equals(HelenaGattAttributes.UUID_LISTED_DEVICE)){
						mNotifyCharacteristic = gattCharacteristic;
						final int charaProp = gattCharacteristic.getProperties();
							if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
								//Initiate notifications
								mNotifyCharacteristic = gattCharacteristic;
								mBluetoothLeService.setCharacteristicNotification(
										gattCharacteristic, true);
							}
							
						}
				}

			} else {
				Log.d(TAG, "Skipping other services");
			}
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}
