package com.marwadiuniversity.abckids.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marwadiuniversity.abckids.R
import com.marwadiuniversity.abckids.data.YouTubeVideo

class YouTubeVideoAdapter(
    private val context: Context,
    private val videos: List<YouTubeVideo>,
    private val onVideoClick: (YouTubeVideo) -> Unit
) : RecyclerView.Adapter<YouTubeVideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.video_card)
        val thumbnail: ImageView = view.findViewById(R.id.video_thumbnail)
        val title: TextView = view.findViewById(R.id.video_title)
        val duration: TextView = view.findViewById(R.id.video_duration)
        val channel: TextView = view.findViewById(R.id.video_channel)
        val playIcon: TextView = view.findViewById(R.id.play_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_youtube_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]

        holder.title.text = video.title
        holder.channel.text = video.channelTitle

        if (video.duration.isNotEmpty()) {
            holder.duration.visibility = View.VISIBLE
            holder.duration.text = video.duration
        } else {
            holder.duration.visibility = View.GONE
        }

        Glide.with(context)
            .load(video.thumbnail)
            .placeholder(R.drawable.ic_video_placeholder)
            .error(R.drawable.ic_video_placeholder)
            .centerCrop()
            .into(holder.thumbnail)

        holder.card.setOnClickListener {
            animateClick(holder.card)
            onVideoClick(video)
        }
    }

    private fun animateClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    override fun getItemCount(): Int = videos.size
}
