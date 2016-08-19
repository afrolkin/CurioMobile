package com.andrewfrolkin.curiomobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.andrewfrolkin.curiomobile.models.Project;
import com.andrewfrolkin.curiomobile.models.Team;
import com.andrewfrolkin.curiomobile.models.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by andrewfrolkin on 2016-07-14.
 */
public class projectTeamFragment extends Fragment {

    public static final String ARG_TEAM_ID = "team_id";

    private Project mItem;

    private String name;
    private int id;
    private ArrayList<User> users;
    private ArrayList<Team> team;
    private RecyclerView recyclerView;
    private boolean refreshed = false;

    public projectTeamFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TESTING TEAM", "ONCREATEVIEW TEAM");
        if (getArguments().containsKey(ARG_TEAM_ID)) {

            if (users == null) {
                users = getArguments().getParcelableArrayList(ARG_TEAM_ID);
                if (users == null) {
                    Log.d("TESTING", "NULL");
                    //users = new ArrayList<>();
                } else {
                    Log.d("TESTING", "NOT NULL");
                }
            }
        }

        View rootView = inflater.inflate(R.layout.project_team, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.user_list);

        setupRecyclerView(recyclerView);

        if (users.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            rootView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
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
            Bitmap circleBitmap = result.createBitmap(result.getWidth(), result.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader (result,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(result.getWidth()/2, result.getHeight()/2, result.getWidth()/2, paint);
            imageView.setImageBitmap(circleBitmap);
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null) {
            Log.d("TESTING", "creating new adapter with users of size " + users.size());
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new UsersRecyclerViewAdapter(users));
        } else {
            if (refreshed) {
               // ((UsersRecyclerViewAdapter) recyclerView.getAdapter()).dataChanged(users);
                refreshed = false;
            }
        }
    }

    public class UsersRecyclerViewAdapter
            extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

        private List<User> mValues;

        public UsersRecyclerViewAdapter(List<User> items) {
            mValues = items;
        }

        @Override
        public UsersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView view = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_list_content, parent, false);
            return new UsersRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final UsersRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).title);
            holder.mNameView.setText(mValues.get(position).nick_name);
            holder.mDescriptionView.setText(mValues.get(position).bio);
            new DownloadImageTask(holder.mProfileView).execute(mValues.get(position).avatar);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void dataChanged(List<User> p) {
            mValues.clear();
            mValues.addAll(p);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView mView;
            public final TextView mTitleView;
            public final TextView mDescriptionView;
            public final TextView mNameView;
            public final ImageView mProfileView;
            public User mItem;

            public ViewHolder(CardView view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mDescriptionView = (TextView) view.findViewById(R.id.description);
                mNameView = (TextView) view.findViewById(R.id.name);
                mProfileView = (ImageView) view.findViewById(R.id.profile);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDescriptionView.getText() + "'";
            }
        }
    }
}
