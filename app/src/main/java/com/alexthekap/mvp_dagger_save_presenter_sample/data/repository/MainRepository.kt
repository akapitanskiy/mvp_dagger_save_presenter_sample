package com.alexthekap.mvp_dagger_save_presenter_sample.data.repository

import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.*
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.model.PictureResponse
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.PixabayApi
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.ActivityScope
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logException
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * created on 19.02.2021 16:32
 */
@ActivityScope
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
                logMessage(this, "debounce: called fetchApiImages()")
//                fetchApiImages()
            }
            .addTo(disposable)
    }

    fun observeDb(page: Int): Observable<List<HitPlusImgEntity>> {
        return pixabayDao.getAllHitsFromDbObs()
//        return Observable.concat(
//                pixabayDao.getAllHitsFromDbMaybe().toObservable(),
//                loadFromNetworkAndObserveDb(page)
//            )
//            .distinctUntilChanged()
    }

//    private fun loadFromNetworkAndObserveDb(page: Int): Observable<List<HitPlusImgEntity>> {
//        return loadFromNetworkAndSaveToDb(page)
//            .flatMapObservable { pixabayDao.getAllHitsFromDbObs() }
//    }

    @Synchronized
    fun loadFromNetworkAndSaveToDb(page: Int): Single<Unit> {
        logMessage(this, "loadMore rep loadFromNetworkAndSaveToDb: $page")
        return loadFromNetwork(page)
            .doOnError {
                logMessage(this, "loadFromNetworkAndSaveToDb: ERROR $it ${it.message}")
            }
            .retry(3)
            .flatMap { saveHitsToDb(it.hits) }
            .map {
                logMessage(this, "loadFromNetworkAndSaveToDb: map fetchImages(it)")
                fetchImages(it)
            }
    }

    private fun fetchImages(list: List<HitPlusImgEntity>) {
        for (hit in list) {
            fetchImage(hit)
        }
    }

    private fun saveHitsToDb(hits: List<HitPlusImgEntity>): Single<List<HitPlusImgEntity>> {

        val listToUpdate = ArrayList<HitPlusImgEntity>()
        val listToInsert = ArrayList<HitPlusImgEntity>()
        logMessage(this, "saveHitsToDb(): ${Thread.currentThread().name} ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
//        val listToUpdateContent = ArrayList<HitPlusImgEntity>()
        for (jsonHit in hits) {
            val dbHit = pixabayDao.getById(jsonHit.jsonId)

            if (dbHit == null || dbHit.img == null) {
//                if (jsonHit.creator == "ulleo" || jsonHit.creator == "klimkin") {
//                    jsonHit.creator = "lkjlkjlkj"
//                }
//                jsonHit.creator = "lkjlkjlkj"
                listToInsert.add(jsonHit)
                logMessage(this, "saveHitsToDb(): listToInsert.added ${jsonHit.jsonId}")
            } else if (dbHit.likes != jsonHit.likes || dbHit.creator != jsonHit.creator) {
                listToUpdate.add(jsonHit)
                logMessage(this, "saveHitsToDb(): listToUpdate.added ${jsonHit.jsonId}")
            }

//            if (dbHit?.img != null && (dbHit.likes != jsonHit.likes || dbHit.creator != jsonHit.creator)) {
////                pixabayDao.updateLikesAndUser(jsonHit.creator, jsonHit.likes, jsonHit.jsonId)
//                listToUpdate.add(dbHit)
//                logMessage(this,"fetchApiImages: updateLikesAndUser row ${jsonHit.jsonId} ${dbHit.jsonId}")
//            }
//
//            if (isContentDifferent(jsonHit, dbHit) && dbHit!!.img == null) {
////                                pixabayDao.updateContent(jsonHit.previewURL, jsonHit.largeImageURL, jsonHit.user, jsonHit.likes, jsonHit.jsonId)
//                logMessage(this,"fetchApiImages: row updated ${jsonHit.jsonId} ${dbHit.jsonId}")
//                listToUpdate.add(jsonHit)
//            }
        }
//        pixabayDao.insert(listToInsert)
//        if ( !listToUpdate.isEmpty() ) {
//            pixabayDao.updateAll(listToUpdate)
//        }

        return pixabayDao.insert(listToInsert)
            .flatMap {
                val list = ArrayList<Single<Int>>()
//                val completableChain = Completable.fromAction { Unit }
                val singleChain = Single.fromCallable { 111 }
                val flow = Flowable.just(222)
                logMessage(this, "saveHitsToDb(): flatMap Upd 1")
                for (hit in listToUpdate) {
//                    completableChain.concatWith(pixabayDao.updateLikesAndUser(hit.creator, hit.likes, hit.jsonId))
                    logMessage(this, "saveHitsToDb(): flatMap Upd for ${hit.jsonId}")
                    flow.concatWith(pixabayDao.updateLikesAndUser(hit.creator, hit.likes, hit.jsonId))
                }
                logMessage(this, "saveHitsToDb(): flatMap Upd 2")
//                for (single in list) {
//                    single.subscribe(
//                        {
//                            logMessage(this, "saveHitsToDb(): for 2 onSuccess $it")
//                        },
//                        {
//                            logMessage(this, "saveHitsToDb(): ERROR!!!!!!!!!! for 2 onError ${it.message}")
//                        }
//                    )
//                }
//                flow.subscribe(
//                    {
//                        logMessage(this, "saveHitsToDb(): for 2 onSuccess $it")
//                    },
//                    {}
//                )
                logMessage(this, "saveHitsToDb(): flatMap Upd 3")
                flow.subscribe()
                flow.toList()
            }
            .flatMap {
                logMessage(this, "saveHitsToDb(): flatMap Single.just ")
                Single.just(listToInsert) }
//            .doOnError {
//                logException(it, this)
//            }
    }

    private fun loadFromNetwork(page: Int): Single<PictureResponse> {
        return pixabayApi.getImages("yellow+flowers", page)
    }

    private fun fetchImage(hitEntity: HitPlusImgEntity) {
        logMessage(this, "fetchImage(): ${hitEntity.jsonId}")

        val call = pixabayApi.downloadFile(hitEntity.largeImageURL)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    logMessage(this, "ERROR fetchImage() onResponse: ${response.errorBody()}")
//                    fetchPixabayDataDebouncedSubj.onNext(Unit)
                } else {
                    response.body()?.let {
                        val arr = getByteArray(it)
                        update(arr, hitEntity.jsonId)
                        logMessage(this, "fetchImage() onResponse: ByteArray size ${arr.size}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                logMessage(this, "ERROR fetchImage() onFailure: ${t.message}")
                fetchPixabayDataDebouncedSubj.onNext(Unit)
            }
        })
    }

    private fun update(array: ByteArray, jsonId: Long) {
        Completable.fromAction {
            pixabayDao.updateImg(array, jsonId)
            logMessage(this, "update(): ByteArray size ${array.size}")
        }
        .subscribeOn(Schedulers.io())
        .subscribeBy(
            onComplete = {},
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
 
    private fun fetchApiImages() {
        disposable.add(
            pixabayApi.getImages("yellow+flowers", 1) // TODO page
                .map {
                    logMessage(this, "fetchApiImages() map: GET images api called. response: $it")
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
                                logMessage(this,"fetchApiImages(): updateLikesAndUser row ${jsonHit.jsonId} ${dbHit.jsonId}")
                            }

                            if (isContentDifferent(jsonHit, dbHit) && dbHit!!.img == null) {
//                                pixabayDao.updateContent(jsonHit.previewURL, jsonHit.largeImageURL, jsonHit.user, jsonHit.likes, jsonHit.jsonId)
                                logMessage(this,"fetchApiImages(): row updated ${jsonHit.jsonId} ${dbHit.jsonId}")
                                listToUpdate.add(jsonHit)
                            }
                        }
                        pixabayDao.insert(jsonHits)
                        if ( !listToUpdate.isEmpty() ) {
                            pixabayDao.updateAll(listToUpdate)
                        }
                        logMessage(this,"fetchApiImages() success: pixabayDao insert called, size ${jsonHits.size}")
                    },

                    {
                        logMessage(this, "fetchApiImages() onError : ${it.message}")
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