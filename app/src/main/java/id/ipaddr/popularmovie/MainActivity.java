package id.ipaddr.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        rv = (RecyclerView)findViewById(R.id.movies);
        movieAdapter = new MovieAdapter(new ArrayList<Movie>(0));
        rv.setAdapter(movieAdapter);
        rv.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        searchPopularity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(Movies movies) {
        updateAdapter(movies.getMovies());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void searchPopularity(){
        if (isConnected()){
            progressBar.setVisibility(View.VISIBLE);
            rv.setVisibility(View.INVISIBLE);
            MovieDBIntentService.startActionGetPopularMovies(this);
        }
    }

    private void searchRating(){
        if (isConnected()){
            progressBar.setVisibility(View.VISIBLE);
            rv.setVisibility(View.INVISIBLE);
            MovieDBIntentService.startActionGetTopRatedMovies(this);
        }
    }

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.popularity:
                searchPopularity();
                return true;
            case R.id.rating:
                searchRating();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    private void updateAdapter(List<Movie> movies){
        progressBar.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        movieAdapter = new MovieAdapter(movies);
        rv.setAdapter(movieAdapter);
        rv.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
    }

    //region recycler view
    /**
     * Custom recylerview adapter
     */

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movie_image);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<ViewHolder>{

        private List<Movie> movies;
        private final View.OnClickListener recyclerViewOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        public MovieAdapter(List<Movie> movies){
            this.movies = new ArrayList<>();
            this.movies.clear();
            this.movies.addAll(movies);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            int height = parent.getMeasuredHeight() / 4;
            v.setMinimumHeight(height);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Movie movie = movies.get(position);
            Picasso.with(MainActivity.this)
                    .load(MovieDBIntentService.MOVIE_DB_IMAGE_PATH + movie.getPosterPath())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(Constant.EXTRA_TITLE, movie.getTitle());
                    intent.putExtra(Constant.EXTRA_RELEASE_DATE, movie.getReleaseDate());
                    intent.putExtra(Constant.EXTRA_POSTER, movie.getPosterPath());
                    intent.putExtra(Constant.EXTRA_VOTE_AVERAGE, movie.getVoteAverage());
                    intent.putExtra(Constant.EXTRA_PLOT_SYNOPSYS, movie.getOverview());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }
    //endregion

}
