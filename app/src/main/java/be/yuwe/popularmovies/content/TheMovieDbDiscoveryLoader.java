package be.yuwe.popularmovies.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import be.yuwe.popularmovies.control.Preferences;

public class TheMovieDbDiscoveryLoader extends TheMovieDbLoader<ArrayList<MovieContract.MovieSummary>> {
    private final static String LOG_TAG = TheMovieDbDiscoveryLoader.class.getSimpleName();
    private final static String DISCOVER_MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    private final static String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public TheMovieDbDiscoveryLoader(Context context) {
        super(context);
    }

    @Override
    String getLogTag() {
        return LOG_TAG;
    }

    @Override
    public ArrayList<MovieContract.MovieSummary> loadInBackground() {
        isFirstLoad = false;
        final Preferences.SORT_ORDER sortOrder = Preferences.getSortOrder(getContext());
        ArrayList<MovieContract.MovieSummary> favoriteMovies = getFavoriteMovies();
        if (sortOrder == Preferences.SORT_ORDER.BY_FAVORITES)
            return favoriteMovies;
        long[] favoriteIds = new long[favoriteMovies.size()];
        for (int i = 0; i < favoriteMovies.size(); i++) {
            favoriteIds[i] = favoriteMovies.get(i).getId();
        }
        Arrays.sort(favoriteIds);
        ArrayList<MovieContract.MovieSummary> popularMovies = new ArrayList<MovieContract.MovieSummary>();
        for (int page = 1; page < 4; page++) {
            final ArrayList<MovieContract.MovieSummary> movies = fetchMovieSummaries(page);
            if (movies == null)
                break;
            popularMovies.addAll(movies);
        }
        for (MovieContract.MovieSummary movie : popularMovies) {
            final int searchResult = Arrays.binarySearch(favoriteIds, movie.getId());
            if (searchResult >= 0)
                movie.setFavorite(true);
        }
        return popularMovies;
    }

    private ArrayList<MovieContract.MovieSummary> getFavoriteMovies() {
        Uri uri = MovieContract.MovieSummary.getContentUri();
        final Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        final ArrayList<MovieContract.MovieSummary> movieSummaries = new ArrayList<MovieContract.MovieSummary>(cursor.getCount());
        while (cursor.moveToNext()) {
            final MovieContract.MovieSummary movieSummary = new MovieContract.MovieSummary(cursor);
            movieSummaries.add(movieSummary);
        }
        Log.d(LOG_TAG, "Got " + movieSummaries.size() + " favorite movies from provider.");
        return movieSummaries;
    }

    @Nullable
    private ArrayList<MovieContract.MovieSummary> fetchMovieSummaries(int page) {
        try {
            String sortOrder = getSortOrder();
            Uri uri = Uri.parse(DISCOVER_MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter("sort_by", sortOrder)
                    .appendQueryParameter("page", Integer.toString(page))
                    .appendQueryParameter("video", "false")
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
            URL url = new URL(uri.toString());
            JSONObject jsonAnswer = fetchJson(url);
            ArrayList<MovieContract.MovieSummary> popularMovies = parsePopularMoviesJson(jsonAnswer);
            return popularMovies;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to fetch popular movies data", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while parsing json response :" + e.getMessage(), e);
        }
        return null;
    }

    private String getSortOrder() {
        final Preferences.SORT_ORDER sortOrder = Preferences.getSortOrder(getContext());
        switch (sortOrder) {
            case BY_RATING:
                return "vote_average.desc";
            case BY_POPULARITY:
            default:
                return "popularity.desc";
        }
    }

    private String strip(String posterPath) {
        return posterPath.substring(posterPath.lastIndexOf("/"));
    }

    private ArrayList<MovieContract.MovieSummary> parsePopularMoviesJson(JSONObject answer) throws JSONException {
        if (answer == null)
            return null;
        final JSONArray results = answer.getJSONArray("results");
        final int movieCount = results.length();
        ArrayList<MovieContract.MovieSummary> movieSummaries = new ArrayList<MovieContract.MovieSummary>(movieCount);
        String sizePath = getPosterSizePath();
        for (int i = 0; i < movieCount; i++) {
            final JSONObject movieJson = results.getJSONObject(i);
            final long id = movieJson.getLong("id");
            final String posterPath = movieJson.getString("poster_path");
            final String title = movieJson.getString("title");
            final double voteAverage = movieJson.getDouble("vote_average");
            final String plot = movieJson.getString("overview");
            final String releaseDateText = movieJson.getString("release_date");
            Date releaseDate = null;
            try {
                releaseDate = dateFormat.parse(releaseDateText);
            } catch (ParseException e) {
            }
            String fullPosterPath = constructFullPosterpath(sizePath, posterPath); //TODO: html encode for safety
            boolean favorite = false;
            final MovieContract.MovieSummary movieSummary = new MovieContract.MovieSummary(id, title, fullPosterPath, releaseDate, voteAverage, plot, favorite);
            movieSummaries.add(movieSummary);
        }
        return movieSummaries;
    }

    @Nullable
    private String constructFullPosterpath(String sizePath, String posterPath) {
        if (posterPath == null || "null".equalsIgnoreCase(posterPath))
            return null;
        return POSTER_IMAGE_BASE_URL + sizePath + posterPath;
    }

    private String getPosterSizePath() {
        //TODO: make a difference
        return "w342";
    }
}
