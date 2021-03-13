package com.alexthekap.mvp_dagger_save_presenter_sample.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * created on 02.03.2021 10:24
 */
@Entity(tableName = "posts_table")
data class PostEntity(

    var userId: Int,

    @PrimaryKey(autoGenerate = false)
    var id: Int,

    var title: String,

    var body: String
)
