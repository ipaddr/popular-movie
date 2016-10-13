package id.ipaddr.popularmovie;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import id.ipaddr.popularmovie.network.IMovieDBNetworkCall;
import id.ipaddr.popularmovie.network.MovieDBNetworkCall;
import retrofit2.Call;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MovieDBIntentService extends IntentService {
    /**
     * TAG for this class
     */
    private static final String TAG = MovieDBIntentService.class.getSimpleName();
    /**
     * API key for Movie DB access
     */
    private final String API_KEY = "257daff2ea5e8a52ce59c391d4c07251";
    /**
     * Movie Db pre-string image path
     */
    public static final String MOVIE_DB_IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_POPULAR_MOVIES = "id.ipaddr.popularmovie.action.GET_POPULAR_MOVIES";
    private static final String ACTION_GET_TOP_RATED_MOVIES = "id.ipaddr.popularmovie.action.GET_TOP_RATED_MOVIES";

    public MovieDBIntentService() {
        super("MovieDBIntentService");
    }

    /**
     * Starts this service to perform action popular with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetPopularMovies(Context context) {
        Intent intent = new Intent(context, MovieDBIntentService.class);
        intent.setAction(ACTION_GET_POPULAR_MOVIES);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action top rated with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetTopRatedMovies(Context context) {
        Intent intent = new Intent(context, MovieDBIntentService.class);
        intent.setAction(ACTION_GET_TOP_RATED_MOVIES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_POPULAR_MOVIES.equals(action)) {
                handleActionGetPopularMovies();
            } else if (ACTION_GET_TOP_RATED_MOVIES.equals(action)) {
                handleActionGetTopRatedMovies();
            }
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
            EventBus.getDefault().post(movies);
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
            EventBus.getDefault().post(movies);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
