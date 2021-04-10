package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * created on 19.03.2021 19:37
 */
@Database(entities = [HitPlusImgEntity::class], version = 8)
abstract class PixabayDatabase : RoomDatabase() {

    companion object {
        const val PIX_DB_FILE_NAME = "pixabay.db"
    }

    abstract fun getPixabayDao(): PixabayDao
}