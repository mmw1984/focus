package com.focus.timer.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.focus.timer.MainActivity
import com.focus.timer.R

class NotificationManager(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "focus_timer_channel"
        const val FOCUS_NOTIFICATION_ID = 1001
        const val BREAK_NOTIFICATION_ID = 1002
        const val MICRO_BREAK_NOTIFICATION_ID = 1003
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "專注計時器",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "專注計時器通知"
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }    
    fun showFocusNotification(remainingTime: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("專注進行中")
            .setContentText("剩餘時間: ${formatTime(remainingTime)}")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        
        notificationManager.notify(FOCUS_NOTIFICATION_ID, notification)
    }
    
    fun showBreakNotification(remainingTime: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("休息時間")
            .setContentText("剩餘時間: ${formatTime(remainingTime)}")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        
        notificationManager.notify(BREAK_NOTIFICATION_ID, notification)
    }
    
    fun showMicroBreakStartNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("微休息開始")
            .setContentText("請短暫休息10秒，閉眼放鬆")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(MICRO_BREAK_NOTIFICATION_ID, notification)
        vibrateShort()
    }
    
    fun showMicroBreakEndNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("微休息結束")
            .setContentText("休息結束，繼續專注")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(MICRO_BREAK_NOTIFICATION_ID, notification)
        vibrateShort()
    }
    
    fun showBreakEndNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("休息結束")
            .setContentText("可以開始下一個專注時段了")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(BREAK_NOTIFICATION_ID, notification)
        vibrateShort()
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
    
    private fun formatTime(timeInMillis: Long): String {
        val totalSeconds = timeInMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    private fun vibrateShort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
}
