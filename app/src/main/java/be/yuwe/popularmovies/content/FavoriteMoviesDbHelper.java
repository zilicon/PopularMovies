package be.yuwe.popularmovies.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    private final String SQL_CREATE_MOVIE_SUMMARY_TABLE = "CREATE TABLE " + MovieContract.MovieSummary.TABLE_NAME + " (" +
            MovieContract.MovieSummary._ID + " INTEGER PRIMARY KEY," +
            MovieContract.MovieSummary.COLUMN_TITLE + " TEXT, " +
            MovieContract.MovieSummary.COLUMN_POSTER_PATH + " TEXT, " +
            MovieContract.MovieSummary.COLUMN_PLOT+ " TEXT, " +
            MovieContract.MovieSummary.COLUMN_VOTE_AVERAGE + " REAL," +
            MovieContract.MovieSummary.COLUMN_RELEASE_DATE + " DATE," +
            MovieContract.MovieSummary.COLUMN_FAVORITE + " BOOL " +
            " );";


    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_SUMMARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
