package id.ipaddr.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import id.ipaddr.popularmovie.data.MovieContract;
import id.ipaddr.popularmovie.data.SyncAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;

    public static final String IS_TWO_PANE = "IS_TWO_PANE";
    private boolean isTwoPane = false;

    public static int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    private static final int MOVIE_LOADER = 0;
    private static final int MOVIE_FAVORITE_LOADER = 1;

    public static final String CURRENT_STATE = "CURRENT_STATE";
    private boolean currentState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.item_list);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        setupRecyclerView();

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        if (Utils.isConnected(this)){
            SyncAdapter.initializeSyncAdapter(this);
        }

        if (savedInstanceState != null){
            currentState = savedInstanceState.getBoolean(CURRENT_STATE);
        }

        if (!currentState)
            getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);
        else getSupportLoaderManager().initLoader(MOVIE_FAVORITE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(CURRENT_STATE, currentState);
    }

    private void setupRecyclerView() {
        updateAdapter(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.main_menu, menu);
        return true;
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
            case R.id.favorite:
                searchFavorite();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    private void searchPopularity(){
        currentState = false;
        if (Utils.isConnected(this)){
            SyncAdapter.syncImmediately(this, Constant.USED_INT_PARAM_POPULAR, Constant.UNUSED_INT_PARAM, null);
        }
    }

    private void searchRating(){
        currentState = false;
        if (Utils.isConnected(this)){
            SyncAdapter.syncImmediately(this, Constant.USED_INT_PARAM_RATED, Constant.UNUSED_INT_PARAM, null);
        }
    }

    private void searchFavorite(){
        currentState = true;
        getSupportLoaderManager().restartLoader(MOVIE_FAVORITE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        outState.putBoolean(IS_TWO_PANE, isTwoPane);
        super.onSaveInstanceState(outState);
    }

    private void updateAdapter(Cursor cursor){
        if (movieAdapter == null && cursor != null ){
            movieAdapter = new MovieAdapter(this, cursor);
            recyclerView.setAdapter(movieAdapter);
        } else if (movieAdapter != null && cursor != null){
            movieAdapter.setCursor(cursor);
            movieAdapter.notifyDataSetChanged();
        }
    }

    //region loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case MOVIE_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                        MovieContract.MovieEntry.PROJECTION,
                        null,
                        null,
                        null);
            case MOVIE_FAVORITE_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE,
                        MovieContract.MovieFavoriteEntry.PROJECTION,
                        null,
                        null,
                        null);
            default:return  null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case MOVIE_LOADER:
            case MOVIE_FAVORITE_LOADER:

                if (data != null)
                    updateAdapter(data);
                if (mPosition != ListView.INVALID_POSITION) {
                    // If we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    recyclerView.smoothScrollToPosition(mPosition);
                }

                break;
            default:break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateAdapter(null);
    }
    //endregion

    //region MovieAdapter
    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

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
        public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.movie_item, parent, false);
            MovieAdapter.ViewHolder vh = new MovieAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MovieAdapter.ViewHolder holder, final int position) {
            if (mCursor.moveToPosition(position)) {

                final long id = mCursor.getLong(mCursor.getColumnIndex(MovieContract.MovieEntry._ID));
                final int dataId = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
                final String posterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL));

                Picasso.with(mContext)
                        .load(Constant.MOVIE_DB_IMAGE_PATH + posterPath)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.imageView);

                if (mTwoPane){
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = null;
                            if (!currentState)
                                uri = MovieContract.MovieEntry.buildMovieWithTrailerAndReview(id, dataId);
                            else uri = MovieContract.MovieFavoriteEntry.buildMovieWithTrailerAndReview(id, dataId);

                            SyncAdapter.syncImmediately(mContext, Constant.USED_INT_PARAM_DETAIL, dataId, uri);
                            mPosition = position;

                            Bundle arguments = new Bundle();
                            arguments.putParcelable(MovieDetailFragment.DETAIL_URI, uri);

                            MovieDetailFragment fragment = new MovieDetailFragment();
                            fragment.setArguments(arguments);

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        }
                    });
                } else {
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Uri uri = null;
                            if (!currentState)
                                uri = MovieContract.MovieEntry.buildMovieWithTrailerAndReview(id, dataId);
                            else uri = MovieContract.MovieFavoriteEntry.buildMovieWithTrailerAndReview(id, dataId);

                            SyncAdapter.syncImmediately(mContext, Constant.USED_INT_PARAM_DETAIL, dataId, uri);
                            mPosition = position;

                            Intent intent = new Intent(mContext, DetailActivity.class);
                            intent.setData(uri);
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.movie_image);
            }
        }
    }
    //endregion

}
