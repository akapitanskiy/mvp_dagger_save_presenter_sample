package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * created on 19.02.2021 15:58
 */
data class PostsResponseItem(

    @SerializedName("userId") @Expose
    var userId: Int,

    @SerializedName("id") @Expose
    var id: Int,

    @SerializedName("title") @Expose
    var title: String,

    @SerializedName("body") @Expose
    var body: String
)
