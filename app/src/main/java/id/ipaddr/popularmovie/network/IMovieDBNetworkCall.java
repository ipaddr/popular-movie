package id.ipaddr.popularmovie.network;

import id.ipaddr.popularmovie.Movie;
import id.ipaddr.popularmovie.Movies;
import id.ipaddr.popularmovie.Review;
import id.ipaddr.popularmovie.Video;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public interface IMovieDBNetworkCall {

    final String TMDB_API_KEY = "api-key";

    @GET("popular?api_key="+TMDB_API_KEY)
    Call<Movies> getPopularMovies();

    @GET("top_rated?api_key="+TMDB_API_KEY)
    Call<Movies> getTopRatedMovies();

    @GET("{id}/videos?api_key="+TMDB_API_KEY)
    Call<Video> getTrailers(@Path("id") int id);

    @GET("{id}/reviews?api_key="+TMDB_API_KEY)
    Call<Review> getReviews(@Path("id") int id);

}
