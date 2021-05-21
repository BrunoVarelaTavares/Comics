package com.btavares.comics.app

import android.app.Application
import android.content.Context
import com.btavares.comics.BuildConfig
import com.btavares.comics.app.kodein.FragmentArgs
import com.btavares.comics.main.appModule
import com.btavares.comics.main.data.dataModule
import com.btavares.comics.main.domain.domainModule
import com.btavares.comics.main.presentation.presentationModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import timber.log.Timber

class ComicsApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@ComicsApplication))
        import(appModule)
        import(presentationModule)
        import(domainModule)
        import(dataModule)

        externalSources.add(FragmentArgs())
    }


    private lateinit var context: Context


    override fun onCreate() {
        super.onCreate()
        context = this
    }




}