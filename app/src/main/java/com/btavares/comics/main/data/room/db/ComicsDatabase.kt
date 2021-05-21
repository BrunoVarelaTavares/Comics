package com.btavares.comics.main.data.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.btavares.comics.main.data.model.ComicDataModel
import com.btavares.comics.main.data.room.converts.Converters
import com.btavares.comics.main.data.room.dao.ComicsDao
import com.btavares.comics.main.data.room.entities.Image

@Database(entities = [ComicDataModel::class, Image::class],  version = 1)
@TypeConverters(Converters::class)
internal abstract class ComicsDatabase : RoomDatabase() {
    abstract fun comicsDao() : ComicsDao
    companion object {
        @Volatile
        private var instance : ComicsDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) : ComicsDatabase {
            return Room.databaseBuilder(context.applicationContext, ComicsDatabase::class.java, "comics")
                .build()
        }
    }
}