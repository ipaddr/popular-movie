package id.ipaddr.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import id.ipaddr.popularmovie.data.MovieContract;

/**
 * Created by ulfiaizzati on 10/27/16.
 */

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = MovieDetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";
    private Uri mUri;
    public static final int MOVIE_DETAIL_LOADER = 1;
    private boolean isFavorite = false;

    private TextView title, releaseDate, plotSynopsis;
    private FrameLayout clickMarkAsFavorite;
    private ImageView startFavoriteImg;
    private RatingBar rating;
    private ImageView poster;
    private ListView movies, reviews;

    private Set<String> moviesKey = new HashSet<>();
    private Set<Pair<String, String>> reviewsAuthorAndContent = new HashSet<>();

    public MovieDetailFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DETAIL_URI)) {
            mUri = arguments.getParcelable(DETAIL_URI);
            getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_movie_detail, container, false);
        title = (TextView)rootView.findViewById(R.id.title);
        releaseDate = (TextView)rootView.findViewById(R.id.releaseDate);
        poster = (ImageView)rootView.findViewById(R.id.poster);
        clickMarkAsFavorite = (FrameLayout)rootView.findViewById(R.id.mark_as_favorite);
        startFavoriteImg = (ImageView)rootView.findViewById(R.id.mark_as_favorite_img);
        rating = (RatingBar)rootView.findViewById(R.id.rating);
        plotSynopsis = (TextView)rootView.findViewById(R.id.synopsis);

        movies = (ListView)rootView.findViewById(R.id.movies);
        reviews = (ListView)rootView.findViewById(R.id.reviews);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //region loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        if (mCursor != null && mCursor.moveToFirst() && getView() != null){

            processDetail(mCursor);
            processTrailer(mCursor);
            processReview(mCursor);

            while (mCursor.moveToNext()){
                processTrailer(mCursor);
                processReview(mCursor);
            }

            movies.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(moviesKey)){
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder.ViewHolderTrailer vht;
                    if (convertView == null){
                        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_item, null);
                        vht = new ViewHolder.ViewHolderTrailer(convertView);
                        convertView.setTag(vht);
                    } else {
                        vht = (ViewHolder.ViewHolderTrailer) convertView.getTag();
                    }

                    vht.tv.setText(getItem(position));

                    return convertView;
                }
            });
            movies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder.ViewHolderTrailer vht = (ViewHolder.ViewHolderTrailer) view.getTag();
                    String movieId = vht.tv.getText().toString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+movieId)));
                }
            });

            reviews.setAdapter(new ArrayAdapter<Pair<String, String>>(getActivity(), android.R.layout.simple_list_item_2, new ArrayList<Pair<String, String>>(reviewsAuthorAndContent)){
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder.ViewHolderReview vhr;
                    if (convertView == null){
                        convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_2, parent, false);
                        vhr = new ViewHolder.ViewHolderReview(convertView);
                        convertView.setTag(vhr);
                    } else {
                        vhr = (ViewHolder.ViewHolderReview) convertView.getTag();
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
    public void onLoaderReset(Loader<Cursor> loader) {}
    //endregion

    private void processDetail(Cursor mCursor){
        final String movieId = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
        final String title = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE));
        final String releaseDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
        final String posterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL));
        final String voteAverage = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE));
        final double rating = Double.valueOf(voteAverage);
        final String plotSynopsys = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS));

        this.title.setText(title);
        this.releaseDate.setText(releaseDate);
        Picasso.with(getActivity())
                .load(Constant.MOVIE_DB_IMAGE_PATH + posterPath)
                .placeholder(R.mipmap.ic_launcher)
                .into(poster);
        this.rating.setRating((float)rating);
        this.plotSynopsis.setText(plotSynopsys);

        final Long movId = Long.parseLong(movieId);
        final String selection = MovieContract.MovieFavoriteEntry.COLUMN_NAME_ID + " = ?";
        final String [] selectionArg = new String[]{movieId};
        final Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE
                , MovieContract.MovieFavoriteEntry.PROJECTION
                , selection, selectionArg, null);
        if (!cursor.moveToFirst()){
            isFavorite = false;
            startFavoriteImg.setImageResource(android.R.drawable.btn_star_big_off);
        } else {
            isFavorite = true;
            startFavoriteImg.setImageResource(android.R.drawable.btn_star_big_on);
        }
        cursor.close();

        this.clickMarkAsFavorite.setTag(movieId);
        this.clickMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite){
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_ID, movId);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_ORIGINAL_TITLE, title);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_IMAGE_THUMBNAIL, posterPath);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_PLOT_SYNOPSIS, plotSynopsys);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_RELEASE_DATE, releaseDate);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
                    getContext().getContentResolver().insert(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE, values);
                    isFavorite = true;
                } else {
                    getContext().getContentResolver().delete(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE, selection, selectionArg);
                    isFavorite = false;
                }
            }
        });
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
}
