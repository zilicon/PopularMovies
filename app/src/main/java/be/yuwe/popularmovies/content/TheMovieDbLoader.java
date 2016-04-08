package be.yuwe.popularmovies.content;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.TimeUnit;

abstract public class TheMovieDbLoader<T> extends AsyncTaskLoader<T> {
    protected final static String API_KEY = ;

    private static RequestQueue jsonRequestQueue;
    protected boolean isFirstLoad = true;

    public TheMovieDbLoader(Context context) {
        super(context);
        if (TheMovieDbLoader.jsonRequestQueue == null)
            TheMovieDbLoader.jsonRequestQueue = Volley.newRequestQueue(context);

    }

    protected JSONObject fetchJson(URL url) {
        if (url == null)
            return null;
        Log.d(getLogTag(), "Downloading from " + (url.toString()));
        final RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(url.toString(), requestFuture, requestFuture);
        jsonRequestQueue.add(jsonRequest);
        try {
            final JSONObject jsonObject = requestFuture.get(15, TimeUnit.SECONDS);
            return jsonObject;
        } catch (Exception e) {
            Log.e(getLogTag(), "Failed to fetch json from url " + url.getHost() + url.getPath(), e);
            return null;
        }
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

    abstract String getLogTag();
}
