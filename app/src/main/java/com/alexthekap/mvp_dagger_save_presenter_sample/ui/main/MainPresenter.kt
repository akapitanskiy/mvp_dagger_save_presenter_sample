package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import android.os.CountDownTimer
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.data.repository.MainRepository
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.ActivityScope
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.BasePresenter
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.main.State.*
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * created on 17.02.2021 10:35
 */
@ActivityScope
class MainPresenter @Inject constructor(
    private val mainRepository: MainRepository
) : BasePresenter<MainView>() {

    private var isFirstLaunch = true

    private var isRunning = false
    private val interval = 1000L
    private val seconds = 101L
    private var timerVal = seconds.toString()

    @Volatile var state = DONE
    @Volatile private var page = INITIAL_PAGE
    @Volatile private var itemCount = 0

    companion object {
        private const val INITIAL_PAGE = 1
        private const val OFFSET = 10
    }

    init {
        logMessage(this, "created")
    }

    override fun onViewReady() {
        startTimer()
        fetchData()
    }

    private fun fetchData() {

        if (isFirstLaunch) {
            state = LOADING
            logMessage(this, "loadMore fetchData 1")
            mainRepository.observeDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.updateList(it)
                        if (it.isEmpty()) {
                            view?.updateList(arrayListOf(HitPlusImgEntity(-1, "", "", "Offline mode", 0)))
                        }
                    },
                    {
                        logMessage(this, "loadMore observeDb error ${it.message}")
                        state = ERROR }
                ).addTo(disposable)
            logMessage(this, "loadMore fetchData 2")

            mainRepository.loadFromNetworkAndSaveToDb(INITIAL_PAGE)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        page = INITIAL_PAGE + 1
                        state = DONE
                        itemCount += it },
                    {
                        logMessage(this, "loadMore loadFromNetworkAndSaveToDb error ${it.message}")
                        state = ERROR }
                ).addTo(disposable)
        }
    }

    fun loadMore(lastVisibleItem: Int) {
        logMessage(this, "loadMore: out $state items=$itemCount last+offs=${lastVisibleItem + OFFSET} p=$page")
        if ( (state == DONE || state == ERROR)
            && itemCount < (lastVisibleItem + OFFSET)
        ) {
            state = LOADING
            mainRepository.loadFromNetworkAndSaveToDb(page)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        page += 1
                        state = DONE
                        itemCount += it
                        logMessage(this, "loadMore OnSuccess $state $page") },
                    {
                        state = ERROR
                        logMessage(this, "loadMore OnError $state $page err msg=${it.message}") }
                ).addTo(disposable)
            logMessage(this, "loadMore: end $state $page")
        }
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

    fun isFirstLaunch(isFirstLaunch: Boolean) {
        this.isFirstLaunch = isFirstLaunch
    }
}