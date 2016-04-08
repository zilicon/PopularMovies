package be.yuwe.popularmovies.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class FavoriteMoviesProvider extends ContentProvider {
    public static final String LOG_TAG = FavoriteMoviesProvider.class.getSimpleName();

    private FavoriteMoviesDbHelper dbHelper;

    public FavoriteMoviesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getWritableDatabase();
            final long id = MovieContract.MovieSummary.parseId(uri);
            selectionArgs = new String[]{Long.toString(id)};
            database.beginTransaction();
            final int nrDeletedRows = database.delete(MovieContract.MovieSummary.TABLE_NAME, MovieContract.MovieSummary.Columns._id.name() + " = ?", selectionArgs);
            database.setTransactionSuccessful();
            Log.d(LOG_TAG, "Deleted a favorite movie");
            return nrDeletedRows;
        } finally {
            if (database != null)
                database.endTransaction();
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getWritableDatabase();
            database.beginTransaction();
            database.insert(MovieContract.MovieSummary.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
            Log.d(LOG_TAG, "Inserted a favorite movie");
            return uri;
        } finally {
            if (database != null)
                database.endTransaction();
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        final Cursor cursor = readableDatabase.query(MovieContract.MovieSummary.TABLE_NAME, null, null, null, null, null, null);
        Log.d(LOG_TAG, "Queried all favorite movies");
        //TODO: query 1 movie
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
