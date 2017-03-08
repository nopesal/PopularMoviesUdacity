package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.adapters.MovieGridAdapter;
import com.example.nopesal.projectmoviesudacity.database.FavoriteMoviesDataSource;
import com.example.nopesal.projectmoviesudacity.tasks.PopularMoviesTask;
import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movie_grid_view) GridView mGridView;
    @BindView(R.id.connection_error_message) TextView mConnectionErrorMessage;
    public ArrayList<Movie> mMovieArrayList;

    public String mOrder = "popular";
    private MovieGridAdapter mMovieGridAdapter;
    private static final int HAS_BEEN_FAVORITED = 0;

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskCompleted(T result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("Most popular");

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Nunito-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (savedInstanceState == null){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String optionSelected = sharedPreferences.getString("sortOptionSelected", null);
            if (optionSelected != null) {
                switch (optionSelected) {
                    case "Best rated":
                        mOrder = "top_rated";
                        setTitle("Best rated");
                        new PopularMoviesTask(new PopularMoviesTaskCompletedListener()).execute(mOrder);
                        break;
                    case "Favorite movies":
                        mMovieArrayList = new FavoriteMoviesDataSource(this).getFavoriteMoviesArray();
                        mMovieGridAdapter = new MovieGridAdapter(getApplicationContext(), mMovieArrayList);
                        mGridView.setAdapter(mMovieGridAdapter);
                        setTitle("Favorite movies");
                        break;
                    default:
                        new PopularMoviesTask(new PopularMoviesTaskCompletedListener()).execute(mOrder);
                        break;
                }
            } else {
                new PopularMoviesTask(new PopularMoviesTaskCompletedListener()).execute(mOrder);
            }

        }
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_order_menu_option_popular) {
            mOrder = "popular";
            setTitle("Most popular");
            new PopularMoviesTask(new PopularMoviesTaskCompletedListener()).execute(mOrder);
        }
        if (item.getItemId() == R.id.sort_order_menu_option_rated) {
            mOrder = "top_rated";
            setTitle("Best rated");
            new PopularMoviesTask(new PopularMoviesTaskCompletedListener()).execute(mOrder);
        }
        if (item.getItemId() == R.id.sort_order_menu_option_favorites) {
            mMovieArrayList = new FavoriteMoviesDataSource(this).getFavoriteMoviesArray();
            mMovieGridAdapter = new MovieGridAdapter(getApplicationContext(), mMovieArrayList);
            mGridView.setAdapter(mMovieGridAdapter);
            setTitle("Favorite movies");
        }
        if (isNetworkAvailable() || getTitle().equals("Favorite movies")) {
            mConnectionErrorMessage.setVisibility(View.GONE);
        } else {
            mConnectionErrorMessage.setVisibility(View.VISIBLE);
            mMovieGridAdapter = new MovieGridAdapter(getApplicationContext(), new ArrayList<Movie>());
            mGridView.setAdapter(mMovieGridAdapter);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("gridScrollPosition", mGridView.getFirstVisiblePosition());
        outState.putString("sortOptionSelected", (String) getTitle());
        outState.putParcelableArrayList("movieArrayList", mMovieArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTitle(savedInstanceState.getString("sortOptionSelected"));
        mMovieArrayList = savedInstanceState.getParcelableArrayList("movieArrayList");
        mMovieGridAdapter = new MovieGridAdapter(this, mMovieArrayList);
        mGridView.setAdapter(mMovieGridAdapter);
        mGridView.setSelection(savedInstanceState.getInt("gridScrollPosition"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        sharedPreferences.edit().putString("sortOptionSelected", (String) getTitle()).apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getTitle().equals("Favorite movies"))
        mMovieArrayList = new FavoriteMoviesDataSource(this).getFavoriteMoviesArray();
        mMovieGridAdapter = new MovieGridAdapter(getApplicationContext(), mMovieArrayList);
        mGridView.setAdapter(mMovieGridAdapter);
    }

    public class PopularMoviesTaskCompletedListener implements AsyncTaskCompleteListener<ArrayList<Movie>> {
        @Override
        public void onTaskCompleted(ArrayList<Movie> movies) {
            if (!movies.isEmpty()) {
                mMovieArrayList = movies;
                mMovieGridAdapter = new MovieGridAdapter(getApplicationContext(), mMovieArrayList);
                mGridView.setAdapter(mMovieGridAdapter);
            }
        }
    }
}
