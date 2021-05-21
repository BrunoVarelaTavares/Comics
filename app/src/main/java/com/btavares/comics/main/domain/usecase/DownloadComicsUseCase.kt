package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.domain.repository.HomeRepository
import java.lang.Exception

internal class DownloadComicsUseCase (
    private val homeRepository: HomeRepository
) {
    sealed class Result{
        data class Success(val data: Boolean) : Result()
        data class Error(val e: Throwable) : Result()
    }

    suspend fun execute(
    ): Result = try {
        homeRepository.downloadComics(
        ).let {
            Result.Success(true)
        }
    } catch (e: Exception){
        Result.Error(e)
    }

}
