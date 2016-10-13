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

    /* Inner class that defines the table contents */
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

        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "originaltitle";
        public static final String COLUMN_NAME_IMAGE_THUMBNAIL = "imagethumbnail";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "voteaverage";
        public static final String COLUMN_NAME_PLOT_SYNOPSIS = "plotsynopsis";
        public static final String COLUMN_NAME_RELEASE_DATE = "releasedate";

        public static final String [] PROJECTION
                = {_ID, COLUMN_NAME_ORIGINAL_TITLE, COLUMN_NAME_IMAGE_THUMBNAIL
                , COLUMN_NAME_VOTE_AVERAGE, COLUMN_NAME_PLOT_SYNOPSIS, COLUMN_NAME_RELEASE_DATE};

    }
}

