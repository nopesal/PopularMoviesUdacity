package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.adapters.MovieGridAdapter;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public GridView mGridView;
    public TextView mConnectionErrorMessage;

    public ArrayList<Movie> mMovieArrayList;
    public String mOrder = "popular";

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskCompleted(T result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.movie_grid_view);
        mConnectionErrorMessage = (TextView) findViewById(R.id.connection_error_message);
        new PopularMoviesTask(this, new PopularMoviesTaskCompletedListener()).execute(mOrder);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMovieArrayList.get(i);
                Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                intent.putExtra("Movie", movie);
                startActivity(intent);
            }
        });
        if (!isNetworkAvailable()) {
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
            new PopularMoviesTask(this, new PopularMoviesTaskCompletedListener()).execute(mOrder);
        }
        if (isNetworkAvailable()) {
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

    public class PopularMoviesTaskCompletedListener implements AsyncTaskCompleteListener<ArrayList<Movie>> {

        @Override
        public void onTaskCompleted(ArrayList<Movie> movies) {
            mMovieArrayList = movies;
            MovieGridAdapter adapter = new MovieGridAdapter(getApplicationContext(), mMovieArrayList);
            mGridView.setAdapter(adapter);
        }
    }
}
