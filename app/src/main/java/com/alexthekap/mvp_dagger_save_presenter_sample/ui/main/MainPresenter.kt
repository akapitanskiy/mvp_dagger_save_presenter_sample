package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.os.CountDownTimer
import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.data.repository.MainRepository
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.ActivityScope
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * created on 17.02.2021 10:35
 */
@ActivityScope
class MainPresenter @Inject constructor(
    private val mainRepository: MainRepository
) : BasePresenter<MainContract.IView>(), MainContract.IPresenter {

    private var isRunning = false
    private val interval = 1000L
    private val seconds = 101L
    private var timerVal = seconds.toString()
    private var isFirstLaunch = true

    companion object {
        private const val TAG = "MainPresenter"
    }

    init {
        Log.d(TAG, "created")
    }

    override fun onViewReady() {
        startTimer()
        fetchImagesUrls()
    }

    private fun fetchImagesUrls() {

        disposable.add(mainRepository.fetchPixabayData(isFirstLaunch)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view?.updateList(it)
                },
                {
                    Log.d(TAG, "fetchImageUrls error: ${it.message}")
                }
            )
        )
    }

    override fun fetchImage(hitEntity: HitPlusImgEntity) {
        mainRepository.fetchImage(hitEntity)
    }

    private fun startTimer() {
        view?.updateTimer(timerVal)
        if (!isRunning) {
            object : CountDownTimer(seconds * interval, interval) {
                override fun onTick(time: Long) {
                    timerVal = (time/1000).toString()
                    view?.updateTimer(timerVal)
                }

                override fun onFinish() {
                    isRunning = false
                    view?.updateTimer(seconds.toString())
                }
            }.start()
            isRunning = true
        }
    }

    override fun onFinished() {
        mainRepository.onViewFinished()
    }

    override fun isFirstLaunch(isFirstLaunch: Boolean) {
        this.isFirstLaunch = isFirstLaunch
    }
}