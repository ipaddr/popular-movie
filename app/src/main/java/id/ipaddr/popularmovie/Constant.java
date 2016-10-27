package id.ipaddr.popularmovie;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public class Constant {

    public static final int UNUSED_INT_PARAM = -1;
    public static final int USED_INT_PARAM_POPULAR = 0;
    public static final int USED_INT_PARAM_RATED = 1;
    public static final int USED_INT_PARAM_DETAIL = 2;

    public static final String KEY_ACTION = "id.ipaddr.popularmovie.KEY_ACTION";
    public static final String KEY_ID = "id.ipaddr.popularmovie.KEY_ID";
    public static final String KEY_URI = "id.ipaddr.popularmovie.KEY_URI";

    /**
     * API key for Movie DB access
     */
    private final String API_KEY = "257daff2ea5e8a52ce59c391d4c07251";
    /**
     * Movie Db pre-string image path
     */
    public static final String MOVIE_DB_IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";

    // Intent extras
    public static final String EXTRA_TITLE = "id.ipaddr.popularmovie.EXTRA_TITLE";
    public static final String EXTRA_RELEASE_DATE = "id.ipaddr.popularmovie.EXTRA_RELEASE_DATE";
    public static final String EXTRA_POSTER = "id.ipaddr.popularmovie.EXTRA_POSTER";
    public static final String EXTRA_VOTE_AVERAGE = "id.ipaddr.popularmovie.EXTRA_VOTE_AVERAGE";
    public static final String EXTRA_PLOT_SYNOPSYS = "id.ipaddr.popularmovie.EXTRA_PLOT_SYNOPSYS";

}
