package com.example.nopesal.projectmoviesudacity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.adapters.MovieGridAdapter;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
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
    public LinearLayout mMovieDetailsPanel;
    public View mDividerOne;
    public View mDividerTwo;
    public LinearLayout mFavoriteButton;
    public ImageView mFavoriteButtonImage;
    public TextView mFavoriteButtonText;

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskCompleted(T result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Nunito-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.movie_details_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mMovieDetailsTitle = (TextView) findViewById(R.id.movie_details_title);
        mMovieDetailsReleaseDate = (TextView) findViewById(R.id.movie_details_release_date);
        mMovieDetailsSynopsis = (TextView) findViewById(R.id.movie_details_synopsis);
        mMovieDetailsPoster = (ImageView) findViewById(R.id.movie_details_poster);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.movie_details_collapsing_toolbar_layout);
        mMovieDetailsDirector = (TextView) findViewById(R.id.movie_details_director);
        mMovieDetailsPanel = (LinearLayout) findViewById(R.id.movie_details_panel);
        mDividerOne = findViewById(R.id.movie_details_divider_1);
        mDividerTwo = findViewById(R.id.movie_details_divider_2);
        mFavoriteButton = (LinearLayout) findViewById(R.id.movie_details_favorite_button);
        mMovieDetailsRating = (TextView) findViewById(R.id.movie_details_rating);
        mFavoriteButtonImage = (ImageView) findViewById(R.id.movie_details_favorite_button_image);
        mFavoriteButtonText = (TextView) findViewById(R.id.movie_details_favorite_button_text);

        final Movie movie = getIntent().getExtras().getParcelable("Movie");
        new DirectorTask(this, new DirectorTaskCompletedListener()).execute(movie.getId());

        String posterPath = MovieDatabase.getHDPosterURL(movie.getPosterPath());
        Picasso.with(getApplicationContext()).load(posterPath).into(mMovieDetailsPoster, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) mMovieDetailsPoster.getDrawable()).getBitmap();
                Palette palette = Palette.from(bitmap).generate();
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null) {
                    applyPaletteColorToViews(vibrantSwatch);
                } else {
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    applyPaletteColorToViews(mutedSwatch);
                }
            }

            @Override
            public void onError() {
                //Do nothing
            }
        });


        Typeface nunitoBold = Typeface.createFromAsset(getAssets(),
                "fonts/Nunito-Bold.ttf");
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(nunitoBold);
        mMovieDetailsTitle.setText(movie.getTitle());
        mMovieDetailsReleaseDate.setText(movie.getReleaseDate().substring(0, 4));
        mMovieDetailsSynopsis.setText(movie.getSynopsis());
        mMovieDetailsRating.setText(movie.getRating());

        hideTitleWhenExpanded(movie);
    }

    private void applyPaletteColorToViews(Palette.Swatch swatch) {
        mMovieDetailsPanel.setBackgroundColor(swatch.getRgb());
        mMovieDetailsTitle.setTextColor(swatch.getBodyTextColor());
        mMovieDetailsDirector.setTextColor(swatch.getBodyTextColor());
        mMovieDetailsReleaseDate.setTextColor(swatch.getBodyTextColor());
        mDividerOne.setBackgroundColor(swatch.getRgb());
        mDividerTwo.setBackgroundColor(swatch.getRgb());
        mFavoriteButtonText.setTextColor(swatch.getRgb());
        mFavoriteButtonImage.setImageTintList(ColorStateList.valueOf(swatch.getRgb()));
        mCollapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
        mMovieDetailsRating.setTextColor(swatch.getRgb());
        setStatusBarColor(swatch);
        setButtonBorderColor(swatch);
    }

    private void setStatusBarColor(Palette.Swatch swatch) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        float[] hsl = swatch.getHsl();
        hsl[2] = (float) (hsl[2] * 0.9);
        window.setStatusBarColor(Color.HSVToColor(hsl));
    }

    private void setButtonBorderColor(Palette.Swatch swatch) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(7 * getResources().getDisplayMetrics().density);
        gd.setStroke((int) (2 * getResources().getDisplayMetrics().density), swatch.getRgb());
        mFavoriteButton.setBackground(gd);
    }

    private void showDirectorInPanel() {
        TextView directedBy = (TextView) findViewById(R.id.movie_details_directed_by_text_view);
        directedBy.setVisibility(View.VISIBLE);
        mMovieDetailsDirector.setVisibility(View.VISIBLE);
    }

    /**
     * Method that hides the Movie title from the interface when the Scroll View is expanded and
     * shows it when it's collapsed.
     *
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
                } else if (isShow) {
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
            if (director != null) {
                showDirectorInPanel();
                mMovieDetailsDirector.setText(director);
            }
        }
    }
}
