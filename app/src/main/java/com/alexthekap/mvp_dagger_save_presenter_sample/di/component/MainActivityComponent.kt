package com.alexthekap.mvp_dagger_save_presenter_sample.di.component

import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PixabayDao
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PixabayDatabase
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.PixabayApi
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.main.MainActivity
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import retrofit2.Retrofit
import javax.inject.Scope

/**
 * created on 16.02.2021 17:38
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope



@ActivityScope
@Subcomponent(modules = [ RepoModuleProvider::class ])
interface MainActivityComponent {

    fun inject(activity: MainActivity)
}



@Module
class RepoModuleProvider {

    @Provides
    @ActivityScope
    fun providePixabayApi(
        retrofit: Retrofit
    ): PixabayApi {
        return retrofit.create(PixabayApi::class.java)
    }


    @Provides
    @ActivityScope
    fun providePixabayDao(db: PixabayDatabase): PixabayDao {
        return db.getPixabayDao()
    }

}