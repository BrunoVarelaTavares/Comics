package com.btavares.comics.app.sharedpreferences

import android.content.Context

internal class ComicPreferences(private val context: Context) {

    private val name = "comic_preferences"
    private val download = "downloaded"
    private val last = "last_comic_number"

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
        editor.putInt(last, number)
        editor.apply()
    }

    fun getLastComicNumber() : Int {
        val sharePreferences = this.context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharePreferences.getInt(last, 0)
    }


}