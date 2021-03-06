package com.alexthekap.mvp_dagger_save_presenter_sample.data.repository

import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostsDao
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.JsonPlaceholderApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * created on 19.02.2021 16:32
 */
class MainRepository @Inject constructor( // TODO internal class надо ли ???
    private val jsonPlaceholderApi: JsonPlaceholderApi,
    private val postsDao: PostsDao
) {

    private val TAG = "MainRepositoryTag"
    private val disposable = CompositeDisposable()

    fun fetchData(isFirstLaunch: Boolean): Observable<List<PostEntity>> {

        val observable = postsDao.getAll()

        if (isFirstLaunch) {
            disposable.add(jsonPlaceholderApi.getPosts()
                .map {
                    Log.d(TAG, "fetchData map: GET posts called. size ${it.size}")
                    return@map it
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        postsDao.insert(it)
                        Log.d(TAG, "fetchData success: postDao insert called, size ${it.size}")
                    },
                    {
                        Log.d(TAG, "fetchData onError : ${it.message}")
                    })
            )
        }
        return observable
    }

    fun onViewFinished() {
        disposable.clear()
    }
}