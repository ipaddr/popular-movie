package id.ipaddr.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import id.ipaddr.popularmovie.data.MovieContract.MovieEntry;
import id.ipaddr.popularmovie.data.MovieContract.MovieTrailerEntry;
import id.ipaddr.popularmovie.data.MovieContract.MovieReviewEntry;
import id.ipaddr.popularmovie.data.MovieContract.MovieFavoriteEntry;


public class MovieProvider extends ContentProvider {

    /**
     * Matcher that will match to specific uri
     */
    public static final int MATCHER_MOVIES = 101;
    public static final int MATCHER_MOVIE_ID = 102;
    public static final int MATCHER_MOVIES_TRAILER = 103;
    public static final int MATCHER_MOVIE_TRAILER_ID = 104;
    public static final int MATCHER_MOVIES_REVIEW = 105;
    public static final int MATCHER_MOVIE_REVIEW_ID = 106;
    public static final int MATCHER_MOVIE_TRAILER_REVIEW_ID = 107;
    public static final int MATCHER_MOVIES_FAVORITE = 108;
    public static final int MATCHER_MOVIE_FAVORITE_ID = 109;
    public static final int MATCHER_MOVIE_FAVORITE_TRAILER_REVIEW_ID = 110;

    private static final String MOVIE_JOIN_TRAILER_REVIEW = MovieEntry.TABLE_NAME + " LEFT JOIN " +
            MovieTrailerEntry.TABLE_NAME +
            " ON " + MovieEntry.TABLE_NAME +
            "." + MovieEntry.COLUMN_NAME_ID +
            " = " + MovieTrailerEntry.TABLE_NAME +
            "." + MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY
            + "  " +
            " LEFT JOIN " +
            MovieReviewEntry.TABLE_NAME +
            " ON " + MovieEntry.TABLE_NAME +
            "." + MovieEntry.COLUMN_NAME_ID +
            " = " + MovieReviewEntry.TABLE_NAME +
            "." + MovieReviewEntry.COLUMN_NAME_MOVIE_KEY;

    private static final SQLiteQueryBuilder sMoviesTrailerReviewQueryBuilder;
    static{
        sMoviesTrailerReviewQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMoviesTrailerReviewQueryBuilder.setTables(MOVIE_JOIN_TRAILER_REVIEW);
    }

    //location.location_setting = ? AND date = ?
    private static final String sMoviesTrailerReviewQueryBuilderSelection =
            MovieEntry.TABLE_NAME +
                    "." + MovieEntry.COLUMN_NAME_ID + " = ? " ;

