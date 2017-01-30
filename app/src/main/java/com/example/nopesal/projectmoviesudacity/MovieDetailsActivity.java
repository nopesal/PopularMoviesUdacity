package com.example.nopesal.projectmoviesudacity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    public TextView mMovieDetailsTitle;
    public TextView mMovieDetailsReleaseDate;
    public TextView mMovieDetailsRating;
    public TextView mMovieDetailsSynopsis;
    public LinearLayout mMovieDetailsPanel;
    public ImageView mMovieDetailsPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovieDetailsTitle = (TextView) findViewById(R.id.movie_details_title);
        mMovieDetailsReleaseDate = (TextView) findViewById(R.id.movie_details_release_date);
        mMovieDetailsRating = (TextView) findViewById(R.id.movie_details_rating);
        mMovieDetailsSynopsis = (TextView) findViewById(R.id.movie_details_synopsis);
        mMovieDetailsPanel = (LinearLayout) findViewById(R.id.movie_details_panel);
        mMovieDetailsPoster = (ImageView) findViewById(R.id.movie_details_poster);

        Movie movie = getIntent().getExtras().getParcelable("Movie");
        mMovieDetailsTitle.setText(movie.getTitle());
        mMovieDetailsReleaseDate.setText(movie.getReleaseDate());
        mMovieDetailsRating.setText(movie.getRating());
        mMovieDetailsSynopsis.setText(movie.getSynopsis());

        String posterPath = MovieDatabase.getHDPosterURL(movie.getPosterPath());
        Picasso.with(getApplicationContext()).load(posterPath).into(mMovieDetailsPoster,
                PicassoPalette.with(posterPath, mMovieDetailsPoster)
                        .use(PicassoPalette.Profile.VIBRANT)
                        .intoBackground(mMovieDetailsPanel)
        );

        mMovieDetailsPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("HEIGHT", "onClick: " + mMovieDetailsPanel.getHeight());
                if (mMovieDetailsPanel.getTag() == "expand") {
                    mMovieDetailsPanel.animate().translationY(0).setDuration(500);
                    mMovieDetailsPanel.setTag("collapse");
                } else {
                    mMovieDetailsPanel.animate().translationY(view.getHeight()).setDuration(500);
                    mMovieDetailsPanel.setTag("expand");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mMovieDetailsPanel.getTag() == "expand") {
            mMovieDetailsPanel.animate().translationY(0).setDuration(500);
            mMovieDetailsPanel.setTag("collapse");
        } else {
            finish();
        }
    }
}
