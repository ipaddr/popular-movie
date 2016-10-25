package id.ipaddr.popularmovie;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import id.ipaddr.popularmovie.data.MovieContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView title, releaseDate, plotSynopsis;
    private RatingBar rating;
    private ImageView poster;
    private ExpandableListView movies, reviews;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = (TextView)findViewById(R.id.title);
        releaseDate = (TextView)findViewById(R.id.releaseDate);
        poster = (ImageView)findViewById(R.id.poster);
        rating = (RatingBar)findViewById(R.id.rating);
        plotSynopsis = (TextView)findViewById(R.id.synopsis);

        movies = (ExpandableListView)findViewById(R.id.movies);
        reviews = (ExpandableListView)findViewById(R.id.reviews);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null){
            mUri = intent.getData();
            getSupportLoaderManager().initLoader(1, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        if (mCursor != null && mCursor.moveToFirst()){
            final String title = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE));
            final String releaseDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
            final String posterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL));
            final String voteAverage = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE));
            final double rating = Double.valueOf(voteAverage);
            final String plotSynopsys = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS));

            this.title.setText(title);
            this.releaseDate.setText(releaseDate);
            Picasso.with(DetailActivity.this)
                    .load(Constant.MOVIE_DB_IMAGE_PATH + posterPath)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(poster);
            this.rating.setRating((float)rating);
            this.plotSynopsis.setText(plotSynopsys);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
