package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import android.util.Log
import androidx.room.*
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.CompletableSubject

/**
 * created on 19.03.2021 19:44
 */
@Dao
interface PixabayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // TODO ignore
    fun insert(hitList: List<HitPlusImgEntity>): Single<List<Long>>

    @Query("SELECT * FROM hits_table")
    fun getAllHitsFromDbObs(): Observable<List<HitPlusImgEntity>>

//    @Query("SELECT * FROM hits_table")
//    fun getAllHitsFromDbMaybe(): Maybe<List<HitPlusImgEntity>>

    @Query("SELECT * FROM hits_table WHERE jsonId = :id")
    fun getById(id: Long): HitPlusImgEntity?

//    @Query("SELECT * FROM images_table WHERE id=:id")
//    fun getImageByPixabayId(id: Int): Observable<ImageEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertByteImage(imageEntity: ImageEntity)

    @Transaction
    fun updateAll(hitList: List<HitPlusImgEntity>) {
//    fun updateAll(hitList: List<HitPlusImgEntity>) {

        val singles = ArrayList<Completable>()
        for (hit in hitList) {
            updateLikesAndUser(hit.creator, hit.likes, hit.jsonId)
        }
    }

    @Query("UPDATE hits_table SET img = :imgByteArr WHERE jsonId = :id")
    fun updateImg(imgByteArr: ByteArray, id: Long)

    @Query("UPDATE hits_table SET previewURL=:previewURL, largeImageURL=:largeImageURL, creator=:creator, likes=:likes WHERE jsonId=:id")
    fun updateContent(previewURL: String, largeImageURL: String, creator: String, likes: Int, id: Long): Int

    @Query("UPDATE hits_table SET creator=:creator, likes=:likes WHERE jsonId=:id")
    fun updateLikesAndUser(creator: String, likes: Int, id: Long): Single<Int>

}