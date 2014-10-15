package edu.stanford.cs.sing.helena.nodes;


import java.util.ArrayList;

import edu.stanford.cs.sing.helena.R;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CopyOfObservAdapter extends ArrayAdapter<Observation> {

    private static class ViewHolder {
        TextView address;
        TextView lastUpdated;
    }
    
    private ArrayList<Parcelable> mFire;
    

	public CopyOfObservAdapter(Context context, ArrayList<Parcelable> fire) {
	       super(context, R.layout.item_oberver, (ArrayList)fire);
	       mFire=fire;
	      
	}


	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Observation mobservation = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       ViewHolder viewHolder; // view lookup cache stored in tag
       if (convertView == null) {
          viewHolder = new ViewHolder();
          LayoutInflater inflater = LayoutInflater.from(getContext());
          convertView = inflater.inflate(R.layout.item_oberver, parent, false);
          viewHolder.address= (TextView) convertView.findViewById(R.id.observed_addr);
         // viewHolder.number = (TextView) convertView.findViewById(R.id.fire_number);
          viewHolder.lastUpdated = (TextView) convertView.findViewById(R.id.observ_time);
          convertView.setTag(viewHolder);
       } else {
           viewHolder = (ViewHolder) convertView.getTag();
       }
       // Populate the data into the template view using the data object
       viewHolder.address.setText(""+mobservation.observed);
       //viewHolder.number.setText(""+mFire.numberOfObservation());
       viewHolder.lastUpdated.setText("" + mobservation.timestamp);
       // Return the completed view to render on screen
       return convertView;
   }
    

    
}
