package id.ipaddr.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import id.ipaddr.popularmovie.data.MovieContract.MovieEntry;

public class MovieProvider extends ContentProvider {

    /**
     * Matcher that will match to specific uri
     */
    public static final int MATCHER_MOVIES = 101;
    public static final int MATCHER_MOVIE_ID = 102;

    /**
     * add instance of uri matcher so it will have content to match to
     */
    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.PATH_MOVIE, MATCHER_MOVIES);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieEntry.PATH_MOVIE + "/#", MATCHER_MOVIE_ID);
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
            default: throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        try {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long id = db.insert(MovieEntry.TABLE_NAME, null, values);
            Uri resultUri = MovieEntry.buildProdictUri(id);
            getContext().getContentResolver().notifyChange(uri, null);
            return resultUri;
        } catch (Exception e){
            throw e;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor;
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                    long id = ContentUris.parseId(uri);
                    String arg = MovieEntry._ID + "=?";
                    String [] args = {String.valueOf(id)};
                cursor = db.query(MovieEntry.TABLE_NAME, MovieEntry.PROJECTION, arg, args, null, null, null);
                break;
            case MATCHER_MOVIES:
                    cursor = db.query(MovieEntry.TABLE_NAME, MovieEntry.PROJECTION, null, null, null, null, null);
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
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                    selection = MovieEntry._ID + "=?";
                    selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                    id = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MATCHER_MOVIES:
                id = db.delete(MovieEntry.TABLE_NAME, null, null);
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
        switch (URI_MATCHER.match(uri)){
            case MATCHER_MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                id = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MATCHER_MOVIES:
            default:throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }
}
