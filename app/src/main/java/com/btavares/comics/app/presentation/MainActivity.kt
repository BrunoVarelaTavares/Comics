package com.btavares.comics.app.presentation

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.btavares.comics.R
import com.btavares.comics.app.presentation.base.BaseActivity
import com.btavares.comics.app.presentation.navigation.NavManager
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.generic.instance

class MainActivity : BaseActivity(R.layout.activity_main) {

    private val navigationController get() = navigationFragment.findNavController()

    private  val navigationManager: NavManager by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadNavigation()
    }


    private fun loadNavigation(){
        navigationManager.setOnNavEvent {
            navigationController.navigate(it)
        }

    }

}