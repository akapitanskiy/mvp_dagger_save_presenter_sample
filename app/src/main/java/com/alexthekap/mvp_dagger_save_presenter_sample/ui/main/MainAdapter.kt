package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.alexthekap.mvp_dagger_save_presenter_sample.R
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostEntity

/**
 * created on 01.03.2021 14:33
 */
class MainAdapter : ListAdapter<PostEntity, MainAdapter.PostViewHolder>(DIFF_CALLBACK) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostEntity>() {
            override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
                return  oldItem.title == newItem.title &&
                        oldItem.userId == newItem.userId &&
                        oldItem.body == newItem.body
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = getItem(position)
        holder.tvTitle.text = currentPost.title
        holder.tvContent.text = currentPost.body
        holder.tvAuthor.text = currentPost.userId.toString()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
    }

}