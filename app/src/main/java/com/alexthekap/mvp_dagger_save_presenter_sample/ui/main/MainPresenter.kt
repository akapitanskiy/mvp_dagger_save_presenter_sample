package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.os.CountDownTimer
import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.data.repository.MainRepository
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.ActivityScope
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    init {
        Log.d(this.javaClass.simpleName, "created: ")
    }

    override fun onViewReady(isFirstLaunch: Boolean) {
        startTimer()
        fetchData(isFirstLaunch)
    }

    private fun fetchData(isFirstLaunch: Boolean) {
        disposable.add( mainRepository.fetchData(isFirstLaunch)
            .map {
                return@map it.sortedBy { item -> item.title }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view?.updateList(it) }
        )
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

    override fun onViewReady() {
        // Empty
    }
}