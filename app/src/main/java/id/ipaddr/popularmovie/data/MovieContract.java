package id.ipaddr.popularmovie.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public final class MovieContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MovieContract() {}

    /**
     * Content authority as same as on the Manifest file
     */
    public static final String CONTENT_AUTHORITY = "id.ipaddr.popularmovie.AUTHORITY";

    /**
     * Base uri which is content:// and content authority
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    /* Inner class that defines the table movie contents */
    public static class MovieEntry implements BaseColumns {

        /**
         * Path to movie table
         */
        public static final String PATH_MOVIE = "path_movie";

        /**
         * Content uri for movie path
         */
        public static final Uri CONTENT_URI_MOVIE = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE);

        /**
         * Content type for list or item of uri
         */
        public static final String CONTENT_TYPE_MOVIE
                = "vnd.android.cursor.dir" + CONTENT_URI_MOVIE + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_MOVIE
                = "vnd.android.cursor.item" + CONTENT_URI_MOVIE + "/" + PATH_MOVIE;

        public static final Uri buildProdictUri(long id){return ContentUris.withAppendedId(CONTENT_URI_MOVIE, id);}

        public static final String TABLE_NAME = "moviesentry";
        public static final String COLUMN_NAME_ID = "movieid";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "originaltitle";
        public static final String COLUMN_NAME_IMAGE_THUMBNAIL = "imagethumbnail";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "voteaverage";
        public static final String COLUMN_NAME_PLOT_SYNOPSIS = "plotsynopsis";
        public static final String COLUMN_NAME_RELEASE_DATE = "releasedate";

        public static final String [] PROJECTION
                = {_ID, COLUMN_NAME_ID, COLUMN_NAME_ORIGINAL_TITLE, COLUMN_NAME_IMAGE_THUMBNAIL
                , COLUMN_NAME_VOTE_AVERAGE, COLUMN_NAME_PLOT_SYNOPSIS, COLUMN_NAME_RELEASE_DATE};

        public static Uri buildMovieWithTrailerAndReview(long id, int paramId){
            return buildProdictUri(id).buildUpon().appendPath(String.valueOf(paramId)).build();
        }
    }

    /* Inner class that defines the table trailer contents */
    public static class MovieTrailerEntry implements BaseColumns {

        /**
         * Path to movie table
         */
        public static final String PATH_MOVIE_TRAILER = "path_movie_trailer";

        /**
         * Content uri for movie path
         */
        public static final Uri CONTENT_URI_MOVIE_TRAILER = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE_TRAILER);

        /**
         * Content type for list or item of uri
         */
        public static final String CONTENT_TYPE_MOVIE
                = "vnd.android.cursor.dir" + CONTENT_URI_MOVIE_TRAILER + "/" + PATH_MOVIE_TRAILER;
        public static final String CONTENT_ITEM_MOVIE
                = "vnd.android.cursor.item" + CONTENT_URI_MOVIE_TRAILER + "/" + PATH_MOVIE_TRAILER;

        public static final Uri buildProdictUri(long id){return ContentUris.withAppendedId(CONTENT_URI_MOVIE_TRAILER, id);}

        public static final String TABLE_NAME = "trailerentry";
        public static final String COLUMN_NAME_MOVIE_KEY = "moviekey";
        public static final String COLUMN_NAME_TRAILER_KEY = "trailerkey";

        public static final String [] PROJECTION
                = {_ID, COLUMN_NAME_MOVIE_KEY, COLUMN_NAME_TRAILER_KEY};

    }

    /* Inner class that defines the table review contents */
    public static class MovieReviewEntry implements BaseColumns {

        /**
         * Path to movie table
         */
        public static final String PATH_MOVIE_REVIEW = "path_movie_review";

        /**
         * Content uri for movie path
         */
        public static final Uri CONTENT_URI_MOVIE_REVIEW = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE_REVIEW);

        /**
         * Content type for list or item of uri
         */
        public static final String CONTENT_TYPE_MOVIE
                = "vnd.android.cursor.dir" + CONTENT_URI_MOVIE_REVIEW + "/" + PATH_MOVIE_REVIEW;
        public static final String CONTENT_ITEM_MOVIE
                = "vnd.android.cursor.item" + CONTENT_URI_MOVIE_REVIEW + "/" + PATH_MOVIE_REVIEW;

        public static final Uri buildProdictUri(long id){return ContentUris.withAppendedId(CONTENT_URI_MOVIE_REVIEW, id);}

        public static final String TABLE_NAME = "reviewentry";
        public static final String COLUMN_NAME_MOVIE_KEY = "moviekey";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CONTENT = "content";

        public static final String [] PROJECTION
                = {_ID, COLUMN_NAME_MOVIE_KEY, COLUMN_NAME_AUTHOR, COLUMN_NAME_CONTENT};

    }

    /* Inner class that defines the table movie favorite contents */
    public static class MovieFavoriteEntry implements BaseColumns {

        /**
         * Path to movie table
         */
        public static final String PATH_MOVIE_FAVORITE = "path_movie_favorite";

        /**
         * Content uri for movie path
         */
        public static final Uri CONTENT_URI_MOVIE_FAVORITE = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE_FAVORITE);

        /**
         * Content type for list or item of uri
         */
        public static final String CONTENT_TYPE_MOVIE
                = "vnd.android.cursor.dir" + CONTENT_URI_MOVIE_FAVORITE + "/" + PATH_MOVIE_FAVORITE;
        public static final String CONTENT_ITEM_MOVIE
                = "vnd.android.cursor.item" + CONTENT_URI_MOVIE_FAVORITE + "/" + PATH_MOVIE_FAVORITE;

        public static final Uri buildProdictUri(long id){return ContentUris.withAppendedId(CONTENT_URI_MOVIE_FAVORITE, id);}

        public static final String TABLE_NAME = "moviesfavoriteentry";
        public static final String COLUMN_NAME_ID = "movieid";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "originaltitle";
        public static final String COLUMN_NAME_IMAGE_THUMBNAIL = "imagethumbnail";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "voteaverage";
        public static final String COLUMN_NAME_PLOT_SYNOPSIS = "plotsynopsis";
        public static final String COLUMN_NAME_RELEASE_DATE = "releasedate";

        public static final String [] PROJECTION
                = {_ID, COLUMN_NAME_ID, COLUMN_NAME_ORIGINAL_TITLE, COLUMN_NAME_IMAGE_THUMBNAIL
                , COLUMN_NAME_VOTE_AVERAGE, COLUMN_NAME_PLOT_SYNOPSIS, COLUMN_NAME_RELEASE_DATE};

        public static Uri buildMovieWithTrailerAndReview(long id, int paramId){
            return buildProdictUri(id).buildUpon().appendPath(String.valueOf(paramId)).build();
        }
    }
}

