package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.adapters.MovieGridAdapter;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieDetailsActivity extends AppCompatActivity {
    public TextView mMovieDetailsTitle;
    public TextView mMovieDetailsReleaseDate;
    public TextView mMovieDetailsRating;
    public TextView mMovieDetailsSynopsis;
    public ImageView mMovieDetailsPoster;
    public CollapsingToolbarLayout mCollapsingToolbarLayout;
    public TextView mMovieDetailsDirector;

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskCompleted(T result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mMovieDetailsTitle = (TextView) findViewById(R.id.movie_details_title);
        mMovieDetailsReleaseDate = (TextView) findViewById(R.id.movie_details_release_date);
        mMovieDetailsSynopsis = (TextView) findViewById(R.id.movie_details_synopsis);
        mMovieDetailsPoster = (ImageView) findViewById(R.id.movie_details_poster);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.movie_details_collapsing_toolbar_layout);
        mMovieDetailsDirector = (TextView) findViewById(R.id.movie_details_director);


        final Movie movie = getIntent().getExtras().getParcelable("Movie");
        new DirectorTask(this, new DirectorTaskCompletedListener()).execute(movie.getId());

        String posterPath = MovieDatabase.getHDPosterURL(movie.getPosterPath());
        Picasso.with(getApplicationContext()).load(posterPath).into(mMovieDetailsPoster);


        mMovieDetailsTitle.setText(movie.getTitle());
        mMovieDetailsReleaseDate.setText(movie.getReleaseDate());
        mMovieDetailsSynopsis.setText(movie.getSynopsis());

        hideTitleWhenExpanded(movie);
    }

    private void showDirectorInPanel() {
        TextView directedBy = (TextView) findViewById(R.id.movie_details_directed_by_text_view);
        directedBy.setVisibility(View.VISIBLE);
        mMovieDetailsDirector.setVisibility(View.VISIBLE);
    }

    /**
     * Method that hides the Movie title from the interface when the Scroll View is expanded and
     * shows it when it's collapsed.
     * @param movie Object that contains the movie info
     */
    private void hideTitleWhenExpanded(final Movie movie) {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(movie.getTitle());
                    isShow = true;
                } else if(isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class DirectorTaskCompletedListener implements MovieDetailsActivity.AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskCompleted(String director) {
            if (director != null){
                showDirectorInPanel();
                mMovieDetailsDirector.setText(director);
            }
        }
    }
}
