package id.ipaddr.popularmovie.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import id.ipaddr.popularmovie.Constant;
import id.ipaddr.popularmovie.Movie;
import id.ipaddr.popularmovie.Movies;
import id.ipaddr.popularmovie.R;
import id.ipaddr.popularmovie.Review;
import id.ipaddr.popularmovie.ReviewResult;
import id.ipaddr.popularmovie.Video;
import id.ipaddr.popularmovie.VideoResult;
import id.ipaddr.popularmovie.network.IMovieDBNetworkCall;
import id.ipaddr.popularmovie.network.MovieDBNetworkCall;
import retrofit2.Call;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = SyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        /*
         * Put the data transfer code here.
         */
        Log.d(TAG, "Starting sync");
        if (extras != null && extras.containsKey(Constant.KEY_ACTION)){
            int action = extras.getInt(Constant.KEY_ACTION);
            if (action == Constant.USED_INT_PARAM_POPULAR)
                handleActionGetPopularMovies();
            else if (action == Constant.USED_INT_PARAM_RATED)
                handleActionGetTopRatedMovies();
            else if (action == Constant.USED_INT_PARAM_DETAIL){
                if (extras.containsKey(Constant.KEY_ID) && extras.containsKey(Constant.KEY_URI)){
                    int id = extras.getInt(Constant.KEY_ID);
                    String sUri = extras.getString(Constant.KEY_URI);
                    Uri uri = Uri.parse(sUri);
                    actionGetTrailer(id, uri);
                    actionGetReview(id, uri);
                }
            }
        } else handleActionGetPopularMovies();

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context, int action, int id, Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (action != Constant.UNUSED_INT_PARAM) bundle.putInt(Constant.KEY_ACTION, action);
        if (id != Constant.UNUSED_INT_PARAM) bundle.putInt(Constant.KEY_ID, id);
        if (uri != null) bundle.putString(Constant.KEY_URI, uri.toString());
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.sync_account_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context, Constant.UNUSED_INT_PARAM, Constant.UNUSED_INT_PARAM, null);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Handle action popular in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetPopularMovies() {
        // TODO: Handle action Foo
        IMovieDBNetworkCall callMovieAPI = MovieDBNetworkCall.getCalledMoviesAPI();
        Call<Movies> callPopularMovies = callMovieAPI.getPopularMovies();
        try {
            Movies movies = callPopularMovies.execute().body();
            Log.d(TAG, "ok");
            processMovie(movies);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Handle action top rated in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetTopRatedMovies() {
        // TODO: Handle action Baz
        IMovieDBNetworkCall callMovieAPI = MovieDBNetworkCall.getCalledMoviesAPI();
        Call<Movies> callTopRatedMovies = callMovieAPI.getTopRatedMovies();
        try {
            Movies movies = callTopRatedMovies.execute().body();
            Log.d(TAG, "ok");
            processMovie(movies);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processMovie(Movies movies){
        List<Movie> movieDatas = movies.getMovies();
        Vector<ContentValues> vContentValues = new Vector<>(movieDatas.size());

        for (Movie movie : movieDatas){
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_NAME_ID, movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL, movie.getPosterPath());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS, movie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
            vContentValues.add(values);
        }

        ContentValues[] cvArray = new ContentValues[vContentValues.size()];
        vContentValues.toArray(cvArray);

        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI_MOVIE, null, null);
        getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI_MOVIE, cvArray);
        getContext().getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI_MOVIE, null);
    }

    private void actionGetTrailer(int id, Uri uri) {
        // TODO: Handle action Baz
        IMovieDBNetworkCall callMovieAPI = MovieDBNetworkCall.getCalledMoviesAPI();
        Call<Video> data = callMovieAPI.getTrailers(id);

        try {
            Video video = data.execute().body();

            String [] projection = new String[]{MovieContract.MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY
                    , MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY};
            String selection = MovieContract.MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY + " = ? AND "
                    + MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY + " = ?";
            String selectionArg[];

            List<VideoResult> videoResults = video.getVideoResults();
            for (VideoResult result : videoResults){
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY, id);
                values.put(MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY, result.getKey());

                selectionArg = new String[]{String.valueOf(id), result.getKey()};
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieTrailerEntry.CONTENT_URI_MOVIE_TRAILER, projection, selection, selectionArg, null);
                if (!cursor.moveToFirst()){
                    getContext().getContentResolver().insert(MovieContract.MovieTrailerEntry.CONTENT_URI_MOVIE_TRAILER, values);
                }
                cursor.close();
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void actionGetReview(int id, Uri uri) {
        // TODO: Handle action Baz
        IMovieDBNetworkCall callMovieAPI = MovieDBNetworkCall.getCalledMoviesAPI();
        Call<Review> data = callMovieAPI.getReviews(id);

        try {
            Review review = data.execute().body();

            String [] projection = new String[]{MovieContract.MovieReviewEntry.COLUMN_NAME_MOVIE_KEY
                    , MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR
                    , MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT};
            String selection = MovieContract.MovieReviewEntry.COLUMN_NAME_MOVIE_KEY + " = ? AND "
                    + MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR + " = ? AND "
                    + MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT + " = ?";
            String selectionArg[];

            List<ReviewResult> reviewResults = review.getReviewResults();
            for (ReviewResult result : reviewResults){
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieReviewEntry.COLUMN_NAME_MOVIE_KEY, id);
                values.put(MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR, result.getAuthor());
                values.put(MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT, result.getContent());

                selectionArg = new String[]{String.valueOf(id), result.getAuthor(), result.getContent()};
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieReviewEntry.CONTENT_URI_MOVIE_REVIEW, projection, selection, selectionArg, null);
                if (!cursor.moveToFirst()) {
                    getContext().getContentResolver().insert(MovieContract.MovieReviewEntry.CONTENT_URI_MOVIE_REVIEW, values);
                }
                cursor.close();
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}