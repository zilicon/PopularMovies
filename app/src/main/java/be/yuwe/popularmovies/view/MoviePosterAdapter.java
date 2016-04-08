package be.yuwe.popularmovies.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import be.yuwe.popularmovies.R;
import be.yuwe.popularmovies.content.CachingImageLoader;
import be.yuwe.popularmovies.content.MovieContract;

public class MoviePosterAdapter extends BaseAdapter {
    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();
    private final Context context;
    private ArrayList<MovieContract.MovieSummary> movieSummaries;
    
    

    public MoviePosterAdapter(Context context) {
        this.context = context;
    }

    public void setMovieSummaries(ArrayList<MovieContract.MovieSummary> movieSummaries) {
        this.movieSummaries = movieSummaries;
        Log.d(LOG_TAG, "Got new movies to show");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (movieSummaries==null) return 0;
        return movieSummaries.size();
    }

    @Override
    public Object getItem(int position) {
        return getMovieSummary(position);
    }

    public MovieContract.MovieSummary getMovieSummary(int position) {
        if (movieSummaries == null) return null;
        return movieSummaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (movieSummaries == null) return 0;
        return movieSummaries.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    final int measuredWidth = getMeasuredWidth();
                    setMeasuredDimension(measuredWidth, (int)(measuredWidth *1.5d));
                }
            };
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
            Picasso.with(context).cancelRequest(imageView);
        }
        final MovieContract.MovieSummary movieSummary = getMovieSummary(position);
        if (movieSummary != null && movieSummary.getPosterPath()!=null) {
            CachingImageLoader.loadImage(context, movieSummary.getPosterPath(), movieSummary.getTitle(), imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_poster);
        }
        return imageView;
    }
}
