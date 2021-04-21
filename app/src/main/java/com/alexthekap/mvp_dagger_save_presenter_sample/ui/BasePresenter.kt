package com.alexthekap.mvp_dagger_save_presenter_sample.ui

import android.util.Log
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.logMessage
import io.reactivex.disposables.CompositeDisposable

/**
 * created on 17.02.2021 11:28
 */
abstract class BasePresenter<V> : BaseIPresenter<V> {

    var view: V? = null
    val disposable = CompositeDisposable()

    override fun bindView(view: V) {
        logMessage(this, "view attached")
        this.view = view
    }

    override fun unbindView() {
        logMessage(this, "view detached")
        view = null
        disposable.clear()
    }

//    override fun onFinished() {
//        // TODO ?
//    }
}