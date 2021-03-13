package com.alexthekap.mvp_dagger_save_presenter_sample

import android.app.Application
import android.content.Context
import com.alexthekap.mvp_dagger_save_presenter_sample.di.ComponentManager

/**
 * created on 16.02.2021 14:07
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentManager.init(this)
    }

    fun getApp(context: Context): App {
        return context.applicationContext as App
    }


}