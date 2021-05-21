package com.btavares.comics.app.presentation.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.btavares.comics.BuildConfig
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.kodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.kcontext

abstract class InjectionActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    KodeinAware {

    private val parentKodein by kodein()


    override val kodeinContext = kcontext<AppCompatActivity>(this)

    final override val kodein: Kodein by retainedKodein {
        extend(parentKodein)
    }


    final override val kodeinTrigger: KodeinTrigger? by lazy {
        if (BuildConfig.DEBUG) KodeinTrigger() else super.kodeinTrigger
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        kodeinTrigger?.trigger()
    }

}