package be.yuwe.popularmovies.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import be.yuwe.popularmovies.R;
import be.yuwe.popularmovies.content.MovieContract;
import be.yuwe.popularmovies.content.TheMovieDbLoader;
import be.yuwe.popularmovies.view.MoviePosterAdapter;

public class PosterWallFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<MovieContract.MovieSummary>> {
    private static final String LOG_TAG = PosterWallFragment.class.getSimpleName();

    private static final int POPULAR_MOVIES_LOADER = 0;

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
                Intent intent = new Intent(getActivity(), MovieDetail.class);
                MovieContract.MovieSummary selectedSummary = moviesAdapter.getMovieSummary(position);
                intent.putExtra(MovieContract.MovieSummary.class.getName(), selectedSummary);
                startActivity(intent);
            }
        });
        return posterWallFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_MOVIES_LOADER, savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<ArrayList<MovieContract.MovieSummary>> onCreateLoader(int id, Bundle args) {
        final TheMovieDbLoader moviesLoader = new TheMovieDbLoader(getActivity());
        return moviesLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieContract.MovieSummary>> loader, ArrayList<MovieContract.MovieSummary> movieSummaries) {
        if (moviesAdapter == null) return;
        moviesAdapter.setMovieSummaries(movieSummaries);
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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        if (grid!=null)
            grid.smoothScrollToPosition(0);
        return true;
    }

    private void updateMovieList() {
        getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
    }


}
