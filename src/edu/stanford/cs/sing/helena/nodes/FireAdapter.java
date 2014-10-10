package edu.stanford.cs.sing.helena.nodes;




import edu.stanford.cs.sing.helena.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FireAdapter extends ArrayAdapter<Firestorm> {
	 // View lookup cache
    private static class ViewHolder {
        TextView address;
        TextView number;
        TextView lastUpdated;
    }
    
    private FireArray mFire;
    

	public FireAdapter(Context context, FireArray fire) {
	       super(context, R.layout.item_firestorm, fire.mArrayList);
	       mFire=fire;
	       mFire.addAddapter(this);
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Firestorm mFire = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       ViewHolder viewHolder; // view lookup cache stored in tag
       if (convertView == null) {
          viewHolder = new ViewHolder();
          LayoutInflater inflater = LayoutInflater.from(getContext());
          convertView = inflater.inflate(R.layout.item_firestorm, parent, false);
          viewHolder.address= (TextView) convertView.findViewById(R.id.fire_adress);
          viewHolder.number = (TextView) convertView.findViewById(R.id.fire_number);
          viewHolder.lastUpdated = (TextView) convertView.findViewById(R.id.last_updated);
          convertView.setTag(viewHolder);
       } else {
           viewHolder = (ViewHolder) convertView.getTag();
       }
       // Populate the data into the template view using the data object
       viewHolder.address.setText(""+mFire.id);
       viewHolder.number.setText(""+mFire.numberOfObservation());
       viewHolder.lastUpdated.setText("" + mFire.lastUpdated);
       // Return the completed view to render on screen
       return convertView;
   }

}
