package com.alexthekap.mvp_dagger_save_presenter_sample.di.module

import android.content.Context
import androidx.room.Room
import com.alexthekap.mvp_dagger_save_presenter_sample.data.db.PixabayDatabase
import com.alexthekap.mvp_dagger_save_presenter_sample.utils.AppConstants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * created on 16.02.2021 16:58
 */
@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.PIXABAY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providePixabayDb(context: Context): PixabayDatabase {
        return Room.databaseBuilder(
                context,
                PixabayDatabase::class.java,
                PixabayDatabase.PIX_DB_FILE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}