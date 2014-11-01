package edu.stanford.cs.sing.helena.nodes;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.stanford.cs.sing.helena.R;

public class ObservAdapter extends ArrayAdapter<Observation> {

    private static class ViewHolder {
        TextView address;
        TextView lastUpdated;
        TextView manufacturer;
    }
    
    private ObservationArray mObservation;
    

	public ObservAdapter(Context context, ObservationArray obsarr) {
	       super(context, R.layout.item_oberver, obsarr.mArrayList);
	       this.mObservation=obsarr;
	       this.mObservation.addAddapter(this);
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
         // 
          viewHolder.lastUpdated = (TextView) convertView.findViewById(R.id.observ_time);
          //viewHolder.manufacturer = (TextView) convertView.findViewById(R.id.manufacturer);
          convertView.setTag(viewHolder);
       } else {
           viewHolder = (ViewHolder) convertView.getTag();
       }
       // Populate the data into the template view using the data object
       viewHolder.address.setText(""+mobservation.observed);
       //viewHolder.number.setText(""+mFire.numberOfObservation());
       viewHolder.lastUpdated.setText("" + mobservation.mSeenTime);
       //viewHolder.manufacturer.setText("" + mobservation.mManufaturer);
       // Return the completed view to render on screen
       
       return convertView;
   }
    

    
}
