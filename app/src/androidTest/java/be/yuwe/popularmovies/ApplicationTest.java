package be.yuwe.popularmovies;

import android.net.http.HttpResponseCache;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import be.yuwe.popularmovies.content.TheMovieDbLoader;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends AndroidTestCase {

    private static final String LOG_TAG = ApplicationTest.class.getSimpleName();


    public void installHttpCache() {
        final File cacheDir = getContext().getCacheDir();
        File httpCacheDir = new File(cacheDir, "http");
        long httpCacheSize = 50 * 1024 * 1024; // 50 MiB
        try {
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
            Log.d(LOG_TAG, "HTTP cache installed.");
        } catch (IOException e) {
            Log.d(LOG_TAG, "HTTP Cache install failed.");
        }
    }

    public void testCache() throws MalformedURLException {
        installHttpCache();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        int requestCount = cache.getRequestCount();
        int hitCount = cache.getHitCount();
        assertTrue(hitCount==0);
        final TheMovieDbLoader loader = new TheMovieDbLoader(getContext());
        //final ArrayList<MovieContract.MovieSummary> movieSummaries = loader.loadInBackground();
        String data = loader.fetchData(new URL("http://www.menstis.be/dedag/index.html"));
        requestCount = cache.getRequestCount();
        assertTrue(requestCount == 1);
        //loader.loadInBackground();
        data = loader.fetchData(new URL("http://www.menstis.be/dedag/index.html"));
        requestCount = cache.getRequestCount();
        hitCount = cache.getHitCount();
        assertTrue("request hitcount is not 2", requestCount==2);
        assertTrue("Cache hitcount is not 1", hitCount==1);

    }
}