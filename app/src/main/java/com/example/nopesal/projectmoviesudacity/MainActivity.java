package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.adapters.MovieGridAdapter;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.itemdecorations.ItemSpacingDecoration;
import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public RecyclerView mGridRecyclerView;
    public TextView mConnectionErrorMessage;

    public ArrayList<Movie> mMovieArrayList;
    public String mOrder = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridRecyclerView = (RecyclerView) findViewById(R.id.movie_grid_recycler_view);
        mConnectionErrorMessage = (TextView) findViewById(R.id.connection_error_message);

        mGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mGridRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mGridRecyclerView.addItemDecoration(new ItemSpacingDecoration(12, 2, false));
        new PopularMoviesTask().execute(mOrder);
        if (!isNetworkAvailable()){
            mConnectionErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_order_menu_option) {
            if (item.getTitle() == "Top Rated") {
                item.setTitle("Most Popular");
                mOrder = "popular";
            } else {
                item.setTitle("Top Rated");
                mOrder = "top_rated";
            }
            new PopularMoviesTask().execute(mOrder);
        }
        if (isNetworkAvailable()){
            mConnectionErrorMessage.setVisibility(View.GONE);
        } else {
            mConnectionErrorMessage.setVisibility(View.VISIBLE);
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;

        }
        return isAvailable;
    }

    public class PopularMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(String... order) {
            MovieDatabase movieDatabase = new MovieDatabase();
            ArrayList<Movie> movieArrayList = new ArrayList<>();
            try {
                movieArrayList = movieDatabase.getMoviesArray(mOrder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mMovieArrayList = movies;
            mGridRecyclerView.setAdapter(new MovieGridAdapter(getApplicationContext(), movies));
        }
    }
}
