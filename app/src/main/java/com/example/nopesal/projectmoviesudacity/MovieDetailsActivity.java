package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.nopesal.projectmoviesudacity.adapters.ReviewListAdapter;
import com.example.nopesal.projectmoviesudacity.database.FavoriteMoviesDataSource;
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
import butterknife.OnClick;
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
    @BindView(R.id.activity_movie_details) CoordinatorLayout mCoordinatorLayout;

    public Movie mMovie;
    public String mDirector = "";
    public Bitmap mPoster = null;

    public boolean isFavorited = false;

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

        mMovie = getIntent().getExtras().getParcelable("Movie");

        FavoriteMoviesDataSource dataSource = new FavoriteMoviesDataSource(this);
        isFavorited = dataSource.isFavorited(mMovie);

        if (!isFavorited) {
            new DirectorTask(new DirectorTaskCompletedListener()).execute(mMovie.getId());
            new TrailerTask(new TrailerTaskCompletedListener()).execute(mMovie.getId());
            new ReviewsTask(new ReviewsTaskCompletedListener()).execute(mMovie.getId());

            String posterPath = URLGenerator.getHDPosterURL(mMovie.getPosterPath());
            Picasso.with(getApplicationContext()).load(posterPath).into(mMovieDetailsPoster, new Callback() {
                @Override
                public void onSuccess() {
                    extractColorsFromPoster();
                }

                @Override
                public void onError() {
                    mCoordinatorLayout.setVisibility(View.VISIBLE);
                }
            });
        } else {
            showDirectorInPanel(dataSource.getDirector(mMovie.getId()));
            mMovieDetailsPoster.setImageBitmap(dataSource.getPoster(mMovie.getId()));
            extractColorsFromPoster();
            if (!isNetworkAvailable()) {
                mReviewsNumber.setText(R.string.reviews_offline);
            } else {
                new TrailerTask(new TrailerTaskCompletedListener()).execute(mMovie.getId());
                new ReviewsTask(new ReviewsTaskCompletedListener()).execute(mMovie.getId());
            }
        }

        Typeface nunitoBold = Typeface.createFromAsset(getAssets(),
                "fonts/Nunito-Bold.ttf");
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(nunitoBold);
        mMovieDetailsTitle.setText(mMovie.getTitle());
        mMovieDetailsReleaseDate.setText(mMovie.getReleaseDate().substring(0, 4));
        mMovieDetailsSynopsis.setText(mMovie.getSynopsis());
        mMovieDetailsRating.setText(mMovie.getRating());

        hideTitleWhenExpanded();

    }

    private void extractColorsFromPoster() {
        mPoster = ((BitmapDrawable) mMovieDetailsPoster.getDrawable()).getBitmap();
        Palette palette = Palette.from(mPoster).generate();
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        if (vibrantSwatch != null) {
            applyPaletteColorToViews(vibrantSwatch);
        } else {
            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
            applyPaletteColorToViews(mutedSwatch);
        }
        mCoordinatorLayout.setVisibility(View.VISIBLE);
    }

    private void applyPaletteColorToViews(Palette.Swatch swatch) {
        mMovieDetailsPanel.setBackgroundColor(swatch.getRgb());
        mMovieDetailsTitle.setTextColor(swatch.getBodyTextColor());
        mMovieDetailsDirector.setTextColor(swatch.getBodyTextColor());
        mMovieDetailsReleaseDate.setTextColor(swatch.getBodyTextColor());
        mDividerOne.setBackgroundColor(swatch.getRgb());
        mDividerTwo.setBackgroundColor(swatch.getRgb());
        mCollapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
        mMovieDetailsRating.setTextColor(swatch.getRgb());
        setStatusBarColor(swatch);
        if (isFavorited) {
            setFavoriteButtonFillColor(swatch);
        } else {
            setFavoriteButtonBorderColor(swatch);
        }
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
        mFavoriteButtonText.setTextColor(swatch.getRgb());
        mFavoriteButtonImage.setImageTintList(ColorStateList.valueOf(swatch.getRgb()));

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(7 * getResources().getDisplayMetrics().density);
        gd.setStroke((int) (1 * getResources().getDisplayMetrics().density), swatch.getRgb());
        mFavoriteButton.setBackground(gd);

        mFavoriteButton.setTag(R.bool.swatch_color_assigned, swatch);
    }

    private void setFavoriteButtonFillColor(Palette.Swatch swatch) {
        mFavoriteButtonText.setTextColor(getColor(R.color.detailsGrey));
        mFavoriteButtonImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.detailsGrey)));

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(7 * getResources().getDisplayMetrics().density);
        gd.setColor(swatch.getRgb());
        mFavoriteButton.setBackground(gd);

        mFavoriteButtonImage.setImageResource(R.drawable.favorite_button_pressed_icon);
        mFavoriteButton.setTag(R.bool.swatch_color_assigned, swatch);
    }

    private void showDirectorInPanel(String director) {
        mDirectedByTextView.setVisibility(View.VISIBLE);
        mMovieDetailsDirector.setVisibility(View.VISIBLE);
        mMovieDetailsDirector.setText(director);
        mDirector = director;
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

    @OnClick(R.id.movie_details_favorite_button)
    public void onFavoriteButtonClicked(View view) {
        Palette.Swatch swatch = (Palette.Swatch) mFavoriteButton.getTag(R.bool.swatch_color_assigned);
        final FavoriteMoviesDataSource dataSource = new FavoriteMoviesDataSource(getApplicationContext());
        if (isFavorited) {
            mFavoriteButton.setTag(R.bool.favorite_button_pressed, false);
            mFavoriteButtonImage.setImageResource(R.drawable.favorite_button_not_pressed_icon);
            setFavoriteButtonBorderColor(swatch);
            isFavorited = false;
            dataSource.deleteFavoriteMovie(mMovie);
        } else {
            setFavoriteButtonFillColor(swatch);
            dataSource.insertFavoriteMovie(mMovie, mPoster, mDirector);
            isFavorited = true;
        }
    }


    /**
     * Method that hides the Movie title from the interface when the Scroll View is expanded and
     * shows it when it's collapsed.
     */
    private void hideTitleWhenExpanded() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(mMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class DirectorTaskCompletedListener implements MovieDetailsActivity.AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskCompleted(String director) {
            if (director != null) {
                showDirectorInPanel(director);
                new FavoriteMoviesDataSource(getApplicationContext()).setDirector(mMovie, director);
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
