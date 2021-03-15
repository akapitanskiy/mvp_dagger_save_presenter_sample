package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model

import com.google.gson.annotations.SerializedName

/**
 * created on 09.03.2021 20:58
 */
data class Hit (

    var id: Int,
    var previewURL: String,
    var largeImageURL: String,
    @SerializedName("user")
    var creator: String,
    var likes: Int
)