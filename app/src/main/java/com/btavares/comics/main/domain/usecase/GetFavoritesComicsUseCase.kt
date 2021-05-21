package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.repository.HomeRepository
import java.lang.Exception
import java.lang.RuntimeException

internal class GetFavoritesComicsUseCase(
    private val homeRepository: HomeRepository
) {

    sealed class Result {
        data class Success(val data: List<ComicDomainModel>) : Result()
        data class Error(val e: Throwable) : Result()
    }

    suspend fun execute():
            Result = try {
        homeRepository.getFavoritesComics(
        )?.let {
            Result.Success(it)
        } ?: Result.Error(RuntimeException("Error"))
    } catch (e: Exception){
        Result.Error(e)
    }

}



