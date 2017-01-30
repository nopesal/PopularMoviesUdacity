package com.example.nopesal.projectmoviesudacity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.R;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nico on 26/01/2017.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Movie> mMovieArray;

    public MovieGridAdapter(Context mContext, ArrayList<Movie> mMovieArray) {
        this.mContext = mContext;
        this.mMovieArray = mMovieArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mMovieGridItemTitle.setText(mMovieArray.get(position).getTitle());
        holder.mMovieGridItemYear.setText(mMovieArray.get(position).getReleaseDate().substring(0, 4));

        String posterPath = MovieDatabase.getSDPosterURL(mMovieArray.get(position).getPosterPath());
        Picasso.with(mContext).load(posterPath).into(holder.mMovieGridItemPoster,
                PicassoPalette.with(posterPath, holder.mMovieGridItemPoster)
                        .use(PicassoPalette.Profile.VIBRANT)
                        .intoBackground(holder.mMovieGridItemDetails)
        );
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mMovieArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mMovieGridItemPoster;
        TextView mMovieGridItemTitle;
        TextView mMovieGridItemYear;
        LinearLayout mMovieGridItemDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            mMovieGridItemPoster = (ImageView) itemView.findViewById(R.id.movie_grid_item_poster);
            mMovieGridItemTitle = (TextView) itemView.findViewById(R.id.movie_grid_item_title);
            mMovieGridItemYear = (TextView) itemView.findViewById(R.id.movie_grid_item_year);
            mMovieGridItemDetails = (LinearLayout) itemView.findViewById(R.id.movie_grid_item_details);
        }
    }
}
