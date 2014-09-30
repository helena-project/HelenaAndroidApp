package edu.stanford.cs.sing.helena;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import edu.stanford.cs.sing.helena.nodes.*;

/**
 * A fragment representing a single node detail screen.
 * This fragment is either contained in a {@link nodeListActivity}
 * in two-pane mode (on tablets) or a {@link nodeDetailActivity}
 * on handsets.
 */
public class nodeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Node mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public nodeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = NodeList.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_node_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.node_detail)).setText(mItem.getUUIDList());
        }

        return rootView;
    }
}
