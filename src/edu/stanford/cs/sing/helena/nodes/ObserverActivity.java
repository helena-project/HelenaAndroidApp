/**
 * 
 */
package edu.stanford.cs.sing.helena.nodes;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author lauril
 *
 */
public class ObserverActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Set the user interface layout for this Activity
	    // The layout file is defined in the project res/layout/main_activity.xml file
//	    setContentView(R.layout.observer_view);
//	    ArrayList<Parcelable> ar = getIntent().getParcelableArrayListExtra("firestorm");
//        ObservAdapter mAdapter = new ObservAdapter(this);
//        // Attach the adapter to a ListView
//        ListView listView = (ListView) findViewById(R.id.observation_list);
//        listView.setAdapter(mAdapter);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();  // Always call the superclass
	    
	    // Stop method tracing that the activity started during onCreate()
	    android.os.Debug.stopMethodTracing();
	}

}
