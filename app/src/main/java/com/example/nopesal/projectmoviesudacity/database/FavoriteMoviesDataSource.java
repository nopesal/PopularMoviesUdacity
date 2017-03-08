package com.example.nopesal.projectmoviesudacity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by nopesal on 23/02/2017.
 */

public class FavoriteMoviesDataSource {
    private Context mContext;

    public FavoriteMoviesDataSource(Context context) {
        mContext = context;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    private byte[] getByteArrayFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getBlob(columnIndex);
    }

    public void insertFavoriteMovie(Movie movie, Bitmap poster, String director) {
        ContentValues values = new ContentValues();
        values.put(MovieSQLiteHelper.COLUMN_ID, movie.getId());
        values.put(MovieSQLiteHelper.COLUMN_POSTER, getBytes(poster));
        values.put(MovieSQLiteHelper.COLUMN_DIRECTOR, director);
        values.put(MovieSQLiteHelper.COLUMN_RATING, movie.getRating());
        values.put(MovieSQLiteHelper.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(MovieSQLiteHelper.COLUMN_SYNOPSIS, movie.getSynopsis());
        values.put(MovieSQLiteHelper.COLUMN_TITLE, movie.getTitle());
        values.put(MovieSQLiteHelper.COLUMN_POSTER_PATH, movie.getPosterPath());

        Uri uri = FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        mContext.getContentResolver().insert(uri, values);
    }

    public void deleteFavoriteMovie(Movie movie) {
        Uri uri = FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        mContext.getContentResolver().delete(uri, null, null);
    }


    public Bitmap getPoster(int id) {
        Bitmap poster = null;
        Uri uri = FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        Cursor cursor = mContext.getContentResolver().query(uri, new String[]{MovieSQLiteHelper.COLUMN_POSTER}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            byte[] blob = getByteArrayFromColumnName(cursor, MovieSQLiteHelper.COLUMN_POSTER);
            poster = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            cursor.close();
        }
        return poster;
    }

    public String getDirector(int id) {
        String director = "";
        Uri uri = FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        Cursor cursor = mContext.getContentResolver().query(uri, new String[]{MovieSQLiteHelper.COLUMN_DIRECTOR}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            director = getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_DIRECTOR);
            cursor.close();
        }
        return director;
    }

    public String setDirector(Movie movie, String director) {
        Uri uri = FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        ContentValues values = new ContentValues();
        values.put(MovieSQLiteHelper.COLUMN_DIRECTOR, director);
        mContext.getContentResolver().update(uri, values, null, null);
        return director;
    }

    public boolean isFavorited(Movie movie) {
        Uri uri = FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        Log.i("CONTROLURI", "isFavorited: " + uri.toString());
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }
        return false;
    }

    public ArrayList<Movie> getFavoriteMoviesArray() {
        Uri uri = FavoriteMoviesContract.CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(
                            getIntFromColumnName(cursor, MovieSQLiteHelper.COLUMN_ID),
                            getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_TITLE),
                            getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_SYNOPSIS),
                            getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_RATING),
                            getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_RELEASE_DATE),
                            getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_POSTER_PATH)
                    );
                    movieArrayList.add(movie);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return movieArrayList;
    }
}
