package com.alexthekap.mvp_dagger_save_presenter_sample.data.repository

import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.*
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.PictureResponse
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.PixabayApi
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * created on 19.02.2021 16:32
 */
class MainRepository @Inject constructor( // TODO internal class надо ли ???
    private val pixabayApi: PixabayApi,
    private val pixabayDao: PixabayDao
) {

    private val TAG = "MainRepositoryTag"
    private val disposable = CompositeDisposable()
    private var fetchPixabayDataDebouncedSubj = PublishSubject.create<Unit>()

    init {
        fetchPixabayDataDebouncedSubj
            .throttleFirst(2000, TimeUnit.MILLISECONDS)
            .subscribe {
                Log.d(TAG, "debounce: called fetchApiImages()")
                fetchApiImages()
            }
            .addTo(disposable)
    }

    fun fetchPixabayData(): Observable<List<HitPlusImgEntity>> {

        return Observable.concat(
                pixabayDao.getAllHitsFromDbMaybe().toObservable(),
                loadFromServerAndObserveDb()
            )
            .distinctUntilChanged()
    }

    private fun loadFromServerAndObserveDb(): Observable<List<HitPlusImgEntity>> {

        return loadFromNetwork()
            .flatMap { saveHitsToDb(it.hits) }
            .map { for (hit in it) {
                    fetchImage(hit)
                 }
            }
            .flatMapObservable { pixabayDao.getAllHitsFromDbObs() }
    }

    private fun saveHitsToDb(hits: List<HitPlusImgEntity>): Single<List<HitPlusImgEntity>> {

        return pixabayDao.insert(hits)
            .flatMap { Single.just(hits) }
//            .doOnError {
//                logException(it, this)
//            }
    }

    private fun loadFromNetwork(): Single<PictureResponse> {
        return pixabayApi.getImages("yellow+flowers")
    }

    fun fetchImage(hitEntity: HitPlusImgEntity) {

        val call = pixabayApi.downloadFile(hitEntity.largeImageURL)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d(TAG, "ERROR onResponse: ${response.errorBody()}")
//                    fetchPixabayDataDebouncedSubj.onNext(Unit)
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

    private fun update(array: ByteArray, jsonId: Long) {

        Completable.fromAction {
            pixabayDao.updateImg(array, jsonId)
            Log.d(TAG, "update: ByteArray size ${array.size}")
        }
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onComplete = {},
            onError = {
                logException(it, this)
            }
        ).addTo(disposable)
    }

    private fun getByteArray(responseBody: ResponseBody): ByteArray {

        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var n: Int
        val inputStream = responseBody.byteStream()

        while (true) {
            n = inputStream.read(buf)
            if (n == -1) break
            outputStream.write(buf, 0, n)
        }
        outputStream.close()
        inputStream.close()
        return outputStream.toByteArray()
    }
 
    private fun fetchApiImages() {
        disposable.add(
            pixabayApi.getImages("yellow+flowers")
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

                            if (dbHit?.img != null && (dbHit.likes != jsonHit.likes || dbHit.creator != jsonHit.creator)) {
                                pixabayDao.updateLikesAndUser(jsonHit.creator, jsonHit.likes, jsonHit.jsonId)
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
                        jsonHit.creator != dbHit.creator ||
                        jsonHit.likes != dbHit.likes ))
    }

    fun onViewFinished() {
        disposable.clear()
    }
}