package com.example.nopesal.projectmoviesudacity.database;

import android.net.Uri;

/**
 * Created by nopesal on 06/03/2017.
 */

public class FavoriteMoviesContract {

    public static final String AUTHORITY = "com.example.nopesal.projectmoviesudacity";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String FAVORITE_MOVIES_PATH = "favoritemovies";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_MOVIES_PATH).build();

}
