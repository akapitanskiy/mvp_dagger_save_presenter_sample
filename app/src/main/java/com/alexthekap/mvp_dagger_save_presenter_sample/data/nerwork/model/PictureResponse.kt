package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model

/**
 * created on 09.03.2021 22:11
 */
data class PictureResponse (

    var total: Int,
    var totalHits: Int,
    var hits: List<Hit>
)