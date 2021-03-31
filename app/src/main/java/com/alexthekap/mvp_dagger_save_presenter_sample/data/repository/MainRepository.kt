package com.alexthekap.mvp_dagger_save_presenter_sample.data.repository

import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.*
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.JsonPlaceholderApi
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.PixabayApi
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Path
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * created on 19.02.2021 16:32
 */
class MainRepository @Inject constructor( // TODO internal class надо ли ???
    private val jsonPlaceholderApi: JsonPlaceholderApi,
    private val postsDao: PostsDao,
    private val pixabayApi: PixabayApi,
    private val pixabayDao: PixabayDao
) {

    private val TAG = "MainRepositoryTag"
    private val disposable = CompositeDisposable()
    private var fetchPixabayDataDebouncedSubj = PublishSubject.create<Unit>()

    init {
        disposable.add(
            fetchPixabayDataDebouncedSubj
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
//                .debounce(2000, TimeUnit.MILLISECONDS)
                .subscribe {
                    Log.d(TAG, "debounce: called fetchApiImages()")
                    fetchApiImages()
                }
        )
    }

    fun fetchData(isFirstLaunch: Boolean): Observable<List<PostEntity>> {
        val dbAllPostsObservable = postsDao.getAll()
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
        return dbAllPostsObservable
    }

    fun fetchPixabayData(isFirstLaunch: Boolean): Observable<List<HitPlusImgEntity>> {

        val dbAllHitsObservable = pixabayDao.getAllHitsFromDb()

        if (isFirstLaunch) {
            Log.d(TAG, "fetchPixabayData: called fetchApiImages() firstLaunch $isFirstLaunch")
            fetchApiImages()
        }
        return dbAllHitsObservable
    }

    fun fetchImage(hitEntity: HitPlusImgEntity) {

        val call = pixabayApi.downloadFile(hitEntity.largeImageURL)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d(TAG, "ERROR onFailure: ${response.errorBody()}")
                    fetchPixabayDataDebouncedSubj.onNext(Unit)
                } else {
                    response.body()?.let {
                        val arr = getByteArray(it)
                        update(arr, hitEntity.jsonId)
                        Log.d(TAG, "onResponse: ByteArray size ${arr.size}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "ERROR onFailure: ${t.message}")
                fetchPixabayDataDebouncedSubj.onNext(Unit)
            }
        })
    }

    fun update(array: ByteArray, jsonId: Long) {

        disposable.add(
            Completable.fromAction {
                pixabayDao.updateImg(array, jsonId)
                Log.d(TAG, "update: ByteArray size ${array.size}")
            }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {
                    Log.d(TAG, "update db: error ${it.message}")
                }
            )
        )
    }

    fun getByteArray(responseBody: ResponseBody): ByteArray {

        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var n: Int
        val inputStream = responseBody.byteStream()

        while (true) {
            n = inputStream?.read(buf)!!
            if (n == -1) break
            outputStream.write(buf, 0, n)
        }
        outputStream.close()
        inputStream.close()
        return outputStream.toByteArray()
    }

    private fun fetchApiImages() {
        disposable.add(
            pixabayApi.getImages()
                .map {
                    Log.d(TAG, "fetchApiImages() map: GET images api called. response: $it")
                    return@map it.hits
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { jsonHits ->
                        val listToUpdate = ArrayList<HitPlusImgEntity>()
                        for (jsonHit in jsonHits) {
                            val dbHit = pixabayDao.getById(jsonHit.jsonId)

                            if (dbHit?.img != null && (dbHit.likes != jsonHit.likes || dbHit.user != jsonHit.user)) {
                                pixabayDao.updateLikesAndUser(jsonHit.user, jsonHit.likes, jsonHit.jsonId)
                                Log.d(TAG,"fetchApiImages: updateLikesAndUser row ${jsonHit.jsonId} ${dbHit.jsonId}")
                            }

                            if (isContentDifferent(jsonHit, dbHit) && dbHit!!.img == null) {
//                                pixabayDao.updateContent(jsonHit.previewURL, jsonHit.largeImageURL, jsonHit.user, jsonHit.likes, jsonHit.jsonId)
                                Log.d(TAG,"fetchApiImages: row updated ${jsonHit.jsonId} ${dbHit.jsonId}")
                                listToUpdate.add(jsonHit)
                            }

                        }
                        pixabayDao.insert(jsonHits)
                        if ( !listToUpdate.isEmpty() ) {
                            pixabayDao.updateAll(listToUpdate)
                        }
                        Log.d(TAG,"fetchApiImages success: pixabayDao insert called, size ${jsonHits.size}")
                    },

                    {
                        Log.d(TAG, "fetchApiImages onError : ${it.message}")
                    }
                )
        )
    }

    private fun isContentDifferent(jsonHit: HitPlusImgEntity, dbHit: HitPlusImgEntity?): Boolean {
        return (dbHit != null
                && (jsonHit.previewURL != dbHit.previewURL ||
                jsonHit.largeImageURL != dbHit.largeImageURL ||
                jsonHit.user != dbHit.user ||
                jsonHit.likes != dbHit.likes))
    }

    fun onViewFinished() {
        disposable.clear()
    }
}