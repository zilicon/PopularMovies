package be.yuwe.popularmovies.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Date;

public class MovieContract {
    private static final String CONTENT_AUTHORITY = "be.yuwe.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_SUMMARY = "summary";

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int MOVIE_SUMMARY = 1;
    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SUMMARY, MOVIE_SUMMARY);
    }

    public static class MovieSummary implements BaseColumns, Parcelable {
        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUMMARY).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUMMARY;

        public static Uri getContentUri() {
            return CONTENT_URI;
        }

        public static Uri getContentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public final static String TABLE_NAME = "movie";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_POSTER_PATH = "poster_path";
        public final static String COLUMN_RELEASE_DATE = "release_date";
        public final static String COLUMN_VOTE_AVERAGE = "vote_average";
        public final static String COLUMN_PLOT = "plot";
        public final static String COLUMN_FAVORITE = "favorite";

        private long id;
        private String title;
        private String posterPath;
        public Date releaseDate;
        public double voteAverage;
        public String plot;
        private boolean favorite;

        private MovieSummary() {
        }

        protected MovieSummary(Parcel in) {
            id = in.readLong();
            title = in.readString();
            posterPath = in.readString();
            long releaseTime = in.readLong();
            releaseDate = releaseTime == 0 ? null : new Date(releaseTime);
            voteAverage = in.readDouble();
            plot = in.readString();
            favorite = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(title);
            dest.writeString(posterPath);
            dest.writeLong(releaseDate == null ? 0 : releaseDate.getTime());
            dest.writeDouble(voteAverage);
            dest.writeString(plot);
            dest.writeInt(favorite ? 1 : 0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<MovieSummary> CREATOR = new Creator<MovieSummary>() {
            @Override
            public MovieSummary createFromParcel(Parcel in) {
                return new MovieSummary(in);
            }

            @Override
            public MovieSummary[] newArray(int size) {
                return new MovieSummary[size];
            }
        };

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public Date getReleaseDate() {
            return releaseDate;
        }

        public double getVoteAverage() {
            return voteAverage;
        }

        public String getPlot() {
            return plot;
        }

        public boolean isFavorite() {
            return favorite;
        }

        public void setFavorite(boolean favorite) {
            this.favorite = favorite;
        }


        public static MovieSummary create(long id, String title, String posterPath, Date releaseDate, double voteAverage, String plot, boolean favorite) {
            MovieSummary summary = new MovieSummary();
            summary.id = id;
            summary.title = title;
            summary.posterPath = posterPath;
            summary.releaseDate = releaseDate;
            summary.voteAverage = voteAverage;
            summary.plot = plot;
            summary.favorite = favorite;
            return summary;
        }
    }
}
