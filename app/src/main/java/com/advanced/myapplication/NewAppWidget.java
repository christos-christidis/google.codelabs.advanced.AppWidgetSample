package com.advanced.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;

// SOS: AppWidgetProvider extends BroadcastReceiver, note that it's defined as a receiver in the
// manifest that listens for the android.appwidget.action.APPWIDGET_UPDATE action. When that intent
// is received, onUpdate here is called
public class NewAppWidget extends AppWidgetProvider {

    private static final String mSharedPrefFile = BuildConfig.APPLICATION_ID + ".sharedPref";
    private static final String STATE_UPDATE_COUNT = "update_count";

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(mSharedPrefFile, 0);
        int updateCount = prefs.getInt(STATE_UPDATE_COUNT + appWidgetId, 0);
        updateCount++;

        String dateString = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_id, String.valueOf(appWidgetId));
        views.setTextViewText(R.id.appwidget_update, context.getResources().getString(
                R.string.update_count_format, updateCount, dateString));

        prefs.edit().putInt(STATE_UPDATE_COUNT + appWidgetId, updateCount)
                .apply();

        setUpWidgetButton(context, appWidgetId, views);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    // SOS: called once when the 1st widget is added, eg it might open a db
    @Override
    public void onEnabled(Context context) {
    }

    // SOS: called once when the last widget is removed, eg it may close the db
    @Override
    public void onDisabled(Context context) {
    }

    // SOS: the button will essentially send a broadcast w action ACTION_APPWIDGET_UPDATE to our
    // NewAppWidget class, which will call its onUpdate passing only this widget's id.
    private static void setUpWidgetButton(Context context, int appWidgetId, RemoteViews views) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] widgetIDs = new int[]{appWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.button_update, pendingIntent);
    }
}

