package id.ipaddr.popularmovie.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public class MovieDBNetworkCall {

    // Trailing slash is needed
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    /**
     * Method that will produce the instance for network call using retrofit framework
     * @return
     */
    public static IMovieDBNetworkCall getCalledMoviesAPI(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IMovieDBNetworkCall calledMoviesAPI = retrofit.create(IMovieDBNetworkCall.class);

        return calledMoviesAPI;
    }

}
