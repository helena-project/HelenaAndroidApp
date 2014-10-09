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

import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import edu.stanford.cs.sing.common.helper.ByteWork;
import edu.stanford.cs.sing.helena.ble.BluetoothLeService;
import edu.stanford.cs.sing.helena.ble.HelenaGattAttributes;
import edu.stanford.cs.sing.helena.nodes.Firestorm;
import edu.stanford.cs.sing.helena.nodes.NodeList;




/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity 
				implements nodeListFragment.Callbacks{
	private final static String TAG = DeviceControlActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private TextView mDataField;
	private String mDeviceName;
	private String mDeviceAddress;
	private ExpandableListView mGattServicesList;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
			new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private nodeDetailFragment nodeDetailFragment;
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	
	public static Bus mMessageBus;



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
				displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				
				dealWithData(intent.getByteArrayExtra(BluetoothLeService.BYTE_DATA));
				
			}
		}
	};

	private boolean mTwoPane;


	private void dealWithData(byte[] data){
		byte[] device = ByteWork.getBytes(data, 0, 5);
		byte[] observed = ByteWork.getBytes(data, 6, 15);
		StringBuilder str = new StringBuilder(device.length);
		for(byte byteChar : device)
			str.append(String.format("%02X ", byteChar));
		Firestorm mFire = NodeList.addItem(str.toString(), observed);
		BusProvider.getInstance().post(new NodeListUpdatedEvent());

		Log.d(TAG, "Deal with data ");
	}

	// If a given GATT characteristic is selected, check for supported features.  This sample
	// demonstrates 'Read' and 'Notify' features.  See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
	// list of supported characteristic features.
//	private final ExpandableListView.OnChildClickListener servicesListClickListner =
//			new ExpandableListView.OnChildClickListener() {
//		@Override
//		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
//				int childPosition, long id) {
//			if (mGattCharacteristics != null) {
//
//				final BluetoothGattCharacteristic characteristic =
//						mGattCharacteristics.get(groupPosition).get(childPosition);
//				final int charaProp = characteristic.getProperties();
//				Log.d(TAG, "onChildClick: " + String.valueOf(charaProp));
//				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//					// If there is an active notification on a characteristic, clear
//					// it first so it doesn't update the data field on the user interface.
//					if (mNotifyCharacteristic != null) {
//						mBluetoothLeService.setCharacteristicNotification(
//								mNotifyCharacteristic, false);
//						mNotifyCharacteristic = null;
//					}
//					mBluetoothLeService.readCharacteristic(characteristic);
//				}
//				if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//					mNotifyCharacteristic = characteristic;
//					mBluetoothLeService.setCharacteristicNotification(
//							characteristic, true);
//				}
//				return true;
//			}
//			return false;
//		}
//	};

	private void clearUI() {
		mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		//mDataField.setText(R.string.no_data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BusProvider.getInstance().register(this);
	      setContentView(R.layout.activity_node_list);

	        if (findViewById(R.id.node_detail_container) != null) {
	            // The detail container view will be present only in the
	            // large-screen layouts (res/values-large and
	            // res/values-sw600dp). If this view is present, then the
	            // activity should be in two-pane mode.
	            mTwoPane = true;

	            // In two-pane mode, list items should be given the
	            // 'activated' state when touched.
	            ((nodeListFragment) getFragmentManager()
	                    .findFragmentById(R.id.node_list))
	                    .setActivateOnItemClick(true);
	        }
		//
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		//
		//        // Sets up UI references.
//		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//		mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
//		//mGattServicesList.setOnChildClickListener(servicesListClickListner);
//		mConnectionState = (TextView) findViewById(R.id.connection_state);
//		mDataField = (TextView) findViewById(R.id.data_value);
		Log.d(TAG, "Connected to " + mDeviceAddress );
		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		Log.d(TAG, "onCreate bindService");
	}

    /**
     * Callback method from {@link nodeListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(nodeDetailFragment.ARG_ITEM_ID, id);
            nodeDetailFragment = new nodeDetailFragment();
            nodeDetailFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.node_detail_container, nodeDetailFragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, nodeDetailActivity.class);
            detailIntent.putExtra(nodeDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
	@Override
	protected void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
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
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				mConnectionState.setText(resourceId);
//			}
//		});
	}

	private void displayData(String data) {
		if (data != null) {
			mDataField.setText(data);
		}
	}

	// Demonstrates how to iterate through the supported GATT Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the ExpandableListView
	// on the UI.
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		String uuid = null;
		String unknownServiceString = getResources().getString(R.string.unknown_service);
		String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
		= new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			uuid = gattService.getUuid().toString();
			if(uuid.equals(HelenaGattAttributes.HELENA_SERVICE)){


				HashMap<String, String> currentServiceData = new HashMap<String, String>();
				uuid = gattService.getUuid().toString();
				currentServiceData.put(
						LIST_NAME, HelenaGattAttributes.lookup(uuid, unknownServiceString));
				currentServiceData.put(LIST_UUID, uuid);
				gattServiceData.add(currentServiceData);

				ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
						new ArrayList<HashMap<String, String>>();
				List<BluetoothGattCharacteristic> gattCharacteristics =
						gattService.getCharacteristics();
			
				ArrayList<BluetoothGattCharacteristic> charas =
						new ArrayList<BluetoothGattCharacteristic>();

				// Loops through available Characteristics.
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if(gattCharacteristic.getUuid().toString().equals(HelenaGattAttributes.UUID_LISTED_DEVICE)){
						final int charaProp = gattCharacteristic.getProperties();
							if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
								mNotifyCharacteristic = gattCharacteristic;
								mBluetoothLeService.setCharacteristicNotification(
										gattCharacteristic, true);
							}
							
						}
					charas.add(gattCharacteristic);
					HashMap<String, String> currentCharaData = new HashMap<String, String>();
					uuid = gattCharacteristic.getUuid().toString();
					currentCharaData.put(
							LIST_NAME, HelenaGattAttributes.lookup(uuid, unknownCharaString));
					currentCharaData.put(LIST_UUID, uuid);
					gattCharacteristicGroupData.add(currentCharaData);
				}
				mGattCharacteristics.add(charas);
				gattCharacteristicData.add(gattCharacteristicGroupData);
			} else {
				Log.d(TAG, "Skipping other services");
			}
		}

//		SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//				this,
//				gattServiceData,
//				android.R.layout.simple_expandable_list_item_2,
//				new String[] {LIST_NAME, LIST_UUID},
//				new int[] { android.R.id.text1, android.R.id.text2 },
//				gattCharacteristicData,
//				android.R.layout.simple_expandable_list_item_2,
//				new String[] {LIST_NAME, LIST_UUID},
//				new int[] { android.R.id.text1, android.R.id.text2 }
//				);
//		mGattServicesList.setAdapter(gattServiceAdapter);
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
