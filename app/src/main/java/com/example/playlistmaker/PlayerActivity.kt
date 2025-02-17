package com.example.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.APP_HISTORY
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity(), HistoryCallback {
    private lateinit var searchHistory: SearchHistory

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackImage: ImageView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPrefs = getSharedPreferences(APP_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs, this)

        val backButton = findViewById<ImageView>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

        val currentTrack: Track = searchHistory.getHistory()[0]

        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackTime = findViewById(R.id.track_time)
        trackImage = findViewById(R.id.track_image)
        trackAlbum = findViewById(R.id.track_album)
        trackYear = findViewById(R.id.track_year)
        trackGenre = findViewById(R.id.track_genre)
        trackCountry = findViewById(R.id.track_country)

        trackName.text = currentTrack.trackName
        artistName.text = currentTrack.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis)
        Glide.with(this)
            .load(currentTrack.getCoverArtwork())
            .placeholder(R.drawable.track_placeholder_big)
            .centerInside()
            .transform(RoundedCorners(dpToPx(8f, resources.displayMetrics).toInt()))
            .into(trackImage)
        trackAlbum.text = currentTrack.collectionName ?: "—"
        trackYear.text = currentTrack.releaseDate?.take(4) ?: "—"
        trackGenre.text = currentTrack.primaryGenreName ?: "—"
        trackCountry.text = currentTrack.country ?: "—"
    }

    override fun onStop() {
        super.onStop()
        (applicationContext as App).saveActivity(this.javaClass.simpleName)
    }

    override fun reloadHistory() {
        //
    }
}