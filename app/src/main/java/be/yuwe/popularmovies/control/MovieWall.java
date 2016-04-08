package be.yuwe.popularmovies.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import be.yuwe.popularmovies.R;
import be.yuwe.popularmovies.content.MovieContract;

public class MovieWall extends ActionBarActivity implements PosterWallFragment.Callbacks {
    private static final String LOG_TAG = MovieWall.class.getSimpleName();
    private View movieDetailFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_wall);
        movieDetailFrame = findViewById(R.id.movie_detail_frame);
        isMasterDetailLayout();
    }

    private boolean isMasterDetailLayout() {
        Log.d(LOG_TAG, "Is master/detail " + (movieDetailFrame != null));
        return movieDetailFrame != null;
    }

    @Override
    public void showMovieDetail(MovieContract.MovieSummary selectedMovie) {
        if (isMasterDetailLayout()) {
            MovieDetailFragment detailFragment = new MovieDetailFragment();
            Bundle fragmentArguments = new Bundle();
            fragmentArguments.putParcelable(MovieContract.MovieSummary.class.getName(), selectedMovie);
            detailFragment.setArguments(fragmentArguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_frame, detailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetail.class);
            intent.putExtra(MovieContract.MovieSummary.class.getName(), selectedMovie);
            startActivity(intent);
        }

    }
}
