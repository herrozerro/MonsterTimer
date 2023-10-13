package com.singularitylathe.monstertimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.singularitylathe.monstertimer.CountdownManager
import com.singularitylathe.monstertimer.R

class MonsterTimer2x1 : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("MonsterTimer2x1", "onUpdate called")
        // Existing logic to update the widget on receive of update action
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MonsterTimer2x1::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val currentTimeMillis = System.currentTimeMillis()
        val nextUpdateTimeMillis = currentTimeMillis + 60000 - (currentTimeMillis % 60000)

        // Set the alarm to trigger at the start of the next minute and then every minute thereafter
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextUpdateTimeMillis, 60000, pendingIntent)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MonsterTimer2x1::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d("MonsterTimer2x1", "updateAppWidget called")
        val monsterTimer = CountdownManager()

        // Here, we update our widget - in this case, we're just setting the text of a TextView.
        val views = RemoteViews(context.packageName, R.layout.monster_timer2x1)
        views.setTextViewText(R.id.monsterRefreshText, "Monsters: " + monsterTimer.countDownText)
        views.setTextViewText(R.id.biomeRefreshText, "Biomes: " + monsterTimer.worldCountDownText)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}