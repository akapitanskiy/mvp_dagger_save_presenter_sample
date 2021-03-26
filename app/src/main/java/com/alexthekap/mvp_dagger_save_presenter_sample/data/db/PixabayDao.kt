package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.*
import io.reactivex.Observable

/**
 * created on 19.03.2021 19:44
 */
@Dao
interface PixabayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // TODO ignore
    fun insert(hitList: List<HitPlusImgEntity>)

    @Query("SELECT * FROM hits_table")
    fun getAllHitsFromDb(): Observable<List<HitPlusImgEntity>>

    @Query("SELECT * FROM hits_table WHERE jsonId = :id")
    fun getById(id: Long): HitPlusImgEntity?

//    @Query("SELECT * FROM images_table WHERE id=:id")
//    fun getImageByPixabayId(id: Int): Observable<ImageEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertByteImage(imageEntity: ImageEntity)

    @Transaction
    fun updateAll(hitList: List<HitPlusImgEntity>) {
        for (hit in hitList) {
            updateContent(hit.previewURL, hit.largeImageURL, hit.user, hit.likes, hit.jsonId)
        }
    }

    @Query("UPDATE hits_table SET img = :imgByteArr WHERE jsonId = :id")
    fun updateImg(imgByteArr: ByteArray, id: Long)

    @Query("UPDATE hits_table SET previewURL=:previewURL, largeImageURL=:largeImageURL, user=:user, likes=:likes WHERE jsonId=:id")
    fun updateContent(previewURL: String, largeImageURL: String, user: String, likes: Int, id: Long): Int

    @Query("UPDATE hits_table SET user=:user, likes=:likes WHERE jsonId=:id")
    fun updateLikesAndUser(user: String, likes: Int, id: Long): Int
}