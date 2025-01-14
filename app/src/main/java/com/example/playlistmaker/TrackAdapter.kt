package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val trackList: ArrayList<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        private val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        private val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)
        private val trackImageView: ImageView = view.findViewById(R.id.trackImageView)

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.trackTime

            Glide.with(itemView)
                .load(track.artworkUrl100)
                //.placeholder(R.drawable.placeholder)
                .centerInside()
                .transform(RoundedCorners(10))
                .into(trackImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int = trackList.size
}