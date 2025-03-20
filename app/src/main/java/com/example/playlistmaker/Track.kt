package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

const val TIME_FORMAT = "mm:ss"

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) {
    fun getCoverArtwork() =
        artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")

    fun formattedTime(): String =
        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
}



