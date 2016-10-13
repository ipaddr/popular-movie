package id.ipaddr.popularmovie.network;

import id.ipaddr.popularmovie.Movie;
import id.ipaddr.popularmovie.Movies;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public interface IMovieDBNetworkCall {

    @GET("popular?api_key=257daff2ea5e8a52ce59c391d4c07251")
    Call<Movies> getPopularMovies();

    @GET("top_rated?api_key=257daff2ea5e8a52ce59c391d4c07251")
    Call<Movies> getTopRatedMovies();

}