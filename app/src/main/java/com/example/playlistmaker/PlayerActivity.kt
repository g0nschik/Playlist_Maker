package com.example.playlistmaker

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
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
import com.example.playlistmaker.SearchActivity.Companion.SEARCH_DEBOUNCE_DELAY
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

    private lateinit var playTime: TextView

    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var play: ImageButton
    private var mediaPlayer = MediaPlayer()

    companion object {
        private const val STATE_INACTIVE = -1
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 300L
    }

    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())
    private val timeRunnable = updatePlayTime()

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
        trackTime.text = currentTrack.formattedTime()
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

        playTime = findViewById(R.id.play_time)

        play = findViewById(R.id.play_button)

        if (currentTrack.previewUrl !== null) {
            preparePlayer(currentTrack.previewUrl.toString())
            play.setOnClickListener {
                playbackControl()
            }
            play.setEnabled(true)
        }
        else {
            playerState = STATE_INACTIVE
            play.setEnabled(false)
        }

        setButton()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        (applicationContext as App).saveActivity(this.javaClass.simpleName)
        handler.removeCallbacks(timeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timeRunnable)
    }

    override fun reloadHistory() {
        //
    }

    private fun preparePlayer(track: String) {
        mediaPlayer.setDataSource(track)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
            setButton()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            handler.removeCallbacks(timeRunnable)
            playTime.text = getString(R.string.zero_time)
            setButton()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        setButton()
        handler.post(timeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        setButton()
        handler.removeCallbacks(timeRunnable)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun setButton() {
        when(playerState) {
            STATE_INACTIVE -> {
                play.setImageResource(R.drawable.play_button_inactive)
            }
            STATE_PLAYING -> {
                play.setImageResource(R.drawable.pause_button)
            }
            STATE_PREPARED, STATE_PAUSED -> {
                play.setImageResource(R.drawable.play_button)
            }
        }
    }

    private fun updatePlayTime(): Runnable {
        return object : Runnable {
            override fun run() {
                playTime.text =
                    SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(mediaPlayer.getCurrentPosition())
                handler.postDelayed(this, DELAY)
            }
        }
    }
}