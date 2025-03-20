package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(
    private val trackList: ArrayList<Track>,
    private val clickCallback: (Track) -> Unit) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        private val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        private val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)
        private val trackImageView: ImageView = view.findViewById(R.id.trackImageView)

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            artistNameTextView.requestLayout()
            trackTimeTextView.text = track.formattedTime()

            Glide.with(itemView)
                .load(track.artworkUrl100 ?: "")
                .placeholder(R.drawable.track_placeholder)
                .centerInside()
                .transform(RoundedCorners(dpToPx(2f, itemView.resources.displayMetrics).toInt()))
                .into(trackImageView)

            itemView.setOnClickListener {
                if (clickDebounce()) {
                    clickCallback(track)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int = trackList.size

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}