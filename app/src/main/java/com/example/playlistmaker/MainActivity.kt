package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
//import android.view.View
//import android.widget.Button
import android.widget.FrameLayout
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
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
}

class MyCustomApplication : Application() {
    // Вызывается при старте приложения, до того как инициализируются другие объекты
    override fun onCreate() {
        super.onCreate()
    }

    // Вызывается, когда конфигурация телефона изменена
    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    // Вызывается, когда заканчивается память в системе
    override fun onLowMemory() {
        super.onLowMemory()
    }
}