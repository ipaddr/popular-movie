package id.ipaddr.popularmovie;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import java.util.ArrayList;
import java.util.List;

import id.ipaddr.popularmovie.data.MovieContract;
import id.ipaddr.popularmovie.data.SyncAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView rv;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        rv = (RecyclerView)findViewById(R.id.movies);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(staggeredGridLayoutManager);

        if (isConnected()){
            SyncAdapter.initializeSyncAdapter(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void searchPopularity(){
        if (isConnected()){
            SyncAdapter.syncImmediately(this, Constant.USED_INT_PARAM_POPULAR, Constant.UNUSED_INT_PARAM);
        }
    }

    private void searchRating(){
        if (isConnected()){
            SyncAdapter.syncImmediately(this, Constant.USED_INT_PARAM_RATED, Constant.UNUSED_INT_PARAM);
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

    private void updateAdapter(Cursor cursor){
        if (movieAdapter == null && cursor != null ){
            movieAdapter = new MovieAdapter(this, cursor);
            rv.setAdapter(movieAdapter);
        } else if (movieAdapter != null && cursor != null){
            movieAdapter.setCursor(cursor);
            movieAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                MovieContract.MovieEntry.PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       if (data != null)
           updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateAdapter(null);
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

        private Context mContext;
        private Cursor mCursor;

        public MovieAdapter(Context context, Cursor cursor){
            this.mContext = context;
            this.mCursor = cursor;
        }

        public void setCursor(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.movie_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (mCursor.moveToPosition(position)) {

                final long id = mCursor.getLong(mCursor.getColumnIndex(MovieContract.MovieEntry._ID));
                final int dataId = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
                final String posterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL));

                Picasso.with(MainActivity.this)
                        .load(Constant.MOVIE_DB_IMAGE_PATH + posterPath)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SyncAdapter.syncImmediately(mContext, Constant.USED_INT_PARAM_DETAIL, dataId);
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.setData(MovieContract.MovieEntry.buildMovieWithTrailerAndReview(id, dataId));
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
    //endregion

}
