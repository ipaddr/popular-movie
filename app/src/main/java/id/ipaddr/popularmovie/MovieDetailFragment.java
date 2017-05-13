package id.ipaddr.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.List;
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
    private RecyclerView movies, reviews;

    private List<String> moviesKey = new ArrayList<>();
    private List<Pair<String, String>> reviewsAuthorAndContent = new ArrayList<>();

    public MovieDetailFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DETAIL_URI)) {
            mUri = arguments.getParcelable(DETAIL_URI);
            getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }
        setHasOptionsMenu(true);
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

        movies = (RecyclerView)rootView.findViewById(R.id.movies);
        reviews = (RecyclerView)rootView.findViewById(R.id.reviews);

        RecyclerView.LayoutManager layoutManagerMovies = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager layoutManagerReviews = new LinearLayoutManager(getActivity());

        movies.setLayoutManager(layoutManagerMovies);
        reviews.setLayoutManager(layoutManagerReviews);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        final ShareActionProvider myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (moviesKey.size() > 0){
            for (String s: moviesKey){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v="+s);
                PackageManager packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(sharingIntent, 0);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe && myShareActionProvider != null) {
                    myShareActionProvider.setShareIntent(sharingIntent);
                }
                break;
            }
        }

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


            movies.setAdapter(new RecyclerView.Adapter<ViewHolder.ViewHolderTrailer>(){

                @Override
                public ViewHolder.ViewHolderTrailer onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, null);
                    ViewHolder.ViewHolderTrailer vht = new ViewHolder.ViewHolderTrailer(view);
                    return vht;
                }

                @Override
                public void onBindViewHolder(ViewHolder.ViewHolderTrailer holder, int position) {
                    final String youtubeId = moviesKey.get(position);
                    holder.tv.setText(youtubeId);
                    holder.tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create the text message with a string
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+youtubeId));
                            // Verify that the intent will resolve to an activity
                            if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(sendIntent);
                            }
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    return moviesKey.size();
                }
            });

            reviews.setAdapter(new RecyclerView.Adapter<ViewHolder.ViewHolderReview>(){

                @Override
                public ViewHolder.ViewHolderReview onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_2, null);
                    ViewHolder.ViewHolderReview vhr = new ViewHolder.ViewHolderReview(view);
                    return vhr;
                }

                @Override
                public void onBindViewHolder(ViewHolder.ViewHolderReview holder, int position) {
                    final Pair<String, String> data = reviewsAuthorAndContent.get(position);
                    holder.tv1.setText(data.first);
                    holder.tv2.setText(data.second);
                }

                @Override
                public int getItemCount() {
                    return reviewsAuthorAndContent.size();
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
        startFavoriteImg.setImageResource(android.R.drawable.btn_star_big_off);
        this.rating.setRating((float)rating);
        this.plotSynopsis.setText(plotSynopsys);


        // TODO: 10/29/16 should run on background thread
        //region should on background thead
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
                    startFavoriteImg.setImageResource(android.R.drawable.btn_star_big_on);
                    isFavorite = true;
                } else {
                    getContext().getContentResolver().delete(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE, selection, selectionArg);
                    startFavoriteImg.setImageResource(android.R.drawable.btn_star_big_off);
                    isFavorite = false;
                }
                getContext().getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE,
                        MovieContract.MovieFavoriteEntry.PROJECTION,
                        null,
                        null,
                        null);
            }
        });
        //endregion
    }

    private void processTrailer(Cursor cursor){
        final String videoCode = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY));
        boolean isFind = false;
        if (videoCode != null && !TextUtils.isEmpty(videoCode)){
            for (String s : moviesKey){
                if (s.equalsIgnoreCase(videoCode)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) moviesKey.add(videoCode);
        }
    }

    private void processReview(Cursor cursor){
        final String reviewAuthor = cursor.getString(cursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR));
        final String reviewContent = cursor.getString(cursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT));
        if (reviewAuthor != null && reviewContent != null){
            boolean isFind = false;
            for (Pair<String, String> data : reviewsAuthorAndContent){
                if (data.first.equalsIgnoreCase(reviewAuthor) && data.second.equalsIgnoreCase(reviewContent)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
                reviewsAuthorAndContent.add(new Pair<String, String>(reviewAuthor, reviewContent));
        }
    }
}
