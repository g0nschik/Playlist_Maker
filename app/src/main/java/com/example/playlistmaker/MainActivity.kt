package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton  = findViewById<FrameLayout>(R.id.btn_search)
        val mediaButton = findViewById<FrameLayout>(R.id.btn_media)
        val settingsButton = findViewById<FrameLayout>(R.id.btn_settings)

        searchButton.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        mediaButton.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        (applicationContext as App).saveActivity(this.javaClass.simpleName)
    }
}

class MyCustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}