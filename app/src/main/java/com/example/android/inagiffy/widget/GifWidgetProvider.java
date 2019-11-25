package com.example.android.inagiffy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.inagiffy.MainActivity;
import com.example.android.inagiffy.R;

/**
 * Implementation of App Widget functionality.
 */
public class GifWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_gif_favorites);
        remoteViews.setOnClickPendingIntent(R.id.widget_heart_image, getGifFavoritesIntent(context, appWidgetId));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    // Method for sending user to their Favorites
    private static PendingIntent getGifFavoritesIntent(Context context, int value) {

        // Create an Intent to launch MainActivity when clicked (TODO: change to favorites)
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.putExtra(MainActivity.FAVORITES, true);
        return PendingIntent.getActivity(context, value, appIntent, 0);
    }
}

