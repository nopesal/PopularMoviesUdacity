package com.example.nopesal.projectmoviesudacity.adapters;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.MainActivity;
import com.example.nopesal.projectmoviesudacity.R;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nico on 26/01/2017.
 */

public class MovieGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovieArray;

    public MovieGridAdapter(Context mContext, ArrayList<Movie> mMovieArray) {
        this.mContext = mContext;
        this.mMovieArray = mMovieArray;
    }

    @Override
    public int getCount() {
        return mMovieArray.size();
    }

    @Override
    public Object getItem(int i) {
        return mMovieArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mMovieGridItemPoster = (ImageView) convertView.findViewById(R.id.movie_grid_item_poster);
            viewHolder.mMovieGridItemTitle = (TextView) convertView.findViewById(R.id.movie_grid_item_title);
            viewHolder.mMovieGridItemYear = (TextView) convertView.findViewById(R.id.movie_grid_item_year);
            viewHolder.mMovieGridItemDetails = (LinearLayout) convertView.findViewById(R.id.movie_grid_item_details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mMovieGridItemTitle.setText(mMovieArray.get(position).getTitle());
        viewHolder.mMovieGridItemYear.setText(mMovieArray.get(position).getReleaseDate().substring(0, 4));

        String posterPath = MovieDatabase.getSDPosterURL(mMovieArray.get(position).getPosterPath());
        Picasso.with(mContext).load(posterPath).into(viewHolder.mMovieGridItemPoster,
                PicassoPalette.with(posterPath, viewHolder.mMovieGridItemPoster)
                        .intoCallBack(new PicassoPalette.CallBack() {
                            @Override
                            public void onPaletteLoaded(Palette palette) {
                                try {
                                    int vibrantColor = palette.getVibrantSwatch().getRgb();
                                    mMovieArray.get(position).setVibrantColor(vibrantColor);
                                    viewHolder.mMovieGridItemDetails.setBackgroundColor(vibrantColor);
                                    Log.i("PELICULAS", "[Pelicula: " + mMovieArray.get(position).getTitle() +", Color: " + mMovieArray.get(position).getVibrantColor() + "]");
                                } catch (NullPointerException ignored) {
                                }
                            }
                        })
        );

        return convertView;
    }

    public ArrayList<Movie> getMovieArrayWithColors() {
        return mMovieArray;
    }

    private static class ViewHolder {
        ImageView mMovieGridItemPoster;
        TextView mMovieGridItemTitle;
        TextView mMovieGridItemYear;
        LinearLayout mMovieGridItemDetails;
    }
}
