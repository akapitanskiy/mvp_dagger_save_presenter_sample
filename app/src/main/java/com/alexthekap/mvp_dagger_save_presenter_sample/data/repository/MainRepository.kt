package com.alexthekap.mvp_dagger_save_presenter_sample.data.repository

import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PixabayDao
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.PictureResponse
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.PixabayApi
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.ActivityScope
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logException
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * created on 19.02.2021 16:32
 */
@ActivityScope
class MainRepository @Inject constructor(
    private val pixabayApi: PixabayApi,
    private val pixabayDao: PixabayDao
) {
    private val disposable = CompositeDisposable()
    private var fetchPixabayDataDebouncedSubj = PublishSubject.create<Unit>()
    var maxPhotos: Int = 0

    fun observeDb(): Flowable<List<HitPlusImgEntity>> {
        return pixabayDao.getAllHitsFromDbObserv()
    }

    @Synchronized
    fun loadFromNetworkAndSaveToDb(page: Int): Single<Int> {
        logMessage(this, "loadMore rep loadFromNetworkAndSaveToDb $page")
        if (page <= maxPhotos/20 + 1) {
            return loadFromNetwork(page)
                .doOnError {
                    logMessage(this, "loadFromNetworkAndSaveToDb ERROR $it msg=${it.message}")
                }
                .retry(2)
                .flatMap {
                    maxPhotos = it.totalHits
                    saveHitsToDb(it.hits)
                }
        } else {
            return Single.error { Error("maxPage reached") }
        }
    }

    fun getByJsonId(jsonId: Long): Single<HitPlusImgEntity> {
        return pixabayDao.getAsyncById(jsonId)
    }

    private fun fetchImages(list: List<HitPlusImgEntity>) {

        list.forEach { hitEntity ->
            val call = pixabayApi.downloadFile(hitEntity.largeImageURL)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (!response.isSuccessful) {
                        logMessage(this, "ERROR fetchImage onResponse ${response.errorBody()}")
//                    fetchPixabayDataDebouncedSubj.onNext(Unit)
                    } else {
                        response.body()?.let {
                            val arr = getByteArray(it)
                            updateImg(arr, hitEntity.jsonId)
                            logMessage(this, "fetchImage onResponse ByteArray size ${arr.size}")
                        }}
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    logMessage(this, "ERROR fetchImage onFailure ${t.message}")
                    fetchPixabayDataDebouncedSubj.onNext(Unit)
                }
            })
        }
    }

    private fun saveHitsToDb(hits: List<HitPlusImgEntity>): Single<Int> {

        val listToUpdate = ArrayList<HitPlusImgEntity>()
        val listToFetch = ArrayList<HitPlusImgEntity>()

        for (jsonHit in hits) {
            val dbHit = pixabayDao.getById(jsonHit.jsonId)
            if (dbHit?.img == null) {
//                jsonHit.creator = "qqlkjlkjlkj"
                listToFetch.add(jsonHit)
                logMessage(this, "saveHitsToDb listToInsert.added ${jsonHit.jsonId}")
            } else if (dbHit.likes != jsonHit.likes || dbHit.creator != jsonHit.creator) {
                listToUpdate.add(jsonHit)
                logMessage(this, "saveHitsToDb listToUpdate.added ${jsonHit.jsonId} ${jsonHit.creator}")
            }
        }

        return pixabayDao.insert(hits)
            .flatMap {
                fetchImages(listToFetch)
                logMessage(this, "saveHitsToDb flatMap Upd 1")
                for (hit in listToUpdate) {
                    pixabayDao.updateLikesAndUser(hit.creator, hit.likes, hit.jsonId)
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
                Single.just(hits.size)
            }
    }

    private fun loadFromNetwork(page: Int): Single<PictureResponse> {
        return pixabayApi.getImages("yellow+flowers", page)
    }

    private fun updateImg(array: ByteArray, jsonId: Long) {
        Completable.fromAction {
            pixabayDao.updateImg(array, jsonId)
            logMessage(this, "update ByteArray size ${array.size}")
        }
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onError = {
                logException(this, it)
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
 
    fun onViewFinished() {
        disposable.clear()
    }
}