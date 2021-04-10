package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model

import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.google.gson.annotations.SerializedName

/**
 * created on 09.03.2021 22:11
 */
data class PictureResponse (

    @SerializedName("total")
    val total: Int,

    @SerializedName("totalHits")
    val totalHits: Int,

    @SerializedName("hits")
    val hits: List<HitPlusImgEntity>
)