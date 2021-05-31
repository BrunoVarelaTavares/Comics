package com.btavares.comics.app.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.btavares.comics.R
import com.btavares.comics.app.presentation.MainActivity
import com.btavares.comics.app.presentation.extension.vectorToBitmap
import com.btavares.comics.app.sharedpreferences.ComicPreferences
import com.btavares.comics.main.data.retrofit.ComicsService
import com.btavares.comics.main.data.room.dao.ComicsDao
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


internal  class Notifications(context: Context, params : WorkerParameters
) : CoroutineWorker(context, params), KodeinAware {

    override val kodein: Kodein by closestKodein(context)

    private val service : ComicsService by instance()

    private val comicsDao: ComicsDao by instance()

    private val sharedPreferences : ComicPreferences by instance()

    override suspend fun doWork(): Result {
        val comic = service.getCurrentComic()
        val lastComicNumber = sharedPreferences.getLastComicNumber()
        if (sharedPreferences.isDownloadCompleted() && comic?.number != null) {
            if (comic.number > lastComicNumber) {
                comicsDao.insert(comic)
                sharedPreferences.saveLastComicNumber(comic.number)
                sendNotification(inputData.getLong(NOTIFICATION_ID, 0).toInt())
            }
        }
        return success()
    }




    private fun sendNotification(id: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_baseline_uncheck_star)
        val titleNotification = applicationContext.getString(R.string.app_name)
        val subtitleNotification = applicationContext.getString(R.string.notification_subtitle)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setLargeIcon(bitmap).setSmallIcon(R.drawable.ic_baseline_uncheck_star)
            .setContentTitle(titleNotification).setContentText(subtitleNotification)
            .setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }


    companion object {
        const val NOTIFICATION_ID = "comics_notification_id"
        const val NOTIFICATION_NAME = "comics"
        const val NOTIFICATION_CHANNEL = "comics_channel_01"
        const val NOTIFICATION_WORK = "comics_notification_work"
    }


}