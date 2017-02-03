package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieDetailsActivity extends AppCompatActivity {
    public TextView mMovieDetailsTitle;
    public TextView mMovieDetailsReleaseDate;
    public TextView mMovieDetailsRating;
    public TextView mMovieDetailsSynopsis;
    public ImageView mMovieDetailsPoster;
    public FloatingActionButton mFAB;

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

        Movie movie = getIntent().getExtras().getParcelable("Movie");
        String posterPath = MovieDatabase.getHDPosterURL(movie.getPosterPath());
        Picasso.with(getApplicationContext()).load(posterPath).into(mMovieDetailsPoster);


        mMovieDetailsTitle.setText(movie.getTitle());
        mMovieDetailsReleaseDate.setText(movie.getReleaseDate());
        mMovieDetailsSynopsis.setText(movie.getSynopsis());

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.movie_details_collapsing_toolbar_layout);
        toolbarLayout.setTitle(movie.getTitle());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
