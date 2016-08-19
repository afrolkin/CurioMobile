package com.andrewfrolkin.curiomobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewfrolkin.curiomobile.models.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewfrolkin on 2016-07-14.
 */
public class projectResearchQuestionsFragment extends Fragment {
    /**
     * A fragment representing a single project detail screen.
     * This fragment is either contained in a {@link projectListActivity}
     * in two-pane mode (on tablets) or a {@link projectDetailActivity}
     * on handsets.
     */
        public static final String ARG_ITEM_ID = "item_id";

        /**
         * The dummy content this fragment is presenting.
         */
        private ArrayList<Question> questions;

        private String name;
        private int id;
        private RecyclerView recyclerView;

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public projectResearchQuestionsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d("TESTING QUESTIONS", "ONCREATEVIEW");
            if (getArguments().containsKey(projectDetailFragment.ARG_QUESTIONS_ID)) {
                questions = getArguments().getParcelableArrayList(projectDetailFragment.ARG_QUESTIONS_ID);
                if (questions == null) {
                    Log.d("TESTING QUESTIONS", "NULL");
                    //questions = new ArrayList<>();
                } else {
                    Log.d("TESTING QUESTIONS", "NOT NULL " + questions.size());
                }
            }

            View rootView = inflater.inflate(R.layout.project_research_questions, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.questions_list);
            setupRecyclerView(recyclerView);

            if (questions.size() == 0) {
                Log.d("TESTING QUESTIONS", "" + questions.size());
                recyclerView.setVisibility(View.GONE);
                rootView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            }
            return rootView;
        }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null ) {
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new QuestionsRecyclerViewAdapter(questions));
        }
    }

    public class QuestionsRecyclerViewAdapter
            extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder> {

        private final List<Question> mValues;

        public QuestionsRecyclerViewAdapter(List<Question> items) {
            mValues = items;
        }

        @Override
        public QuestionsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView view = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contribute_list_content, parent, false);
            return new QuestionsRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final QuestionsRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).question);
            holder.mDescriptionView.setText(mValues.get(position).motivaton);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView mView;
            public final TextView mTitleView;
            public final TextView mDescriptionView;
            public Question mItem;

            public ViewHolder(CardView view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mDescriptionView = (TextView) view.findViewById(R.id.description);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDescriptionView.getText() + "'";
            }
        }
    }
    }
