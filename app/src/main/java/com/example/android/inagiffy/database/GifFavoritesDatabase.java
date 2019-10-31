package com.example.android.inagiffy.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.android.inagiffy.data.Gif;

@Database(entities = {Gif.class}, version = 1, exportSchema = false)
public abstract class GifFavoritesDatabase extends RoomDatabase {

    private static final String LOG = GifFavoritesDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String FAVORITES_LIST = "favorites";
    private static GifFavoritesDatabase sInstance;

    public static GifFavoritesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG, "Creating new database instance.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        GifFavoritesDatabase.class,
                        GifFavoritesDatabase.FAVORITES_LIST)
                        .build();
            }
        }
        Log.d(LOG, "Getting the database instance.");
        return sInstance;
    }

    public abstract GifDao gifDao();

}
