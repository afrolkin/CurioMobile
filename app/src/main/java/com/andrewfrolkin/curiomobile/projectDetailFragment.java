package com.andrewfrolkin.curiomobile;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrewfrolkin.curiomobile.models.Project;

/**
 * A fragment representing a single project detail screen.
 * This fragment is either contained in a {@link projectListActivity}
 * in two-pane mode (on tablets) or a {@link projectDetailActivity}
 * on handsets.
 */
public class projectDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_QUESTIONS_ID = "questions_id";


    /**
     * The dummy content this fragment is presenting.
     */
    private Project mItem;

    private String name;
    private int id;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public projectDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getParcelable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.name);
            }
        }

        View rootView = inflater.inflate(R.layout.project_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.project_detail)).setText(mItem.description);

            if (mItem.description == null || mItem.description.equals("")) {
                ((TextView) rootView.findViewById(R.id.project_detail)).setVisibility(View.GONE);
                ((LinearLayout) rootView.findViewById(R.id.empty_view)).setVisibility(View.VISIBLE);
            }

        }

        return rootView;
    }
}
