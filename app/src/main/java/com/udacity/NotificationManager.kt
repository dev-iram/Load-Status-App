package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val DATA_INFO_CONTENT = "data_content"
const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    applicationContext: Context, channelId: String, content: DownloadContent
) {
    val detailIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(DATA_INFO_CONTENT, content)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        channelId
    ).apply {
        setSmallIcon(R.drawable.cloud_download)
        setContentTitle(applicationContext.getString(R.string.notification_title))
        setContentText(applicationContext.getString(R.string.notification_description))
        setContentIntent(contentPendingIntent)
        addAction(
            R.drawable.cloud_download,
            applicationContext.getString(R.string.notification_title),
            contentPendingIntent
        )
        setAutoCancel(true)
    }
    notify(NOTIFICATION_ID, builder.build())
}
