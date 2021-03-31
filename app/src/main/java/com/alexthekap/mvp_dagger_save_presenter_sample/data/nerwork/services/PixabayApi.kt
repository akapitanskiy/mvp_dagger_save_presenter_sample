package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services

import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.PictureResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * created on 09.03.2021 20:42
 */
interface PixabayApi {

    @GET("api/?key=18933503-d327756bf2a67a0d0b736d17a&q=yellow+flowers&image_type=photo&pretty=true")
    fun getImages(): Single<PictureResponse>

//    @GET("{path}")
//    fun downloadFile(@Path("path", encoded = true) path: String): Call<ResponseBody>

    @GET
    fun downloadFile(@Url url: String):Call<ResponseBody>
}