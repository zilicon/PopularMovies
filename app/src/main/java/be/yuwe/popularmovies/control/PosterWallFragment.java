package be.yuwe.popularmovies.control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import be.yuwe.popularmovies.R;
import be.yuwe.popularmovies.content.MovieContract;
import be.yuwe.popularmovies.content.TheMovieDbDiscoveryLoader;
import be.yuwe.popularmovies.view.MoviePosterAdapter;

public class PosterWallFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<MovieContract.MovieSummary>> {
    private static final String LOG_TAG = PosterWallFragment.class.getSimpleName();
    private static final int DISCOVER_MOVIES_LOADER = 0;

    interface Callbacks {
        void showMovieDetail(MovieContract.MovieSummary selectedMovie);
    }

    private MoviePosterAdapter moviesAdapter;
    private GridView grid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View posterWallFragment = inflater.inflate(R.layout.fragment_poster_wall, container);
        grid = (GridView) posterWallFragment.findViewById(R.id.posterGrid);
        moviesAdapter = new MoviePosterAdapter(getActivity());
        grid.setAdapter(moviesAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Callbacks callbacks = (Callbacks) getActivity();
                MovieContract.MovieSummary selectedMovie = moviesAdapter.getMovieSummary(position);
                callbacks.showMovieDetail(selectedMovie);
            }
        });
        return posterWallFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DISCOVER_MOVIES_LOADER, savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        updateMovieList();
        super.onResume();
    }

    @Override
    public Loader<ArrayList<MovieContract.MovieSummary>> onCreateLoader(int id, Bundle args) {
        final TheMovieDbDiscoveryLoader moviesLoader = new TheMovieDbDiscoveryLoader(getActivity());
        return moviesLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieContract.MovieSummary>> loader, ArrayList<MovieContract.MovieSummary> movieSummaries) {
        moviesAdapter.setMovieSummaries(movieSummaries);
        if (movieSummaries == null || movieSummaries.size() == 0)
            Toast.makeText(getActivity(), R.string.result_no_movies, Toast.LENGTH_LONG).show();
        if (grid != null)
            grid.smoothScrollToPosition(0);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieContract.MovieSummary>> loader) {
        if (moviesAdapter == null) return;
        moviesAdapter.setMovieSummaries(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_wall, menu);
        final Preferences.SORT_ORDER sortOrder = Preferences.getSortOrder(getActivity());
        if (sortOrder == Preferences.SORT_ORDER.BY_POPULARITY) {
            final MenuItem popularityButton = menu.findItem(R.id.action_sort_by_popularity);
            popularityButton.setChecked(true);
        } else if (sortOrder == Preferences.SORT_ORDER.BY_RATING) {
            final MenuItem ratingButton = menu.findItem(R.id.action_sort_by_rating);
            ratingButton.setChecked(true);
        } else if (sortOrder == Preferences.SORT_ORDER.BY_FAVORITES) {
            final MenuItem favoritesButton = menu.findItem(R.id.action_show_favorites);
            favoritesButton.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_sort_by_popularity):
                item.setChecked(true);
                Preferences.setSortOrder(getActivity(), Preferences.SORT_ORDER.BY_POPULARITY);
                updateMovieList();
                break;
            case (R.id.action_sort_by_rating):
                item.setChecked(true);
                Preferences.setSortOrder(getActivity(), Preferences.SORT_ORDER.BY_RATING);
                updateMovieList();
                break;
            case (R.id.action_show_favorites):
                item.setChecked(true);
                Preferences.setSortOrder(getActivity(), Preferences.SORT_ORDER.BY_FAVORITES);
                updateMovieList();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void updateMovieList() {
        Log.d(LOG_TAG, "Requesting to refresh my movies");
        moviesAdapter.setMovieSummaries(null);
        getLoaderManager().restartLoader(DISCOVER_MOVIES_LOADER, null, this);
    }


}
