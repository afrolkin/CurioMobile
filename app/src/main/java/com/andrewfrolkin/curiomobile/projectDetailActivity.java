package com.andrewfrolkin.curiomobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.andrewfrolkin.curiomobile.models.Project;
import com.andrewfrolkin.curiomobile.models.Question;
import com.andrewfrolkin.curiomobile.models.Team;
import com.andrewfrolkin.curiomobile.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.andrewfrolkin.curiomobile.CurioApi.projectUrl;
import static com.andrewfrolkin.curiomobile.CurioApi.questionUrl;
import static com.andrewfrolkin.curiomobile.CurioApi.userUrl;
import static com.andrewfrolkin.curiomobile.R.string.baseUrl;

/**
 * An activity representing a single project detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link projectListActivity}.
 */
public class projectDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;
    private Project project;
    private ArrayList<Question> questions;
    private ArrayList<User> users;
    private projectTeamFragment teamFragment;
    private projectResearchQuestionsFragment questionsFragment;
    private projectDetailFragment detailFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView backdrop;
    private CollapsingToolbarLayout collapsingToolbar;
    private int numUsers = 0;
    private int fetchedUsers = 0;
    private boolean initialLoad = true;
    private ViewPagerAdapter adapter;

    private static final String PROJECT = "project";
    private static final String QUESTIONS = "questions";
    private boolean rotated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        backdrop = (ImageView) findViewById(R.id.backdrop);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            if (getIntent() != null) {
                project = getIntent().getParcelableExtra(projectDetailFragment.ARG_ITEM_ID);
                questions = getIntent().getParcelableArrayListExtra(projectDetailFragment.ARG_QUESTIONS_ID);
            }
        } else {
            project = savedInstanceState.getParcelable(PROJECT);
            questions = savedInstanceState.getParcelableArrayList(QUESTIONS);
            rotated = true;
        }

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitleEnabled(false);
        toolbar.setTitle(project.name);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.project_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        users = new ArrayList<>();

        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        refreshData();
    }

    @Override
    protected void onSaveInstanceState(Bundle si) {
        // save projects and questions
        si.putParcelable(PROJECT, project);
        si.putParcelableArrayList(QUESTIONS, questions);

        super.onSaveInstanceState(si);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        if (initialLoad) {
            // setup all fragments with info (detail + research)
            // get users for project
            // this gets all users after completion
            getBannerImage();
        } else {
            // this will get questions and download image after completion
            getProject(project.id);
        }
    }

    private void onFinishRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                //teamFragment.dataChanged(users);
                if (initialLoad) {
                    setupViewPager(viewPager);
                    tabLayout.setupWithViewPager(viewPager);
                    initialLoad = false;
                } else {
                    /*
                    users.add(new User(4,"andrew", "andrew", "andrew", "https://pbs.twimg.com/profile_images/569208018844127232/LrFxEvoT.jpeg", "andrew", "andrew"));
                    project.description = "testing";
                    questions.add(new Question(1, "andrew", "andrew", "andrew" , "andrew", 3));
                    */
                    adapter.refreshViewPager();
                }

                // hack to fix questions rotation bug
                if (rotated) {
                    //getQuestions();
                    rotated = false;
                    rotated = false;
                    onFinishRefresh();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 400);

    }

    private void getTeam() {
        users.clear();
        if (project.team != null) {
            numUsers = project.team.size();
            Log.d("TESTING", "NUM USERS " + numUsers);
            if (numUsers == 0) {
                onFinishRefresh();
            } else {
                fetchedUsers = 0;
                for (Team t : project.team) {
                    Log.d("TESTING", "Getting user for id " + t.id);
                    getUser(t.id);
                }
            }
        } else {
            onFinishRefresh();
        }
    }

    private void getUser(int id) {
        new GetUserTask().execute(getResources().getString(baseUrl) + "/user/profile/" + id + "/");
    }

    private void getBannerImage() {
        new DownloadImageTask(appBarLayout).execute(project.avatar);
    }

    private void getProject(int id) {
        new GetProjectTask().execute(getResources().getString(baseUrl) + "/project/" + id + "/");
    }

    private void getQuestions() {
        new GetQuestionsTask().execute(getResources().getString(baseUrl) + "/curio/");
    }

    private class GetProjectTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                project = projectUrl(urls[0]);
                Log.d("TESTING", "PROJECT " + project.description);
                return "success";
            } catch (IOException e) {
                Log.d("TESTING", "PROJECT " + e.toString());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            getQuestions();
        }
    }


    private class GetQuestionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                questions = new ArrayList<>(questionUrl(urls[0]));
                questions = new ArrayList<>(getQuestionsWithId(project.id));
                Log.d("TESTING", "QUESTIONS " + questions.size());
                return "success";
            } catch (IOException e) {
                Log.d("TESTING", "QUESTIONS " + e.toString());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            getBannerImage();
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


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final AppBarLayout bar;

        public DownloadImageTask(AppBarLayout bar) {
            this.bar = bar;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            // style the image bar
            float aspectRatio = result.getWidth() /
                    (float) result.getHeight();
            // scale the image for performance
            Log.d("TESTING", "ASPECT RATIO " + aspectRatio);
            int width = 600;
            int height = Math.round(width / aspectRatio);

            if (result != null && !result.isRecycled()) {
                Palette palette = Palette.from(result).generate();
                if (palette.getVibrantSwatch() != null) {
                    if (palette.getDarkVibrantSwatch() != null) {
                        if (palette.getLightVibrantSwatch() != null) {
                            appBarLayout.setBackgroundColor(palette.getVibrantSwatch().getRgb());
                            collapsingToolbar.setContentScrim(new ColorDrawable(palette.getVibrantSwatch().getRgb()));
                            collapsingToolbar.setStatusBarScrim(new ColorDrawable(palette.getDarkVibrantSwatch().getRgb()));
                            tabLayout.setSelectedTabIndicatorColor(palette.getLightVibrantSwatch().getRgb());
                            //swipeRefreshLayout.setIndeterminateDrawable(new ColorDrawable(palette.getLightVibrantSwatch().getRgb()));
                        }
                    }
                }
            }

            Drawable backgrounds[] = new Drawable[2];
            backgrounds[0] = bar.getBackground();
            backgrounds[1] = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(
                    result, width, height, false));

            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);

            //bar.setBackground(crossfader);
            backdrop.setImageDrawable(crossfader);

            crossfader.startTransition(500);

            getTeam();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, projectListActivity.class));
            return true;
        }

        if (id == R.id.action_refresh) {
            refreshData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.refreshViewPager();

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void refreshViewPager() {
            mFragmentList.clear();
            mFragmentTitleList.clear();

            Bundle arguments = new Bundle();
            arguments.putParcelable(projectDetailFragment.ARG_ITEM_ID, project);
            detailFragment = new projectDetailFragment();
            detailFragment.setArguments(arguments);
            adapter.addFragment(detailFragment, "About");

            Bundle arguments2 = new Bundle();
            //Log.d("TESTING QUESTIONS", "PASSING QUESTIONS " + questions.get(0).question);
            arguments2.putParcelableArrayList(projectDetailFragment.ARG_QUESTIONS_ID, questions);
            questionsFragment = new projectResearchQuestionsFragment();
            questionsFragment.setArguments(arguments2);
            adapter.addFragment(questionsFragment, "Contribute");

            Bundle arguments3 = new Bundle();
            arguments3.putParcelableArrayList(projectTeamFragment.ARG_TEAM_ID, users);
            teamFragment = new projectTeamFragment();
            teamFragment.setArguments(arguments3);
            adapter.addFragment(teamFragment, "Team");

            notifyDataSetChanged();
        }


        @Override
        public int getItemPosition(Object o) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class GetUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("TESTING", "GETTING USERS");
            // params comes from the execute() call: params[0] is the url.
            try {
                users.add(userUrl(urls[0]));
                Log.d("TESTING", "GOT A USER, " + users.size());
                return "success";
            } catch (IOException e) {
                Log.e("TESTING", e.toString());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                fetchedUsers++;
            }

            if(fetchedUsers == numUsers) {
                Log.d("TESTING", "GOT ALL USERS");
                onFinishRefresh();
            }

        }
    }
}
