package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val HISTORY_KEY = "search_history"
const val HISTORY_SIZE = 10

interface HistoryCallback {
    fun reloadHistory()
}

class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val historyCallback: HistoryCallback
) {

    private val gson = Gson()

    fun getHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    fun addTrack(track: Track) {
        val history = getHistory()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
        historyCallback.reloadHistory()
    }

    private fun saveHistory(history: ArrayList<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
        historyCallback.reloadHistory()
    }
}