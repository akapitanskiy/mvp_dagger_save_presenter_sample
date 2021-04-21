package com.alexthekap.mvp_dagger_save_presenter_sample.di

import android.content.Context
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.AppComponent
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.DaggerAppComponent
import com.alexthekap.mvp_dagger_save_presenter_sample.di.component.MainActivityComponent
import com.alexthekap.mvp_dagger_save_presenter_sample.di.module.AppModule

/**
 * created on 16.02.2021 16:03
 */
object ComponentManager {

    private lateinit var appComponent: AppComponent
    private var mainActivityComponent: MainActivityComponent? = null

    fun init(applicationContext: Context) {
//        appContext = applicationContext
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(applicationContext))
            .build()
    }

    fun getMainActivityComponent(): MainActivityComponent {
        if (mainActivityComponent == null) {
            mainActivityComponent = appComponent.plusMainActivityComponent()
        }
        return mainActivityComponent!!
    }

    fun clearMainActivityComponent() {
        mainActivityComponent = null
    }

}