package be.yuwe.popularmovies.control;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String LOG_TAG = Preferences.class.getSimpleName();

    public enum SORT_ORDER {BY_POPULARITY, BY_RATING, BY_FAVORITES}

    private final static String SORT_ORDER_PREFERENCE_KEY = "SORT_ORDER";

    public static SORT_ORDER getSortOrder(Context context) {
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        try {
            SORT_ORDER sortOrder = SORT_ORDER.valueOf(prefs.getString(SORT_ORDER_PREFERENCE_KEY, SORT_ORDER.BY_POPULARITY.name()));
            return sortOrder;
        } catch (IllegalArgumentException e) {
            return SORT_ORDER.BY_POPULARITY;
        }
    }

    public static void setSortOrder(Context context, SORT_ORDER sortOrder) {
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SORT_ORDER_PREFERENCE_KEY, sortOrder.name());
        editor.commit();
    }
}
