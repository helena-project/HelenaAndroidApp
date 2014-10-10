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
import java.util.HashMap;
import java.util.List;

import com.squareup.otto.Bus;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import edu.stanford.cs.sing.common.helper.ByteWork;
import edu.stanford.cs.sing.helena.ble.BluetoothLeService;
import edu.stanford.cs.sing.helena.ble.HelenaGattAttributes;
import edu.stanford.cs.sing.helena.nodes.FireAdapter;
import edu.stanford.cs.sing.helena.nodes.FireArray;






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

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	
	public static Bus mMessageBus;
	private FireArray mFirestorsm;
	private FireAdapter mAdapter;


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
		
		mFirestorsm.addDeviceData(str.toString(),observed);
		Log.d(TAG, "Deal with data ");
	}


	private void clearUI() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFirestorsm = new FireArray();
	    setContentView(R.layout.device_control_activity);

		//
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		Log.d(TAG, "Connected to " + mDeviceAddress );
		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        //mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		addList();

		Log.d(TAG, "onCreate bindService");
	}

	private void addList(){
        // Create the adapter to convert the array to views
        mAdapter = new FireAdapter(this, mFirestorsm);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.fire_list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            	Log.d(TAG, "onClick" + position);
//                Intent i = new Intent(More.this, NextActvity.class);
//               //If you wanna send any data to nextActicity.class you can use
//                 i.putExtra(String key, value.get(position));
//
//            startActivity(i);
            }
          });
    

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



	private void checkServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		String uuid;
		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			 uuid = gattService.getUuid().toString();
			if(uuid.equals(HelenaGattAttributes.HELENA_SERVICE)){
				mHelenaService = gattService;
				List<BluetoothGattCharacteristic> gattCharacteristics =
						gattService.getCharacteristics();
				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if(gattCharacteristic.getUuid().toString().equals(HelenaGattAttributes.UUID_LISTED_DEVICE)){
						mNotifyCharacteristic = gattCharacteristic;
						final int charaProp = gattCharacteristic.getProperties();
							if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
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
//		ListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 }
//        );
//        mGattServicesList.setAdapter(gattServiceAdapter);

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
