package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.nopesal.projectmoviesudacity.adapters.ReviewListAdapter;
import com.example.nopesal.projectmoviesudacity.database.URLGenerator;
import com.example.nopesal.projectmoviesudacity.tasks.DirectorTask;
import com.example.nopesal.projectmoviesudacity.tasks.ReviewsTask;
import com.example.nopesal.projectmoviesudacity.tasks.TrailerTask;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.example.nopesal.projectmoviesudacity.utils.Review;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieDetailsActivity extends AppCompatActivity {
    @BindView(R.id.movie_details_title) TextView mMovieDetailsTitle;
    @BindView(R.id.movie_details_release_date) TextView mMovieDetailsReleaseDate;
    @BindView(R.id.movie_details_rating) TextView mMovieDetailsRating;
    @BindView(R.id.movie_details_synopsis) TextView mMovieDetailsSynopsis;
    @BindView(R.id.movie_details_poster) ImageView mMovieDetailsPoster;
    @BindView(R.id.movie_details_collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.movie_details_director) TextView mMovieDetailsDirector;
    @BindView(R.id.movie_details_panel) LinearLayout mMovieDetailsPanel;
    @BindView(R.id.movie_details_divider_1) View mDividerOne;
    @BindView(R.id.movie_details_divider_2) View mDividerTwo;
    @BindView(R.id.movie_details_watch_trailer_button) Button mMovieTrailersButton;
    @BindView(R.id.movie_details_favorite_button) LinearLayout mFavoriteButton;
    @BindView(R.id.movie_details_favorite_button_image) ImageView mFavoriteButtonImage;
    @BindView(R.id.movie_details_favorite_button_text) TextView mFavoriteButtonText;
    @BindView(R.id.movie_details_directed_by_text_view) TextView mDirectedByTextView;
    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.movie_details_review_recycler_view) RecyclerView mReviewRecyclerView;
    @BindView(R.id.movie_details_reviews_number) TextView mReviewsNumber;

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskCompleted(T result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

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

        final Movie movie = getIntent().getExtras().getParcelable("Movie");
        new DirectorTask(new DirectorTaskCompletedListener()).execute(movie.getId());
        new TrailerTask(new TrailerTaskCompletedListener()).execute(movie.getId());
        new ReviewsTask(new ReviewsTaskCompletedListener()).execute(movie.getId());

        String posterPath = URLGenerator.getHDPosterURL(movie.getPosterPath());
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
        setFavoriteButtonBorderColor(swatch);
    }

    private void setStatusBarColor(Palette.Swatch swatch) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        float[] hsl = swatch.getHsl();
        hsl[2] = (float) (hsl[2] * 0.9);
        window.setStatusBarColor(Color.HSVToColor(hsl));
    }

    private void setFavoriteButtonBorderColor(Palette.Swatch swatch) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(7 * getResources().getDisplayMetrics().density);
        gd.setStroke((int) (1 * getResources().getDisplayMetrics().density), swatch.getRgb());
        mFavoriteButton.setBackground(gd);
    }

    private void showDirectorInPanel() {
        mDirectedByTextView.setVisibility(View.VISIBLE);
        mMovieDetailsDirector.setVisibility(View.VISIBLE);
    }

    private void assignTargetToMovieTrailerButton(final String youtubeURL) {
        mMovieTrailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL));
                startActivity(intent);
            }
        });
    }

    /**
     * Method that hides the Movie title from the interface when the Scroll View is expanded and
     * shows it when it's collapsed.
     *
     * @param movie Object that contains the movie info
     */
    private void hideTitleWhenExpanded(final Movie movie) {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
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

    public class TrailerTaskCompletedListener implements MovieDetailsActivity.AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskCompleted(String youtubeTrailerURL) {
            if (youtubeTrailerURL != null) {
                assignTargetToMovieTrailerButton(youtubeTrailerURL);
            }
        }
    }

    public class ReviewsTaskCompletedListener implements MovieDetailsActivity.AsyncTaskCompleteListener<ArrayList<Review>> {
        @Override
        public void onTaskCompleted(ArrayList<Review> reviewArrayList) {
            if (!reviewArrayList.isEmpty()) {
                mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mReviewRecyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(), R.drawable.transparent_divider)));
                mReviewRecyclerView.setAdapter(new ReviewListAdapter(getApplicationContext(), reviewArrayList));
                mReviewRecyclerView.setNestedScrollingEnabled(false);
            }
            mReviewsNumber.setText(getApplicationContext().getResources().getQuantityString(R.plurals.number_of_reviews, reviewArrayList.size(), reviewArrayList.size()));
        }
    }
}
