package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val APP_SETTINGS = "settings"
const val DARK_MODE = "dark_mode"

class App : Application() {
    private var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_MODE, false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(DARK_MODE, darkTheme)
            .apply()
    }

    fun isDark(): Boolean {
        return darkTheme
    }
}