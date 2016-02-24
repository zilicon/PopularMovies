package be.yuwe.popularmovies.content;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import be.yuwe.popularmovies.control.Preferences;

public class TheMovieDbLoader extends AsyncTaskLoader<ArrayList<MovieContract.MovieSummary>> {
    private final static String LOG_TAG = TheMovieDbLoader.class.getSimpleName();
    private final static String API_KEY = ;
    private final static String DISCOVER_MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    private final static String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static RequestQueue requestQueue;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isFirstLoad = true;

    public TheMovieDbLoader(Context context) {
        super(context);
        if (TheMovieDbLoader.requestQueue==null)
            TheMovieDbLoader.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    protected void onStartLoading() {
        if (isFirstLoad)
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public ArrayList<MovieContract.MovieSummary> loadInBackground() {
        isFirstLoad = false;
        ArrayList<MovieContract.MovieSummary> allMovies = new ArrayList<MovieContract.MovieSummary>();
        final Preferences.SORT_ORDER sortOrder = Preferences.getSortOrder(getContext());
        if (sortOrder == Preferences.SORT_ORDER.BY_FAVORITES) {
            allMovies = getFavoriteMovies();
        } else {
            for (int page = 1; page < 4; page++) {
                final ArrayList<MovieContract.MovieSummary> movies = fetchMovieSummaries(page);
                if (movies != null)
                    allMovies.addAll(movies);
            }
        }
        return allMovies;
    }


    private ArrayList<MovieContract.MovieSummary> getFavoriteMovies() {
        Uri uri = null;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;
        //getContext().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        return null;
    }

    @Nullable
    private ArrayList<MovieContract.MovieSummary> fetchMovieSummaries(int page) {
        Uri uri = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            String sortOrder = getSortOrder();
            uri = Uri.parse(DISCOVER_MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter("sort_by", sortOrder)
                    .appendQueryParameter("page", Integer.toString(page))
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
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.d(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    public JSONObject fetchJson(URL url) {
        if (url==null)
            return null;
        final RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(url.toString(), requestFuture, requestFuture);
        requestQueue.add(jsonRequest);
        try {
            final JSONObject jsonObject = requestFuture.get(15, TimeUnit.SECONDS);
            return jsonObject;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to fetch json from url "+url.getHost()+url.getPath(), e);
            return null;
        }
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
            final MovieContract.MovieSummary movieSummary = MovieContract.MovieSummary.create(id, title, fullPosterPath, releaseDate, voteAverage, plot, favorite);
            movieSummaries.add(movieSummary);
        }
        return movieSummaries;
    }

    @Nullable
    private String constructFullPosterpath(String sizePath, String posterPath) {
        if (posterPath==null || "null".equalsIgnoreCase(posterPath))
            return null;
        return POSTER_IMAGE_BASE_URL + sizePath + posterPath;
    }

    private String getPosterSizePath() {
        //TODO: make a difference
        return "w342";
    }
}
