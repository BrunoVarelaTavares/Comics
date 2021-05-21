package com.btavares.comics.app.presentation.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.SystemClock
import android.view.View
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

fun View.setOnDebouncedClickListener(action: () -> Unit){
    val actionDebouncer = ActionDebouncer(action)

    setOnClickListener{
        actionDebouncer.notifyAction()
    }
}


private class ActionDebouncer(private val action: () -> Unit) {

    companion object{
        const val DEBOUNCE_INTERVAL_MILLISECONDS = 600L
    }

    private var lastActionTime = 0L

    fun notifyAction(){
        val now = SystemClock.elapsedRealtime()

        val millisecondsPassed = now - lastActionTime
        val actionAllowed = millisecondsPassed > DEBOUNCE_INTERVAL_MILLISECONDS
        lastActionTime = now

        if (actionAllowed){
            action.invoke()
        }
    }

}


suspend fun getImageBitmap(context: Context, imageUrl : String): Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()

    val result = (loading.execute(request) as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}