package id.ipaddr.popularmovie;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import id.ipaddr.popularmovie.data.MovieContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView title, releaseDate, plotSynopsis;
    private RatingBar rating;
    private ImageView poster;

    private ListView movies, reviews;

    private Set<String> moviesKey = new HashSet<>();
    private Set<Pair<String, String>> reviewsAuthorAndContent = new HashSet<>();

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

        movies = (ListView)findViewById(R.id.movies);
        reviews = (ListView)findViewById(R.id.reviews);

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

            processDetail(mCursor);
            processTrailer(mCursor);
            processReview(mCursor);

            while (mCursor.moveToNext()){
                processTrailer(mCursor);
                processReview(mCursor);
            }
            Log.d("Test", "Test");
            movies.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(moviesKey)){
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolderTrailer vht;
                    if (convertView == null){
                        convertView = LayoutInflater.from(DetailActivity.this).inflate(R.layout.trailer_item, null);
                        vht = new ViewHolderTrailer(convertView);
                        convertView.setTag(vht);
                    } else {
                        vht = (ViewHolderTrailer) convertView.getTag();
                    }

                    vht.tv.setText(getItem(position));

                    return convertView;
                }
            });
            movies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolderTrailer vht = (ViewHolderTrailer) view.getTag();
                    String movieId = vht.tv.getText().toString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+movieId)));
                }
            });

            reviews.setAdapter(new ArrayAdapter<Pair<String, String>>(DetailActivity.this, android.R.layout.simple_list_item_2, new ArrayList<Pair<String, String>>(reviewsAuthorAndContent)){
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolderReview vhr;
                    if (convertView == null){
                        convertView = LayoutInflater.from(DetailActivity.this).inflate(android.R.layout.simple_list_item_2, parent, false);
                        vhr = new ViewHolderReview(convertView);
                        convertView.setTag(vhr);
                    } else {
                        vhr = (ViewHolderReview) convertView.getTag();
                    }

                    Pair<String, String> data = getItem(position);

                    vhr.tv1.setText(data.first);
                    vhr.tv2.setText(data.second);

                    return convertView;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void processDetail(Cursor mCursor){
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

    private void processTrailer(Cursor cursor){
        final String videoCode = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY));
        if (videoCode != null && !moviesKey.contains(videoCode))
            moviesKey.add(videoCode);
    }

    private void processReview(Cursor cursor){
        final String reviewAuthor = cursor.getString(cursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR));
        final String reviewContent = cursor.getString(cursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT));
        if (reviewAuthor != null && reviewContent != null){
            boolean isExist = false;
            for (Pair<String, String> data : reviewsAuthorAndContent){
                if (data.first.equalsIgnoreCase(reviewAuthor) && data.second.equalsIgnoreCase(reviewContent))
                    isExist = true;
            }
            if (!isExist)
                reviewsAuthorAndContent.add(new Pair<String, String>(reviewAuthor, reviewContent));
        }
    }

    static class ViewHolderTrailer{
        TextView tv;
        public ViewHolderTrailer(View view){
            tv = (TextView)view.findViewById(R.id.trailer);
        }
    }

    static class ViewHolderReview{
        TextView tv1;
        TextView tv2;
        public ViewHolderReview(View view){
            tv1 = (TextView)view.findViewById(android.R.id.text1);
            tv2 = (TextView)view.findViewById(android.R.id.text2);
        }
    }
}
