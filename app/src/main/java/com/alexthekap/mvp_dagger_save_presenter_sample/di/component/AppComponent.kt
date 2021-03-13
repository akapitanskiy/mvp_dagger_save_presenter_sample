package com.alexthekap.mvp_dagger_save_presenter_sample.di.component

import com.alexthekap.mvp_dagger_save_presenter_sample.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * created on 16.02.2021 16:56
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun plusMainActivityComponent(): MainActivityComponent
}