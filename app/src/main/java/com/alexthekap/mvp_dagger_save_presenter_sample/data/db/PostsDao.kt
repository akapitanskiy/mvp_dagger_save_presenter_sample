package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable

/**
 * created on 02.03.2021 10:53
 */
@Dao
interface PostsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postList: List<PostEntity>)

    @Query("SELECT * FROM posts_table ORDER BY title ASC")
    fun getAll(): Observable<List<PostEntity>>
}