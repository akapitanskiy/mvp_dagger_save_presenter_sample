package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

/**
 * created on 22.03.2021 14:45
 */
@Entity(
    tableName = "images_table",
    foreignKeys = [ForeignKey(
        entity = HitPlusImgEntity::class, parentColumns = ["jsonId"], childColumns = ["jsonId"], onDelete = CASCADE
    )]
)
data class ImageEntity(

    @PrimaryKey(autoGenerate = false)
    var id: Int,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var img: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        if (id != other.id) return false
        if (!img.contentEquals(other.img)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + img.contentHashCode()
        return result
    }
}
