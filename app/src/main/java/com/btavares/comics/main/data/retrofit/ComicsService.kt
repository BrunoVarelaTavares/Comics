package com.btavares.comics.main.data.retrofit

import com.btavares.comics.main.data.model.ComicDataModel
import retrofit2.http.GET
import retrofit2.http.Path

internal interface ComicsService {

    @GET("info.0.json")
    suspend fun getCurrentComic(
    ) : ComicDataModel?

    @GET("{number}/info.0.json ")
    suspend fun getComicByNumber(
        @Path("number") id : Int
    ) : ComicDataModel?

}