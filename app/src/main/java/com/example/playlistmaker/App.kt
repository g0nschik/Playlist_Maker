package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private var darkTheme = false
    private var lastActivity: String? = null

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(DARK_MODE, false)
        lastActivity = sharedPrefs.getString(LAST_ACTIVITY, null).toString()

        switchTheme(darkTheme)
        lastActivity?.let { setActivity(it) }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPrefs.edit().putBoolean(DARK_MODE, darkTheme).apply()
    }

    fun isDark(): Boolean {
        return darkTheme
    }

    private fun setActivity(lastActivity: String) {
        when(lastActivity) {
            PLAYER_ACTIVITY -> {
                saveActivity("")
                val displayIntent = Intent(this, PlayerActivity::class.java)
                displayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(displayIntent)
            }
        }
    }

    fun saveActivity(currentActivity: String) {
        sharedPrefs.edit().putString(LAST_ACTIVITY, currentActivity).apply()
    }

    companion object {
        const val APP_SETTINGS = "settings"
        const val DARK_MODE = "dark_mode"
        const val LAST_ACTIVITY = "last_activity"
        const val PLAYER_ACTIVITY = "PlayerActivity"
    }
}