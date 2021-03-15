package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexthekap.mvp_dagger_save_presenter_sample.R
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.Hit
import com.squareup.picasso.Picasso

/**
 * created on 01.03.2021 14:33
 */
class MainAdapter(val context: Context) : ListAdapter<Hit, MainAdapter.PixabayItemViewHolder>(DIFF_CALLBACK) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Hit>() {
            override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return  oldItem.previewURL == newItem.previewURL &&
                        oldItem.largeImageURL == newItem.largeImageURL &&
                        oldItem.creator == newItem.creator &&
                        oldItem.likes == newItem.likes
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixabayItemViewHolder {
        return PixabayItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pixaby, parent, false))
    }

    override fun onBindViewHolder(holder: PixabayItemViewHolder, position: Int) {
        val currentHit = getItem(position)
        Picasso.get()
            .load(currentHit.largeImageURL)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_error)
            .fit()
            .centerInside()
            .into(holder.imageView)
        holder.tvCreator.text = currentHit.creator
        holder.tvLikes.text = context.getString(R.string.likes_number, currentHit.likes)
    }

    inner class PixabayItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val tvCreator: TextView = itemView.findViewById(R.id.text_view_creator)
        val tvLikes: TextView = itemView.findViewById(R.id.text_view_likes)
    }

}