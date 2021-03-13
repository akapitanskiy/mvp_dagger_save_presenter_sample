package com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services

import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostEntity
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET

/**
 * created on 19.02.2021 15:51
 */
interface JsonPlaceholderApi {

    @GET("posts")
    fun getPosts(): Single<List<PostEntity>>
}