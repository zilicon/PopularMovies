package be.yuwe.popularmovies.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "movies.db";

    private final String SQL_CREATE_MOVIE_SUMMARY_TABLE = "CREATE TABLE " + MovieContract.MovieSummary.TABLE_NAME + " (" +
            MovieContract.MovieSummary._ID + " INTEGER PRIMARY KEY," +
            MovieContract.MovieSummary.Columns.title.name() + " VARCHAR, " +
            MovieContract.MovieSummary.Columns.poster_path.name() + " VARCHAR, " +
            MovieContract.MovieSummary.Columns.release_date.name() + " DATE," +
            MovieContract.MovieSummary.Columns.vote_average.name() + " REAL," +
            MovieContract.MovieSummary.Columns.plot.name() + " TEXT, " +
            MovieContract.MovieSummary.Columns.favorite.name() + " BOOL " +
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
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieSummary.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieSummary.TABLE_NAME);
        onCreate(db);
    }
}
