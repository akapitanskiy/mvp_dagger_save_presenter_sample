package com.alexthekap.mvp_dagger_save_presenter_sample.di.component

import android.content.Context
import androidx.room.Room
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostsDao
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PostsDatabase
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.JsonPlaceholderApi
import com.alexthekap.mvp_dagger_save_presenter_sample.data.nerwork.services.PixabayApi
import com.alexthekap.mvp_dagger_save_presenter_sample.data.repository.MainRepository
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.main.MainActivity
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.main.MainContract
import com.alexthekap.mvp_dagger_save_presenter_sample.ui.main.MainPresenter
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.AppConstants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Scope

/**
 * created on 16.02.2021 17:38
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope



@ActivityScope
@Subcomponent(modules = [BindsPresenterModule::class,
                         RepoModuleProvider::class])
interface MainActivityComponent {

    fun inject(activity: MainActivity)
}



@Module
interface BindsPresenterModule {

//    @Binds
//    fun bindRepository(impl: MainRepository): MainRepository

    @Binds
    @ActivityScope
    fun bindPresenter(impl: MainPresenter): MainContract.IPresenter // нужен т.к. типы не совпадают
}



@Module
class RepoModuleProvider {

    @Provides
    @ActivityScope
    fun provideJsonPlaceholderApi(
        retrofit: Retrofit
    ): JsonPlaceholderApi {
        return retrofit.create(JsonPlaceholderApi::class.java)
    }

    @Provides
    @ActivityScope
//    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.JSON_PLACEHOLDER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }


    @Provides
    @ActivityScope
    fun providePixabayApi(
        retrofit: Retrofit
    ): PixabayApi {
        return retrofit.create(PixabayApi::class.java)
    }

//    @Provides
//    @ActivityScope
////    @Singleton
//    fun provideRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(AppConstants.PIXABAY_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .build()
//    }


    @Provides
    @ActivityScope
    fun providePostsRoomDb(context: Context): PostsDatabase =
        Room.databaseBuilder(
            context,
            PostsDatabase::class.java,
            PostsDatabase.DB_FILE_NAME
        ).build()

    @Provides
    @ActivityScope
    fun provideCloudSignUserDao(database: PostsDatabase): PostsDao =
        database.getPostsDao()

//    @Provides
//    @ActivityScope
//    fun provideRepository(jsonPlaceholderApi: JsonPlaceholderApi): MainRepository {
//        return MainRepository(jsonPlaceholderApi)
//    }

}