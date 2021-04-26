package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * created on 19.03.2021 19:44
 */
@Dao
interface PixabayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // TODO ignore
    fun insert(hitList: List<HitPlusImgEntity>): Single<List<Long>>

    @Query("SELECT * FROM hits_table")
    fun getAllHitsFromDbObserv(): Flowable<List<HitPlusImgEntity>>

    @Query("SELECT * FROM hits_table WHERE jsonId = :id")
    fun getById(id: Long): HitPlusImgEntity?

    @Query("SELECT * FROM hits_table WHERE jsonId = :id")
    fun getAsyncById(id: Long): Single<HitPlusImgEntity>

//    @Transaction
//    fun updateAll(hitList: List<HitPlusImgEntity>) {
//        for (hit in hitList) {
//            updateLikesAndUser(hit.creator, hit.likes, hit.jsonId)
//        }
//    }

    @Query("UPDATE hits_table SET img = :imgByteArr WHERE jsonId = :id")
    fun updateImg(imgByteArr: ByteArray, id: Long)

    @Query("UPDATE hits_table SET creator=:creator, likes=:likes WHERE jsonId=:id")
    fun updateLikesAndUser(creator: String, likes: Int, id: Long): Single<Int>

}