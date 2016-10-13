package id.ipaddr.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView title, releaseDate, plotSynopsis;
    private RatingBar rating;
    private ImageView poster;

    private String sTitle, sReleaseDate, sPlaySysnopsys, sPoster;
    private Double sRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = (TextView)findViewById(R.id.title);
        releaseDate = (TextView)findViewById(R.id.releaseDate);
        poster = (ImageView)findViewById(R.id.poster);
        rating = (RatingBar)findViewById(R.id.rating);
        plotSynopsis = (TextView)findViewById(R.id.synopsis);


        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra(Constant.EXTRA_TITLE)
                && intent.hasExtra(Constant.EXTRA_RELEASE_DATE)
                && intent.hasExtra(Constant.EXTRA_POSTER)
                && intent.hasExtra(Constant.EXTRA_VOTE_AVERAGE)
                && intent.hasExtra(Constant.EXTRA_PLOT_SYNOPSYS)){

                sTitle = intent.getStringExtra(Constant.EXTRA_TITLE);
                title.setText(sTitle);
                sReleaseDate = intent.getStringExtra(Constant.EXTRA_RELEASE_DATE);
                releaseDate.setText(sReleaseDate);
                sPoster = intent.getStringExtra(Constant.EXTRA_POSTER);
                Picasso.with(DetailActivity.this)
                        .load(MovieDBIntentService.MOVIE_DB_IMAGE_PATH + sPoster)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(poster);
                sRating = intent.getDoubleExtra(Constant.EXTRA_VOTE_AVERAGE, 0.0);
                rating.setRating((float)sRating.doubleValue());
                sPlaySysnopsys = intent.getStringExtra(Constant.EXTRA_PLOT_SYNOPSYS);
                plotSynopsis.setText(sPlaySysnopsys);
            } else {

            }
        }

    }
}
