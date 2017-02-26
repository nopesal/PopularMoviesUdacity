package com.example.nopesal.projectmoviesudacity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nopesal on 23/02/2017.
 */

public class MovieSQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "favoritemovies.db";
    public static final int DB_VERSION = 1;

    public static final String TABLENAME_FAVORITE_MOVIES = "FAVORITE_MOVIES";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_POSTER = "POSTER";
    public static final String COLUMN_DIRECTOR = "DIRECTOR";
    public static final String COLUMN_RATING = "RATING";
    public static final String COLUMN_RELEASE_DATE = "RELEASE_DATE";
    public static final String COLUMN_SYNOPSIS = "SYNOPSIS";
    public static final String COLUMN_TITLE = "TITLE";

    public static final String CREATE_FAVORITE_MOVIES =
            "CREATE TABLE " + TABLENAME_FAVORITE_MOVIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_POSTER + " BLOB, " +
                    COLUMN_DIRECTOR + " TEXT, " +
                    COLUMN_RATING + " REAL, " +
                    COLUMN_RELEASE_DATE + " TEXT, " +
                    COLUMN_SYNOPSIS + " TEXT, " +
                    COLUMN_TITLE + " TEXT" +
                    ")";

    public MovieSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
