package com.example.nopesal.projectmoviesudacity.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by nopesal on 06/03/2017.
 */

public class FavoriteMoviesContentProvider extends ContentProvider {

    private MovieSQLiteHelper mMovieSQLiteHelper;
    public static UriMatcher sUriMatcher = buildURIMatcher();

    //For get favorite movies array
    public static final int MOVIES = 100;

    //For inserting new movie, deleting movie, get poster, get/set director, check is favorited,
    public static final int MOVIES_WITH_ID = 101;

    @Override
    public boolean onCreate() {
        mMovieSQLiteHelper = new MovieSQLiteHelper(getContext());
        return true;
    }

    public SQLiteDatabase open() {
        return mMovieSQLiteHelper.getWritableDatabase();
    }

    public static UriMatcher buildURIMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.FAVORITE_MOVIES_PATH, MOVIES);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.FAVORITE_MOVIES_PATH + "/#", MOVIES_WITH_ID);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = open();
        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                retCursor = database.query(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                retCursor = database.query(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES,
                        projection,
                        MovieSQLiteHelper.COLUMN_ID + " = ?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + FavoriteMoviesContract.AUTHORITY + "/" + FavoriteMoviesContract.FAVORITE_MOVIES_PATH;
            case MOVIES_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + FavoriteMoviesContract.AUTHORITY + "/" + FavoriteMoviesContract.FAVORITE_MOVIES_PATH;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = open();
        Uri returnUri = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_WITH_ID:
                long id = database.insert(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = open();
        int tasksDeleted;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = database.delete(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES, MovieSQLiteHelper.COLUMN_ID + " = ?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = open();
        int tasksUpdated;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksUpdated = database.update(MovieSQLiteHelper.TABLENAME_FAVORITE_MOVIES, values, MovieSQLiteHelper.COLUMN_ID + " = ?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }
}
