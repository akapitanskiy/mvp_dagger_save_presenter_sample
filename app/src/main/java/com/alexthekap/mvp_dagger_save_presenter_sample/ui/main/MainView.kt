package com.alexthekap.mvp_dagger_save_presenter_sample.ui.main

import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.HitPlusImgEntity

/**
 * created on 03.04.2021 13:34
 */
interface MainView {

    fun updateTimer(time: String)

    fun updateList(list: List<HitPlusImgEntity>)
}