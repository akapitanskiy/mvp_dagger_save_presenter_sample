package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.content.Context
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
import io.reactivex.disposables.CompositeDisposable

/**
 * created on 01.03.2021 14:33
 */
class MainAdapter (val context: Context)
    : ListAdapter<HitPlusImgEntity, MainAdapter.PixabayItemViewHolder>(DIFF_CALLBACK) {

    private var presenter: MainContract.IPresenter? = null
    private val disposable = CompositeDisposable()

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HitPlusImgEntity>() {

            override fun areItemsTheSame(oldItem: HitPlusImgEntity, newItem: HitPlusImgEntity): Boolean {
                return oldItem.jsonId == newItem.jsonId
            }

            override fun areContentsTheSame(oldItem: HitPlusImgEntity, newItem: HitPlusImgEntity): Boolean {
                return  oldItem.previewURL == newItem.previewURL &&
                        oldItem.largeImageURL == newItem.largeImageURL &&
                        oldItem.user == newItem.user &&
                        oldItem.img.contentEquals(newItem.img) &&
                        oldItem.likes == newItem.likes
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixabayItemViewHolder {
        return PixabayItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pixaby, parent, false))
    }

    override fun onBindViewHolder(holder: PixabayItemViewHolder, position: Int) {
        val currentHit: HitPlusImgEntity = getItem(position)
        if (currentHit.img == null) {
            holder.imageView.setImageResource(R.drawable.ic_image_placeholder)
            presenter?.fetchImage(currentHit)
        } else {
            holder.imageView.setImageBitmap(
                BitmapFactory.decodeByteArray(currentHit.img, 0, currentHit.img!!.size)
            )
        }

        holder.tvCreator.text = currentHit.user
        holder.tvLikes.text = context.getString(R.string.likes_number, currentHit.likes)
    }


    class PixabayItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val tvCreator: TextView = itemView.findViewById(R.id.text_view_creator)
        val tvLikes: TextView = itemView.findViewById(R.id.text_view_likes)
    }

    fun setPresenter(presenter: MainContract.IPresenter) {
        this.presenter = presenter
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable.clear()
        disposable.dispose()
        presenter = null
    }
}