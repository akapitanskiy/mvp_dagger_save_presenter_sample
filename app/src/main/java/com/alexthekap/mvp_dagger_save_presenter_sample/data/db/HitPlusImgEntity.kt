package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * created on 19.03.2021 19:32
 */
@Entity(tableName = "hits_table", indices = [Index(value = ["jsonId"], unique = true)] )
data class HitPlusImgEntity(

    @SerializedName("id")
    val jsonId: Long,

    @SerializedName("previewURL")
    val previewURL: String,

    @SerializedName("largeImageURL")
    val largeImageURL: String,

    @SerializedName("user") // TODO try  creator instead user
    var creator: String,

    @SerializedName("likes")
    val likes: Int
) {
    @PrimaryKey(autoGenerate = true)
//    @SerializedName("dbId") // TODO надо ли ?
    var dbId: Long = -1

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var img: ByteArray? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HitPlusImgEntity

        if (jsonId != other.jsonId) return false
        if (previewURL != other.previewURL) return false
        if (largeImageURL != other.largeImageURL) return false
        if (creator != other.creator) return false
        if (likes != other.likes) return false
        if (!img.contentEquals(other.img)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jsonId.hashCode()
        result = 31 * result + previewURL.hashCode()
        result = 31 * result + largeImageURL.hashCode()
        result = 31 * result + creator.hashCode()
        result = 31 * result + likes
        result = 31 * result + dbId.hashCode()
        result = 31 * result + (img?.contentHashCode() ?: 0)
        return result
    }
}
