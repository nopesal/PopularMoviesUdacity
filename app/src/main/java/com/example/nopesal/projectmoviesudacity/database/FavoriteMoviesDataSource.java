package com.example.nopesal.projectmoviesudacity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by nopesal on 23/02/2017.
 */

public class FavoriteMoviesDataSource {
    private Context mContext;
    private MovieSQLiteHelper mMovieSQLiteHelper;

    public FavoriteMoviesDataSource(Context context) {
        mContext = context;
        mMovieSQLiteHelper = new MovieSQLiteHelper(context);
    }

    public SQLiteDatabase open() {
        return mMovieSQLiteHelper.getWritableDatabase();
    }

    public void close(SQLiteDatabase database) {
        database.close();
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
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(MovieSQLiteHelper.COLUMN_ID, movie.getId());
        values.put(MovieSQLiteHelper.COLUMN_POSTER, getBytes(poster));
        values.put(MovieSQLiteHelper.COLUMN_DIRECTOR, director);
        values.put(MovieSQLiteHelper.COLUMN_RATING, movie.getRating());
        values.put(MovieSQLiteHelper.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(MovieSQLiteHelper.COLUMN_SYNOPSIS, movie.getSynopsis());
        values.put(MovieSQLiteHelper.COLUMN_TITLE, movie.getTitle());
        values.put(MovieSQLiteHelper.COLUMN_POSTER_PATH, movie.getPosterPath());

        database.insert(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES, null, values);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void deleteFavoriteMovie(Movie movie) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES, MovieSQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(movie.getId())});

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }


    public Bitmap getPoster(int id) {
        SQLiteDatabase database = open();

        Cursor cursor = database.query(
                MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES,
                new String[]{MovieSQLiteHelper.COLUMN_POSTER},
                MovieSQLiteHelper.COLUMN_ID + " = ?", //select
                new String[]{String.valueOf(id)}, //select args
                null, //group by
                null, //having
                null //order by
        );
        cursor.moveToFirst();

        byte[] blob = getByteArrayFromColumnName(cursor, MovieSQLiteHelper.COLUMN_POSTER);
        Bitmap poster = BitmapFactory.decodeByteArray(blob, 0, blob.length);

        cursor.close();
        close(database);
        return poster;
    }

    public String getDirector(int id) {
        SQLiteDatabase database = open();

        Cursor cursor = database.query(
                MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES,
                new String[]{MovieSQLiteHelper.COLUMN_DIRECTOR},
                MovieSQLiteHelper.COLUMN_ID + " = ?", //select
                new String[]{String.valueOf(id)}, //select args
                null, //group by
                null, //having
                null //order by
        );
        cursor.moveToFirst();

        String director = getStringFromColumnName(cursor, MovieSQLiteHelper.COLUMN_DIRECTOR);

        cursor.close();
        close(database);
        return director;
    }

    public boolean isFavorited(Movie movie){
        SQLiteDatabase database = open();

        Cursor cursor = database.query(
                MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES,
                new String[]{MovieSQLiteHelper.COLUMN_TITLE},
                MovieSQLiteHelper.COLUMN_ID + " = ?", //select
                new String[]{String.valueOf(movie.getId())}, //select args
                null, //group by
                null, //having
                null //order by
        );
        if(cursor.getCount() <= 0){
            cursor.close();
            close(database);
            return false;
        }
        cursor.close();
        close(database);
        return true;
    }

    public ArrayList<Movie> getFavoriteMoviesArray() {
        SQLiteDatabase database = open();

        Cursor cursor = database.query(
                MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES,
                new String[]{
                        MovieSQLiteHelper.COLUMN_ID,
                        MovieSQLiteHelper.COLUMN_POSTER,
                        MovieSQLiteHelper.COLUMN_DIRECTOR,
                        MovieSQLiteHelper.COLUMN_RATING,
                        MovieSQLiteHelper.COLUMN_RELEASE_DATE,
                        MovieSQLiteHelper.COLUMN_SYNOPSIS,
                        MovieSQLiteHelper.COLUMN_TITLE,
                        MovieSQLiteHelper.COLUMN_POSTER_PATH
                },
                null,
                null,
                null, //group by
                null, //having
                null //order by
        );
        ArrayList<Movie> movieArrayList = new ArrayList<>();
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
        close(database);
        return movieArrayList;
    }
}
