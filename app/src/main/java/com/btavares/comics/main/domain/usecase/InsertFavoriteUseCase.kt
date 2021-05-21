package com.btavares.comics.main.domain.usecase

import android.graphics.Bitmap
import com.btavares.comics.main.domain.repository.HomeRepository
import java.lang.Exception

internal class InsertFavoriteUseCase(
    private val homeRepository: HomeRepository
) {

    sealed class Result {
        data class Success(val data: Boolean) : Result()
        data class Error(val e: Throwable) : Result()
    }

    suspend fun execute(comicNumber : Int, imageBitmap: Bitmap):
            Result = try {
        homeRepository.insertFavorite(
            comicNumber,
            imageBitmap
        )?.let {
            Result.Success(true)
        }
    } catch (e: Exception){
        Result.Error(e)
    }

}



