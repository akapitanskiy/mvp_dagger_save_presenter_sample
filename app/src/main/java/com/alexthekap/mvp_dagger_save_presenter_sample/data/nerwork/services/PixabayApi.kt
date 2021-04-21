package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services

import com.alexthekap.mvp_dagger_save_presenter_sample.BuildConfig
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.PictureResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * created on 09.03.2021 20:42
 */
interface PixabayApi {

    companion object {
        private const val API_KEY = BuildConfig.PIXABAY_API_KEY
        private const val IMAGE_TYPE = "photo"
    }

//    @GET("api/?key=$API_KEY&image_type=$IMAGE_TYPE&per_page=10")
    @GET("api/?key=$API_KEY&image_type=$IMAGE_TYPE")
    fun getImages(@Query("q") response: String, @Query("page") page: Int): Single<PictureResponse>

//    @Streaming
    @GET
    fun downloadFile(@Url url: String): Call<ResponseBody>
}