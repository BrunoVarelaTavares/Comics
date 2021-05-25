package com.btavares.comics.main.domain

import android.graphics.Bitmap
import com.btavares.comics.app.presentation.extension.getImageBitmap
import com.btavares.comics.main.domain.model.ComicDomainModel

object DomainFixtures {

    internal fun getComicDomainModel(
        month : Int  = 1,
        number : Int = 1,
        link : String = "link",
        year : Int = 2020,
        news : String = "news",
        safeTitle : String = "safeTitle",
        description : String = "description",
        alt : String = "alt",
        imageUrl : String = "imageUrl",
        title : String = "title",
        day : Int = 4,
        isFavorite : Boolean = false,
        bitmap: Bitmap? = null
    ) : ComicDomainModel = ComicDomainModel(month, number, link, year, news, safeTitle, description, alt, imageUrl, title, day, isFavorite , bitmap)
}