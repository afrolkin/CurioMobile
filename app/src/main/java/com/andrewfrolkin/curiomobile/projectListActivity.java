package com.andrewfrolkin.curiomobile;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.andrewfrolkin.curiomobile.models.Project;
import com.andrewfrolkin.curiomobile.models.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.andrewfrolkin.curiomobile.CurioApi.projectsUrl;
import static com.andrewfrolkin.curiomobile.CurioApi.questionUrl;

public class projectListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private List<Project> projects;
    private List<Question> questions;
    private String baseUrl;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private boolean searched = false;

    private static final String PROJECTS = "projects";
    private static final String QUESTIONS = "questions";
    private static final String SEARCH_STRING = "search_string";
    private static final String SEARCHED = "searched";
    private String searchString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        baseUrl = getResources().getString(R.string.baseUrl);

        recyclerView = (RecyclerView) findViewById(R.id.project_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        assert recyclerView != null;

        if (findViewById(R.id.project_detail_container) != null) {
            mTwoPane = true;
        }

        boolean dataRestored = false;
        if (savedInstanceState != null) {
            projects = savedInstanceState.getParcelableArrayList(PROJECTS);
            questions = savedInstanceState.getParcelableArrayList(QUESTIONS);
            searchString = savedInstanceState.getString(SEARCH_STRING);
            searched = savedInstanceState.getBoolean(SEARCHED);
            restoreData();
            dataRestored = true;
        }

        if (!dataRestored) {
            refreshData();
        }
    }

    private void restoreData() {
        Log.d("TESTING", "RESTORING DATA");
        setupRecyclerView(recyclerView);
        onRefreshComplete();
    }

    @Override
    protected void onSaveInstanceState(Bundle si) {
        // save projects and questions
        // save state of searching
        si.putParcelableArrayList(PROJECTS, new ArrayList<>(projects));
        si.putParcelableArrayList(QUESTIONS, new ArrayList<>(questions));
        si.putString(SEARCH_STRING, searchString);
        si.putBoolean(SEARCHED, searched);

        super.onSaveInstanceState(si);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (searchString != null) {
            searchView.setQuery(searchString, searched);
            searchView.setIconified(false);
            if (searched) {
                searchView.clearFocus();
            }
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProjects(query);
                searched = true;
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchString = newText;
                return false;
            }
        });

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (searched) {
                    refreshData();
                }
                searched = false;
                searchString = null;
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProjects() {
        new GetProjectsTask().execute(baseUrl + "/project/");
    }
    private void getProjectsSearch(String search) {
        new GetProjectsTask().execute(baseUrl + "/project/?description=" + search);
    }
    private void getQuestions() {
        new GetQuestionsTask().execute(baseUrl + "/curio/");
    }

    public void refreshData() {
        Log.d("TESTING", "REFRESHING DATA");
        if (recyclerView.getAdapter() != null) {
            ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).clearData();
        }
        swipeRefreshLayout.setRefreshing(true);
        // this will call getQuestions when it finishes
        getProjects();
    }

    public void searchProjects(String search) {
        if (recyclerView.getAdapter() != null) {
            ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).clearData();
        }
        swipeRefreshLayout.setRefreshing(true);
        // this will call getQuestions when it finishes
        getProjectsSearch(search);
    }

    private class GetProjectsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                projects = projectsUrl(urls[0]);
                Log.d("TESTING", "PROJECTS SUCCESS");
                return "success";
            } catch (IOException e) {
                Log.d("TESTING", "PROJECTS " + e.toString());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    getQuestions();
                    setupRecyclerView(recyclerView);
                }
            }, 400);
        }
    }

    private List<Question> getQuestionsWithId(int id) {
        List<Question> ret = new ArrayList<>();
        for (Question q : questions) {
            if (q.project == id) {
                ret.add(q);
            }
        }
        return ret;
    }

    private class GetQuestionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                questions = questionUrl(urls[0]);
                return "success";
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            onRefreshComplete();
        }
    }

    private void onRefreshComplete() {
        recyclerView.getAdapter().notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

        if (projects.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(projects));
        } else {
            ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).dataChanged(projects);
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Project> mValues;

        public SimpleItemRecyclerViewAdapter(List<Project> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView view = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.project_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).name);
            holder.mDescriptionView.setText(mValues.get(position).short_description);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, projectDetailActivity.class);
                    intent.putExtra(projectDetailFragment.ARG_ITEM_ID, holder.mItem);
                    intent.putExtra(projectDetailFragment.ARG_QUESTIONS_ID, new ArrayList<>(getQuestionsWithId(holder.mItem.id)));

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView mView;
            public final TextView mTitleView;
            public final TextView mDescriptionView;
            public final View contentView;
            public Project mItem;

            public ViewHolder(CardView view) {
                super(view);
                mView = view;
                contentView = view.findViewById(R.id.card_content);
                mTitleView = (TextView) view.findViewById(R.id.title);
                mDescriptionView = (TextView) view.findViewById(R.id.description);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDescriptionView.getText() + "'";
            }
        }

        public void clearData() {
            int size = this.mValues.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.mValues.remove(0);
                }
                this.notifyItemRangeRemoved(0, size);
            }
        }

        public void dataChanged(List<Project> p) {
            mValues = p;
            notifyDataSetChanged();
        }
    }
}
