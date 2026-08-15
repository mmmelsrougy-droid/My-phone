package com.example.screenlineoverlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private var width = 4
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val widthLabel = findViewById<TextView>(R.id.widthLabel)
        val offsetLabel = findViewById<TextView>(R.id.offsetLabel)
        val widthSeekBar = findViewById<SeekBar>(R.id.widthSeekBar)
        val offsetSeekBar = findViewById<SeekBar>(R.id.offsetSeekBar)
        val toggleButton = findViewById<Button>(R.id.toggleButton)

        widthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                width = progress + 1
                widthLabel.text = "Width: $width px"
                if (isRunning) updateOverlay()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        offsetSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                offset = progress
                offsetLabel.text = "Position from edge: $offset px"
                if (isRunning) updateOverlay()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        toggleButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                return@setOnClickListener
            }

            if (!isRunning) {
                startOverlay()
                toggleButton.text = "Stop Overlay"
            } else {
                stopService(Intent(this, OverlayService::class.java))
                toggleButton.text = "Start Overlay"
            }
