package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexthekap.mvp_dagger_save_presenter_sample.R
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.ActivityScope
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logException
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * created on 01.03.2021 14:33
 */
@ActivityScope
class MainAdapter @Inject constructor(
    private val context: Context,
) : ListAdapter<HitPlusImgEntity, MainAdapter.PixabayItemViewHolder>(DIFF_CALLBACK) {

    lateinit var onItemClickListener: OnItemClickListener

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HitPlusImgEntity>() {

            override fun areItemsTheSame(oldItem: HitPlusImgEntity, newItem: HitPlusImgEntity): Boolean {
                return oldItem.jsonId == newItem.jsonId
            }

            override fun areContentsTheSame(oldItem: HitPlusImgEntity, newItem: HitPlusImgEntity): Boolean {
                return  oldItem.creator == newItem.creator &&
                        oldItem.img.contentEquals(newItem.img) &&
                        oldItem.likes == newItem.likes
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixabayItemViewHolder {
        logMessage(this, "onCreateViewHolder: ${currentList.size}")
        return PixabayItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pixaby, parent, false))
    }

    override fun onBindViewHolder(holder: PixabayItemViewHolder, position: Int) {
        val currentHit: HitPlusImgEntity? = getItem(position)
        if (currentHit?.img == null) {
            holder.imageView.setImageResource(R.drawable.ic_image_placeholder)
        } else {
            Maybe.fromCallable {
                BitmapFactory.decodeByteArray(currentHit.img, 0, currentHit.img!!.size)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableMaybeObserver<Bitmap>() {
                override fun onSuccess(bmp: Bitmap) {
                    holder.imageView.setImageBitmap(bmp)
                    dispose()
                }
                override fun onError(e: Throwable) {
                    logException(this@MainAdapter, e)
                }
                override fun onComplete() { }
            })
        }
        holder.tvCreator.text = currentHit?.creator
        holder.tvLikes.text = context.getString(R.string.likes_number, currentHit?.likes)
    }

    inner class PixabayItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                if (onItemClickListener != null && adapterPosition != RecyclerView.NO_POSITION)
                    onItemClickListener.onItemClick(getItem(adapterPosition))
            }
        }

        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val tvCreator: TextView = itemView.findViewById(R.id.text_view_creator)
        val tvLikes: TextView = itemView.findViewById(R.id.text_view_likes)
    }

    fun interface OnItemClickListener {
        fun onItemClick(hit: HitPlusImgEntity)
    }
}