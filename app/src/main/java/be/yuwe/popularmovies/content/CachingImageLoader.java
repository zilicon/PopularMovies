package be.yuwe.popularmovies.content;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class CachingImageLoader {
    private final static String LOG_TAG = CachingImageLoader.class.getSimpleName();

    private static Picasso cachingPicasso;

    private static Picasso getCachingPicasso(Context context) {
        if (cachingPicasso!=null)
            return cachingPicasso;
        Downloader downloader   = new OkHttpDownloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        cachingPicasso = builder.downloader(downloader).build();
        //cachingPicasso.setIndicatorsEnabled(true);
        return cachingPicasso;
    }

    public static void loadImage(final Context context, final String imageUrl, final String label, final ImageView imageView) {
        final Picasso picasso = getCachingPicasso(context);

        picasso.load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                       picasso.load(imageUrl)
                                .noFade()
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(LOG_TAG, "Could not fetch image " + imageUrl);
                                    }
                                });
                    }
                });
    }
}
