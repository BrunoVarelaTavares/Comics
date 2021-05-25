package com.btavares.comics.app.sharedpreferences

import android.content.Context

internal class ComicPreferences(private val context: Context) {

    val name = "comic_preferences"
    val download = "downloaded"
    val lastComicNumber = "last_comic_number"

    fun saveDownloadCompleted() {
        val sharePreferences = this.context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharePreferences.edit()
        editor.putBoolean(download, true)
        editor.apply()
    }

    fun isDownloadCompleted() : Boolean {
        val sharePreferences = this.context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharePreferences.getBoolean(download, false)
    }

    fun saveLastComicNumber(number : Int) {
        val sharePreferences = this.context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharePreferences.edit()
        editor.putInt(lastComicNumber, number)
        editor.apply()
    }

    fun getLastComicNumber() : Int {
        val sharePreferences = this.context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharePreferences.getInt(lastComicNumber, 0)
    }


}