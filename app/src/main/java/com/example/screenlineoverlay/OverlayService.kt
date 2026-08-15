package com.example.screenlineoverlay

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val width = intent?.getIntExtra("width", 4) ?: 4
        val offset = intent?.getIntExtra("offset", 0) ?: 0

        startForegroundIfNeeded()
        removeOverlay()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val type =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            width,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.START or Gravity.TOP
        params.x = offset
        params.y = 0

        val view = View(this)
        view.setBackgroundColor(Color.BLACK)
        overlayView = view

        windowManager.addView(view, params)
        return START_STICKY
    }

    private fun startForegroundIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "overlay_channel"
            val channel = NotificationChannel(
                channelId, "Overlay Service", NotificationManager.IMPORTANCE_MIN
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)

            val notification = Notification.Builder(this, channelId)
                .setContentTitle("Screen line overlay running")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .build()

            startForeground(1, notification)
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            if (::windowManager.isInitialized) {
                windowManager.removeView(it)
            }
        }
        overlayView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
    }
}
