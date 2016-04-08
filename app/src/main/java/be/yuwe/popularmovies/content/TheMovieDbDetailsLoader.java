package be.yuwe.popularmovies.content;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class TheMovieDbDetailsLoader extends TheMovieDbLoader<MovieContract.Movie> {
    private final static String LOG_TAG = TheMovieDbDetailsLoader.class.getSimpleName();
    private static final String QUERY_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
    private final long theMovieDbId;

    public TheMovieDbDetailsLoader(Context context, long theMovieDbId) {
        super(context);
        this.theMovieDbId = theMovieDbId;
    }

    @Override
    String getLogTag() {
        return LOG_TAG;
    }

    @Override
    public MovieContract.Movie loadInBackground() {
        Log.d(LOG_TAG, "Loading movie details for movie " + theMovieDbId);
        try {
            Uri uri = Uri.parse(QUERY_MOVIES_BASE_URL).buildUpon().
                    appendPath(Long.toString(theMovieDbId)).
                    appendQueryParameter("append_to_response", "videos,reviews").
                    appendQueryParameter("api_key", API_KEY).
                    build();
            URL url = new URL(uri.toString());
            JSONObject jsonAnswer = fetchJson(url);
            if (jsonAnswer == null)
                return null;
            ArrayList<MovieContract.Trailer> trailers = parseTrailers(jsonAnswer);
            final ArrayList<MovieContract.Review> reviews = parseReviews(jsonAnswer);
            final MovieContract.Movie movie = new MovieContract.Movie(theMovieDbId, trailers, reviews);
            return movie;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to fetch popular movies data for movie " + Long.toString(theMovieDbId), e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while parsing json response for movie " + Long.toString(theMovieDbId) + ":" + e.getMessage(), e);
        }
        return null;
    }

    private ArrayList<MovieContract.Trailer> parseTrailers(JSONObject jsonAnswer) throws JSONException {
        jsonAnswer = jsonAnswer.getJSONObject("videos");
        if (jsonAnswer == null)
            return null;
        final JSONArray videosJson = jsonAnswer.getJSONArray("results");
        final int length = Math.min(videosJson.length(), 5);
        ArrayList<MovieContract.Trailer> trailers = new ArrayList<MovieContract.Trailer>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject trailerJson = videosJson.getJSONObject(i);
            final MovieContract.Trailer trailer = parseTrailer(trailerJson);
            trailers.add(trailer);
        }
        return trailers;
    }

    private MovieContract.Trailer parseTrailer(JSONObject trailerJson) throws JSONException {
        final String key = trailerJson.getString("key");
        final String site = trailerJson.getString("site");
        final String type = trailerJson.getString("type");
        final MovieContract.Trailer movieTrailer = new MovieContract.Trailer(key, site, type);
        return movieTrailer;
    }

    private ArrayList<MovieContract.Review> parseReviews(JSONObject jsonAnswer) throws JSONException {
        jsonAnswer = jsonAnswer.getJSONObject("reviews");
        if (jsonAnswer == null)
            return null;
        final JSONArray reviewsJson = jsonAnswer.getJSONArray("results");
        final int length = Math.min(reviewsJson.length(), 10);
        ArrayList<MovieContract.Review> reviews = new ArrayList<MovieContract.Review>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject reviewJson = reviewsJson.getJSONObject(i);
            final MovieContract.Review review = parseReview(reviewJson);
            reviews.add(review);
        }
        return reviews;
    }

    private MovieContract.Review parseReview(JSONObject reviewJson) throws JSONException {
        final String author = reviewJson.getString("author");
        final String content = reviewJson.getString("content");
        final MovieContract.Review review = new MovieContract.Review(author, content);
        return review;
    }
}
