package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostEntity
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.BaseIPresenter

/**
 * created on 17.02.2021 10:34
 */
interface MainContract {

    interface IPresenter : BaseIPresenter<IView> {

        fun onViewReady(isFirstLaunch: Boolean)
    }

    interface IView {

        fun updateTimer(time: String)
        fun updateList(list: List<PostEntity>)
    }
}