package be.yuwe.popularmovies.control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import be.yuwe.popularmovies.R;
import be.yuwe.popularmovies.content.CachingImageLoader;
import be.yuwe.popularmovies.content.MovieContract;


public class MovieDetailFragment extends Fragment {
    private TextView movieTitle;
    private ImageView posterImage;
    private TextView ratingLabel;
    private TextView releaseLabel;
    private TextView plotText;

    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        movieTitle = (TextView) fragment.findViewById(R.id.movieTitleLabel);
        posterImage = (ImageView) fragment.findViewById(R.id.posterImage);
        ratingLabel = (TextView) fragment.findViewById(R.id.ratingLabel);
        releaseLabel = (TextView) fragment.findViewById(R.id.releaseLabel);
        plotText = (TextView) fragment.findViewById(R.id.plotText);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        final MovieContract.MovieSummary movieSummary = getActivity().getIntent().getParcelableExtra(MovieContract.MovieSummary.class.getName());
        setMovieSummary(movieSummary);
        super.onActivityCreated(savedInstanceState);
    }

    private void setMovieSummary(MovieContract.MovieSummary movieSummary) {
        movieTitle.setText(movieSummary.getTitle());
        if (movieSummary.getPosterPath()!=null)
            CachingImageLoader.loadImage(getActivity(), movieSummary.getPosterPath(), movieSummary.getTitle(), posterImage);
        ratingLabel.setText("Rating: " + Math.round(movieSummary.getVoteAverage()));
        releaseLabel.setText(movieSummary.getReleaseDate()==null?"":dateFormat.format(movieSummary.getReleaseDate()));
        plotText.setText(movieSummary.getPlot());
    }
}
