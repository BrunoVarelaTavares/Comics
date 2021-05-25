package com.btavares.comics.main.data

import com.btavares.comics.main.data.model.ComicDataModel

object DataFixtures {

    internal fun getComicDataModelOne(
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
        isFavorite : Boolean = false
    ) : ComicDataModel = ComicDataModel(month, number, link, year, news, safeTitle, description, alt, imageUrl, title, day, isFavorite)

    internal fun getComicDataModelTwo(
        month : Int  = 1,
        number : Int = 2,
        link : String = "link",
        year : Int = 2020,
        news : String = "news",
        safeTitle : String = "safeTitle",
        description : String = "description",
        alt : String = "alt",
        imageUrl : String = "imageUrl",
        title : String = "title",
        day : Int = 4,
        isFavorite : Boolean = false
    ) : ComicDataModel = ComicDataModel(month, number, link, year, news, safeTitle, description, alt, imageUrl, title, day, isFavorite)


}