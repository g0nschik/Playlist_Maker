package com.example.playlistmaker

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val searchHistory: SearchHistory) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        private val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        private val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)
        private val trackImageView: ImageView = view.findViewById(R.id.trackImageView)

        fun dpToPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                itemView.context.resources.displayMetrics
            ).toInt()
        }

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.track_placeholder)
                .centerInside()
                .transform(RoundedCorners(dpToPx(2f)))
                .into(trackImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { track ->
            searchHistory.addTrack(trackList[position])
            //Toast.makeText(holder.itemView.context, "Трек ${trackList[position].trackName} сохранён в истории", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = trackList.size
}