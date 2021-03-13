package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * created on 01.03.2021 21:52
 */
@Database(entities = [PostEntity::class], version = 1)
abstract class PostsDatabase : RoomDatabase() {

    companion object {
        const val DB_FILE_NAME = "posts.db"
    }

    abstract fun getPostsDao(): PostsDao

}