package com.marwadiuniversity.abckids.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.R
import com.marwadiuniversity.abckids.data.YouTubeCategory
import com.marwadiuniversity.abckids.data.YouTubeVideo

class YouTubeCategoryAdapter(
    private val context: Context,
    private val categories: List<YouTubeCategory>,
    private val onVideoClick: (YouTubeVideo) -> Unit
) : RecyclerView.Adapter<YouTubeCategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.category_name)
        val categoryIcon: TextView = view.findViewById(R.id.category_icon)
        val videosRecycler: RecyclerView = view.findViewById(R.id.videos_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_youtube_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.categoryName.text = category.name
        holder.categoryIcon.text = category.icon

        val videoAdapter = YouTubeVideoAdapter(context, category.videos, onVideoClick)
        holder.videosRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.videosRecycler.adapter = videoAdapter
    }

    override fun getItemCount(): Int = categories.size
}