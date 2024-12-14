package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton  = findViewById<FrameLayout>(R.id.btn_back)
        val shareButton  = findViewById<FrameLayout>(R.id.btn_share)
        val supportButton  = findViewById<FrameLayout>(R.id.btn_support)
        val agreementButton  = findViewById<FrameLayout>(R.id.btn_agreement)
        val toggleDarkButton  = findViewById<Switch>(R.id.btn_dark_theme)

        backButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        shareButton.setOnClickListener {
            val url = "https://practicum.yandex.ru/referrals/?ref_code=gAAAAABmo_KiI0v1JXr0mzVglejzlHxQthCf54q8DeguprDZCnmcBXTrG_JNtvtmjGkk2Vb1kMHdGjeFToArtWDvrJ_beTvWDw%3D%3D"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(intent)
        }

        supportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_adress)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(intent)
        }

        agreementButton.setOnClickListener {
            val url = "https://yandex.ru/legal/practicum_offer/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        toggleDarkButton.setOnClickListener { 
            // check the mode
            val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            // set the other
            AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}