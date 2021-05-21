package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.repository.HomeRepository
import java.lang.Exception
import java.lang.RuntimeException

internal class SearchComicsUseCase(
    private val homeRepository: HomeRepository
) {

    sealed class Result {
        data class Success(val data: MutableList<ComicDomainModel>) : Result()
        data class Error(val e: Throwable) : Result()
    }

    suspend fun execute(searchText : String):
            Result = try {
        homeRepository.searchComics(
            searchText
        )?.let {
            Result.Success(it.toMutableList())
        }
    } catch (e: Exception){
        Result.Error(e)
    }

}



