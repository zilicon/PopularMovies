package be.yuwe.popularmovies.control;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.ArrayList;

import be.yuwe.popularmovies.R;
import be.yuwe.popularmovies.content.CachingImageLoader;
import be.yuwe.popularmovies.content.MovieContract;
import be.yuwe.popularmovies.content.TheMovieDbDetailsLoader;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieContract.Movie> {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAILS_LOADER = 0;

    private TextView movieTitle;
    private ImageView posterImage;
    private TextView ratingLabel;
    private TextView releaseLabel;
    private TextView plotText;
    private ToggleButton favoriteStar;
    private GridView trailerList;
    private ListView reviewList;

    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private MovieContract.MovieSummary movieSummary;
    private ArrayAdapter<MovieContract.Trailer> trailerAdapter;
    private ArrayAdapter<MovieContract.Review> reviewAdapter;

    private ArrayList<MovieContract.Trailer> trailers;
    private ArrayList<MovieContract.Review> reviews;

    private ShareActionProvider videoShareActionProvider;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        movieTitle = (TextView) fragment.findViewById(R.id.movieTitleLabel);
        posterImage = (ImageView) fragment.findViewById(R.id.posterImage);
        posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        ratingLabel = (TextView) fragment.findViewById(R.id.ratingLabel);
        releaseLabel = (TextView) fragment.findViewById(R.id.releaseLabel);
        plotText = (TextView) fragment.findViewById(R.id.plotText);
        favoriteStar = (ToggleButton) fragment.findViewById(R.id.starButton);
        favoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = favoriteStar.isChecked();
                updateFavorite(isChecked);
            }
        });
        trailerList = (GridView) fragment.findViewById(R.id.trailerList);
        createTrailerAdapter();
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMovie(position);
            }
        });
        reviewList = (ListView) fragment.findViewById(R.id.reviewList);
        createReviewAdapter();
        return fragment;
    }

    private void showMovie(int position) {
        final MovieContract.Trailer trailer = trailers.get(position);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
            startActivity(intent);
        }
    }


    private Intent createShareMovieIntent() {
        if (trailers == null || trailers.size() == 0)
            return null;
        final MovieContract.Trailer trailer = trailers.get(0);
        String shareText = "Check the " + trailer.getType() + " of movie " + movieSummary.getTitle() + ": http://www.youtube.com/watch?v=" + trailer.getKey();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share_video);
        videoShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        videoShareActionProvider.setShareIntent(createShareMovieIntent());
    }


    @NonNull
    private void createTrailerAdapter() {
        trailerAdapter = new ArrayAdapter<MovieContract.Trailer>(getActivity(), R.layout.list_item_trailer) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (trailers.size() > 1)
                    textView.setText(textView.getText() + " " + (position + 1));
                return textView;
            }
        };
        trailerList.setAdapter(trailerAdapter);
    }

    private void createReviewAdapter() {
        reviewAdapter = new ArrayAdapter<MovieContract.Review>(getActivity(), R.layout.list_item_review, R.id.reviewText);
        reviewList.setAdapter(reviewAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MovieContract.MovieSummary movieSummary = getArguments() == null ? null : (MovieContract.MovieSummary) getArguments().getParcelable(MovieContract.MovieSummary.class.getName());
        if (movieSummary == null)
            movieSummary = getActivity().getIntent().getParcelableExtra(MovieContract.MovieSummary.class.getName());
        setMovieSummary(movieSummary);
        super.onActivityCreated(savedInstanceState);
    }

    private void setMovieSummary(MovieContract.MovieSummary movieSummary) {
        this.movieSummary = movieSummary;
        movieTitle.setText(movieSummary.getTitle());
        if (movieSummary.getPosterPath() != null)
            CachingImageLoader.loadImage(getActivity(), movieSummary.getPosterPath(), movieSummary.getTitle(), posterImage);
        ratingLabel.setText("Rating: " + Math.round(movieSummary.getVoteAverage()));
        releaseLabel.setText(movieSummary.getReleaseDate() == null ? "" : dateFormat.format(movieSummary.getReleaseDate()));
        plotText.setText(movieSummary.getPlot());
        favoriteStar.setChecked(movieSummary.isFavorite());
        getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
    }

    private void updateFavorite(boolean isFavorite) {
        favoriteStar.setChecked(isFavorite);
        movieSummary.setFavorite(isFavorite);
        if (isFavorite) {
            ContentValues contentValues = new ContentValues();
            movieSummary.writeToContentValues(contentValues);
            getActivity().getContentResolver().insert(MovieContract.MovieSummary.getContentUri(movieSummary.getId()), contentValues);
        } else
            getActivity().getContentResolver().delete(MovieContract.MovieSummary.getContentUri(movieSummary.getId()), null, null);
    }

    private void toggleFavorite() {
        updateFavorite(!movieSummary.isFavorite());
    }


    @Override
    public Loader<MovieContract.Movie> onCreateLoader(int loaderId, Bundle args) {
        long theMovieDbId = movieSummary.getId();
        final TheMovieDbDetailsLoader detailsLoader = new TheMovieDbDetailsLoader(getActivity(), theMovieDbId);
        return detailsLoader;
    }

    @Override
    public void onLoadFinished(Loader<MovieContract.Movie> loader, MovieContract.Movie movie) {
        if (movie == null) {
            setTrailers(null);
            setReviews(null);
        } else {
            setTrailers(movie.getTrailers());
            setReviews(movie.getReviews());
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieContract.Movie> loader) {
        setTrailers(null);
        setReviews(null);
    }

    public void setTrailers(ArrayList<MovieContract.Trailer> trailers) {
        this.trailers = trailers;
        trailerAdapter.clear();
        if (trailers != null)
            trailerAdapter.addAll(trailers);
        videoShareActionProvider.setShareIntent(createShareMovieIntent());
    }

    public void setReviews(ArrayList<MovieContract.Review> reviews) {
        Log.d(LOG_TAG, "Got " + (reviews == null ? 0 : reviews.size()) + " reviews.");
        this.reviews = reviews;
        reviewAdapter.clear();
        if (reviews != null)
            reviewAdapter.addAll(reviews);
    }
}
