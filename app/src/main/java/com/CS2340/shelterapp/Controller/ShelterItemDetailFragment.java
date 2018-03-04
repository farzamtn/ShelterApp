package com.CS2340.shelterapp.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;

/**
 * A fragment representing a single ShelterItem detail screen.
 * This fragment is either contained in a {@link ShelterItemListActivity}
 * in two-pane mode (on tablets) or a {@link ShelterItemDetailActivity}
 * on handsets.
 *
 * @author Farzam
 * @version 1.0
 */
public class ShelterItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private ShelterData mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShelterItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Shelters.INSTANCE.findItemById(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shelteritem_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.shelterName)).setText(String.format("Name: %s", mItem.getName()));
            ((TextView) rootView.findViewById(R.id.shelterCapacity)).setText(String.format("Capacity: %s", mItem.getCapacity()));
            ((TextView) rootView.findViewById(R.id.shelterResriction)).setText(String.format("Restrictions: %s", mItem.getRestrictions()));
            ((TextView) rootView.findViewById(R.id.shelterLongitude)).setText(String.format("Longitude: %s", mItem.getLongitude()));
            ((TextView) rootView.findViewById(R.id.shelterLatitude)).setText(String.format("Latitude: %s", mItem.getLatitude()));
            ((TextView) rootView.findViewById(R.id.shelterAddress)).setText(String.format("Address: %s", mItem.getAddress()));
            ((TextView) rootView.findViewById(R.id.shelterNotes)).setText(String.format("Special Notes: %s", mItem.getSpecialNotes()));
            ((TextView) rootView.findViewById(R.id.shelterPhoneNumber)).setText(String.format("Phone Number: %s", mItem.getPhoneNumber()));
        }

        return rootView;
    }
}
