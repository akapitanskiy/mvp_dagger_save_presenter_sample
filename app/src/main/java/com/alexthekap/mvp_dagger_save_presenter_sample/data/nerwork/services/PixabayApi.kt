package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services

import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.PictureResponse
import io.reactivex.Single
import retrofit2.http.GET

/**
 * created on 09.03.2021 20:42
 */
interface PixabayApi {

    @GET("?key=18933503-d327756bf2a67a0d0b736d17a&q=yellow+flowers&image_type=photo&pretty=true")
    fun getImages(): Single<PictureResponse>
}