package be.yuwe.popularmovies.control;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import be.yuwe.popularmovies.R;

public class MovieWall extends ActionBarActivity {
    private static final String LOG_TAG = MovieWall.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_wall);
    }


}
