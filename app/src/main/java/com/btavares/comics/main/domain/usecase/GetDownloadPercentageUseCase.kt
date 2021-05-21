package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

internal class GetDownloadPercentageUseCase (
    private val homeRepository: HomeRepository
) {

    sealed class Result {
        data class Success(val percentage: Int) : Result()
        data class Error(val e: Throwable) : Result()
    }

    suspend fun execute():
            Result = try {
        homeRepository.getDownloadPercentage(
        )?.let {
            Result.Success(it)
        }
    } catch (e: Exception){
        Result.Error(e)
    }

}