package com.btavares.comics.main.data.room.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "image")
class Image(
    @PrimaryKey
    @field:Json(name = "comic_number") val comicNumber : Int,
    val comicBitmap: Bitmap?
)


