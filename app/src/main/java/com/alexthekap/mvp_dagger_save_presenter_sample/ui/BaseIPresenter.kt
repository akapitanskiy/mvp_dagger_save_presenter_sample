package com.alexthekap.mvp_dagger_save_presenter_sample.ui

/**
 * created on 17.02.2021 11:58
 */
interface BaseIPresenter<V> {

    fun bindView(view: V)

    fun unbindView()

    fun onFinished()

    fun onViewReady()
}