    private Cursor getMovieWithTrailerAndReview(
            Uri uri, String[] projection, String sortOrder) {

        String movieId = uri.getPathSegments().get(2);
        String[] selectionArg = new String[]{movieId};

        return sMoviesTrailerReviewQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                sMoviesTrailerReviewQueryBuilderSelection,
                selectionArg,
                null,
                null,
                sortOrder
        );
    }

    private static final String MOVIE_FAVORITE_JOIN_TRAILER_REVIEW = MovieFavoriteEntry.TABLE_NAME + " LEFT JOIN " +
            MovieTrailerEntry.TABLE_NAME +
            " ON " + MovieFavoriteEntry.TABLE_NAME +
            "." + MovieFavoriteEntry.COLUMN_NAME_ID +
            " = " + MovieTrailerEntry.TABLE_NAME +
            "." + MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY
            + "  " +
            " LEFT JOIN " +
            MovieReviewEntry.TABLE_NAME +
            " ON " + MovieFavoriteEntry.TABLE_NAME +
            "." + MovieFavoriteEntry.COLUMN_NAME_ID +
            " = " + MovieReviewEntry.TABLE_NAME +
            "." + MovieReviewEntry.COLUMN_NAME_MOVIE_KEY;

    private static final SQLiteQueryBuilder sMoviesFavoriteTrailerReviewQueryBuilder;
    static{
        sMoviesFavoriteTrailerReviewQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMoviesFavoriteTrailerReviewQueryBuilder.setTables(MOVIE_FAVORITE_JOIN_TRAILER_REVIEW);
    }

    //location.location_setting = ? AND date = ?
    private static final String sMoviesFavoriteTrailerReviewQueryBuilderSelection =
            MovieFavoriteEntry.TABLE_NAME +
                    "." + MovieFavoriteEntry.COLUMN_NAME_ID + " = ? " ;

    private Cursor getMovieFavoriteWithTrailerAndReview(
            Uri uri, String[] projection, String sortOrder) {

        String movieId = uri.getPathSegments().get(2);
        String[] selectionArg = new String[]{movieId};

        return sMoviesFavoriteTrailerReviewQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                sMoviesFavoriteTrailerReviewQueryBuilderSelection,
                selectionArg,
                null,
                null,
                sortOrder
        );
    }

    /**
     * add instance of uri matcher so it will have content to match to
     */
    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.PATH_MOVIE, MATCHER_MOVIES);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.PATH_MOVIE + "/#", MATCHER_MOVIE_ID);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.PATH_MOVIE + "/#/*", MATCHER_MOVIE_TRAILER_REVIEW_ID);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieTrailerEntry.PATH_MOVIE_TRAILER, MATCHER_MOVIES_TRAILER);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieTrailerEntry.PATH_MOVIE_TRAILER + "/#", MATCHER_MOVIE_TRAILER_ID);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieReviewEntry.PATH_MOVIE_REVIEW, MATCHER_MOVIES_REVIEW);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieReviewEntry.PATH_MOVIE_REVIEW + "/#", MATCHER_MOVIE_REVIEW_ID);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieFavoriteEntry.PATH_MOVIE_FAVORITE, MATCHER_MOVIES_FAVORITE);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieFavoriteEntry.PATH_MOVIE_FAVORITE + "/#", MATCHER_MOVIE_FAVORITE_ID);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieFavoriteEntry.PATH_MOVIE_FAVORITE + "/#/*", MATCHER_MOVIE_FAVORITE_TRAILER_REVIEW_ID);
    }

    private MovieDbHelper mDbHelper;

    public MovieProvider() {
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_MOVIE;
            case MATCHER_MOVIES:
                return MovieEntry.CONTENT_TYPE_MOVIE;
            case MATCHER_MOVIE_TRAILER_ID:
                return MovieTrailerEntry.CONTENT_ITEM_MOVIE;
            case MATCHER_MOVIES_TRAILER:
                return MovieTrailerEntry.CONTENT_TYPE_MOVIE;
            case MATCHER_MOVIE_REVIEW_ID:
                return MovieReviewEntry.CONTENT_ITEM_MOVIE;
            case MATCHER_MOVIES_REVIEW:
                return MovieReviewEntry.CONTENT_TYPE_MOVIE;
            case MATCHER_MOVIE_TRAILER_REVIEW_ID:
                return MovieEntry.CONTENT_ITEM_MOVIE;
            case MATCHER_MOVIE_FAVORITE_ID:
                return MovieFavoriteEntry.CONTENT_ITEM_MOVIE;
            case MATCHER_MOVIES_FAVORITE:
                return MovieFavoriteEntry.CONTENT_TYPE_MOVIE;
            case MATCHER_MOVIE_FAVORITE_TRAILER_REVIEW_ID:
                return MovieFavoriteEntry.CONTENT_ITEM_MOVIE;
            default: throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;
        Uri resultUri;

        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
            case MATCHER_MOVIES:
                id = db.insert(MovieEntry.TABLE_NAME, null, values);
                resultUri = MovieEntry.buildProdictUri(id);
                break;
            case MATCHER_MOVIE_TRAILER_ID:
            case MATCHER_MOVIES_TRAILER:
                id = db.insert(MovieTrailerEntry.TABLE_NAME, null, values);
                resultUri = MovieTrailerEntry.buildProdictUri(id);
                break;
            case MATCHER_MOVIE_REVIEW_ID:
            case MATCHER_MOVIES_REVIEW:
                id = db.insert(MovieReviewEntry.TABLE_NAME, null, values);
                resultUri = MovieReviewEntry.buildProdictUri(id);
                break;
            case MATCHER_MOVIE_FAVORITE_ID:
            case MATCHER_MOVIES_FAVORITE:
                id = db.insert(MovieFavoriteEntry.TABLE_NAME, null, values);
                resultUri = MovieFavoriteEntry.buildProdictUri(id);
                break;
            default:throw new UnsupportedOperationException("Not yet implemented");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;
        String arg;
        String [] args;
        Cursor cursor;
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                id = ContentUris.parseId(uri);
                arg = MovieEntry._ID + "=?";
                args = new String[]{String.valueOf(id)};
                cursor = db.query(MovieEntry.TABLE_NAME, MovieEntry.PROJECTION, arg, args, null, null, null);
                break;
            case MATCHER_MOVIES:
                cursor = db.query(MovieEntry.TABLE_NAME, MovieEntry.PROJECTION, null, null, null, null, null);
                break;
            case MATCHER_MOVIE_TRAILER_ID:
                id = ContentUris.parseId(uri);
                arg = MovieTrailerEntry._ID + "=?";
                args = new String[]{String.valueOf(id)};
                cursor = db.query(MovieTrailerEntry.TABLE_NAME, MovieTrailerEntry.PROJECTION, arg, args, null, null, null);
                break;
            case MATCHER_MOVIES_TRAILER:
                cursor = db.query(MovieTrailerEntry.TABLE_NAME, MovieTrailerEntry.PROJECTION, selection, selectionArgs, null, null, null);
                break;
            case MATCHER_MOVIE_REVIEW_ID:
                id = ContentUris.parseId(uri);
                arg = MovieReviewEntry._ID + "=?";
                args = new String[]{String.valueOf(id)};
                cursor = db.query(MovieReviewEntry.TABLE_NAME, MovieReviewEntry.PROJECTION, arg, args, null, null, null);
                break;
            case MATCHER_MOVIES_REVIEW:
                cursor = db.query(MovieReviewEntry.TABLE_NAME, MovieReviewEntry.PROJECTION, selection, selectionArgs, null, null, null);
                break;
            case MATCHER_MOVIE_TRAILER_REVIEW_ID:
                cursor = getMovieWithTrailerAndReview(uri, projection, sortOrder);
                break;
            case MATCHER_MOVIE_FAVORITE_ID:
                id = ContentUris.parseId(uri);
                arg = MovieFavoriteEntry._ID + "=?";
                args = new String[]{String.valueOf(id)};
                cursor = db.query(MovieFavoriteEntry.TABLE_NAME, MovieFavoriteEntry.PROJECTION, arg, args, null, null, null);
                break;
            case MATCHER_MOVIES_FAVORITE:
                cursor = db.query(MovieFavoriteEntry.TABLE_NAME, MovieFavoriteEntry.PROJECTION, selection, selectionArgs, null, null, null);
                break;
            case MATCHER_MOVIE_FAVORITE_TRAILER_REVIEW_ID:
                cursor = getMovieFavoriteWithTrailerAndReview(uri, projection, sortOrder);
                break;
            default:throw new UnsupportedOperationException("Not yet implemented");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int id;
        String arg;
        String [] args;
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                arg = MovieEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.delete(MovieEntry.TABLE_NAME, arg, args);
                break;
            case MATCHER_MOVIES:
                id = db.delete(MovieEntry.TABLE_NAME, null, null);
                break;
            case MATCHER_MOVIE_TRAILER_ID:
                arg = MovieTrailerEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.delete(MovieTrailerEntry.TABLE_NAME, arg, args);
                break;
            case MATCHER_MOVIES_TRAILER:
                id = db.delete(MovieTrailerEntry.TABLE_NAME, null, null);
                break;
            case MATCHER_MOVIE_REVIEW_ID:
                arg = MovieReviewEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.delete(MovieReviewEntry.TABLE_NAME, arg, args);
                break;
            case MATCHER_MOVIES_REVIEW:
                id = db.delete(MovieReviewEntry.TABLE_NAME, null, null);
                break;
            case MATCHER_MOVIE_FAVORITE_ID:
                arg = MovieFavoriteEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.delete(MovieFavoriteEntry.TABLE_NAME, arg, args);
                break;
            case MATCHER_MOVIES_FAVORITE:
                id = db.delete(MovieFavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int id;
        String arg;
        String [] args;
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                arg = MovieEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.update(MovieEntry.TABLE_NAME, values, arg, args);
                break;
            case MATCHER_MOVIE_TRAILER_ID:
                arg = MovieTrailerEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.update(MovieTrailerEntry.TABLE_NAME, values, arg, args);
                break;
            case MATCHER_MOVIE_REVIEW_ID:
                arg = MovieReviewEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.update(MovieReviewEntry.TABLE_NAME, values, arg, args);
                break;
            case MATCHER_MOVIE_FAVORITE_ID:
                arg = MovieFavoriteEntry._ID + "=?";
                args = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.update(MovieFavoriteEntry.TABLE_NAME, values, arg, args);
                break;
            case MATCHER_MOVIES:
            case MATCHER_MOVIES_TRAILER:
            case MATCHER_MOVIES_REVIEW:
            case MATCHER_MOVIES_FAVORITE:
            default:throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }
}
