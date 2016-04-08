package be.yuwe.popularmovies.content;

import android.content.ContentValues;
import android.database.Cursor;

public interface Contentable {
    void writeToContentValues(ContentValues content);

    void readFromCursor(Cursor content);
}
