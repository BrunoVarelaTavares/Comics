package com.btavares.comics.main.data.room.dao


import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.btavares.comics.main.data.model.ComicDataModel
import com.btavares.comics.main.data.room.entities.Image

@Dao
internal interface ComicsDao {

    @Query("SELECT * FROM comics ORDER BY number DESC")
    suspend fun getAllComics(): List<ComicDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg comics: ComicDataModel)

    @Query("SELECT * FROM comics WHERE number LIKE :search OR title LIKE :search OR description LIKE :search OR alt LIKE :search ")
    suspend fun searchComics(search : String): List<ComicDataModel>

    @Query("SELECT * FROM comics WHERE isFavorite = 1 ORDER BY number DESC")
    suspend fun getAllFavorites(): List<ComicDataModel>?

    @Query("UPDATE comics SET isFavorite = 1  WHERE number = :number")
    suspend fun setFavorite(number: Int)

    @Query("UPDATE comics SET isFavorite = 0 WHERE number = :number")
    suspend fun removeFavorite(number: Int)

    @Query("DELETE FROM image WHERE comicNumber = :number ")
    suspend fun removeImage(number: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveImage(vararg comics: Image)

    @Query("SELECT comicBitmap FROM image WHERE comicNumber = :number")
    suspend fun getImage(number : Int): Bitmap

    @Query("SELECT * FROM comics where number == :number")
    suspend fun getComic(number: Int): ComicDataModel?



}