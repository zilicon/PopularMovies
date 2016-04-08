package be.yuwe.popularmovies.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;

public class MovieContract {
    private static final String CONTENT_AUTHORITY = "be.yuwe.popularmovies.content.FavoriteMoviesProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_SUMMARY = "summary";

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int MOVIE_SUMMARY = 1;

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SUMMARY, MOVIE_SUMMARY);
    }

    public static class MovieSummary implements BaseColumns, Parcelable, Contentable {
        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUMMARY).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUMMARY;

        public static Uri getContentUri() {
            return CONTENT_URI;
        }

        public static Uri getContentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long parseId(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static String TABLE_NAME = "movie_summary";

        public enum Columns {_id, title, poster_path, release_date, vote_average, plot, favorite}

        private long id;
        private String title;
        private String posterPath;
        public Date releaseDate;
        public double voteAverage;
        public String plot;
        private boolean favorite;

        public MovieSummary(long id, String title, String posterPath, Date releaseDate, double voteAverage, String plot, boolean favorite) {
            this.id = id;
            this.title = title;
            this.posterPath = posterPath;
            this.releaseDate = releaseDate;
            this.voteAverage = voteAverage;
            this.plot = plot;
            this.favorite = favorite;
        }

        public MovieSummary(Parcel in) {
            id = in.readLong();
            title = in.readString();
            posterPath = in.readString();
            long releaseTime = in.readLong();
            releaseDate = releaseTime == 0 ? null : new Date(releaseTime);
            voteAverage = in.readDouble();
            plot = in.readString();
            favorite = in.readInt() == 1;
        }

        public MovieSummary(Cursor cursor) {
            readFromCursor(cursor);
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
        public void writeToContentValues(ContentValues contentValues) {
            contentValues.put(_ID, id);
            contentValues.put(Columns.title.name(), title);
            contentValues.put(Columns.poster_path.name(), posterPath);
            contentValues.put(Columns.release_date.name(), releaseDate == null ? 0 : releaseDate.getTime());
            contentValues.put(Columns.vote_average.name(), voteAverage);
            contentValues.put(Columns.plot.name(), plot);
            contentValues.put(Columns.favorite.name(), favorite);
        }

        @Override
        public void readFromCursor(Cursor cursor) {
            this.id = cursor.getLong(Columns._id.ordinal());
            this.title = cursor.getString(Columns.title.ordinal());
            this.posterPath = cursor.getString(Columns.poster_path.ordinal());
            final Long dateAsLong = cursor.getLong(Columns.release_date.ordinal());
            this.releaseDate = dateAsLong == null ? null : new Date(dateAsLong);
            this.voteAverage = cursor.getDouble(Columns.vote_average.ordinal());
            this.plot = cursor.getString(Columns.plot.ordinal());
            this.favorite = cursor.getInt(Columns.favorite.ordinal()) == 1;
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
    }

    public static class Trailer {
        private String key;
        private String site;
        private String type;

        public Trailer(String key, String site, String type) {
            this.key = key;
            this.site = site;
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public String getSite() {
            return site;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return site + " " + type;
        }
    }

    public static class Movie {
        private long theMovieDbId;
        private ArrayList<Trailer> trailers;
        private ArrayList<Review> reviews;

        public Movie(long theMovieDbId, ArrayList<Trailer> trailers, ArrayList<Review> reviews) {
            this.theMovieDbId = theMovieDbId;
            this.trailers = trailers;
            this.reviews = reviews;
        }

        public long getTheMovieDbId() {
            return theMovieDbId;
        }

        public ArrayList<Trailer> getTrailers() {
            return trailers;
        }

        public ArrayList<Review> getReviews() {
            return reviews;
        }
    }

    public static class Review {
        private String author;
        private String review;

        public Review(String author, String review) {
            this.author = author;
            this.review = review;
        }

        public String getAuthor() {
            return author;
        }

        public String getReview() {
            return review;
        }

        @Override
        public String toString() {
            return review;
        }
    }
}
