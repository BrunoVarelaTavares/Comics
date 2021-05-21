package com.btavares.comics.main.data.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.btavares.comics.main.domain.model.ComicDomainModel
import com.squareup.moshi.Json
import com.squareup.moshi.ToJson


@Entity(tableName = "comics")
internal data class ComicDataModel(
    val month : Int,
    @PrimaryKey @field:Json(name = "num") val number : Int,
    val link : String,
    val year : Int,
    val news : String,
    @ColumnInfo(name = "safe_title") @field:Json(name = "safe_title") val safeTitle : String,
    @field:Json(name = "transcript") val description : String,
    val alt : String,
    @ColumnInfo(name = "image_url") @field:Json(name = "img") val imageUrl : String,
    val title : String,
    val day : Int,
    var isFavorite : Boolean
)



internal fun ComicDataModel.toDomainModel() : ComicDomainModel {

    return  ComicDomainModel(
        month = this.month,
        number = this.number,
        link = this.link,
        year = this.year,
        news = this.news,
        safeTitle = this.safeTitle,
        description = this.description,
        alt = this.alt,
        imageUrl = this.imageUrl,
        title = this.title,
        day = this.day,
        favorite = isFavorite,
        comicBitmap = null
    )
}


internal fun ComicDataModel.toDomainModel(imageBitmap: Bitmap) : ComicDomainModel {

    return  ComicDomainModel(
        month = this.month,
        number = this.number,
        link = this.link,
        year = this.year,
        news = this.news,
        safeTitle = this.safeTitle,
        description = this.description,
        alt = this.alt,
        imageUrl = this.imageUrl,
        title = this.title,
        day = this.day,
        favorite = isFavorite,
        comicBitmap = imageBitmap
    )
}