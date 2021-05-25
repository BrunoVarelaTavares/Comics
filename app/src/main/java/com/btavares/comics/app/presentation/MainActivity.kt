package com.btavares.comics.app.presentation

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.btavares.comics.R
import com.btavares.comics.app.helpers.Notifications
import com.btavares.comics.app.presentation.base.BaseActivity
import com.btavares.comics.app.presentation.navigation.NavManager
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(R.layout.activity_main) {

    private val navigationController get() = navigationFragment.findNavController()

    private  val navigationManager: NavManager by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadNavigation()
        setUpNotification()
    }

    private fun loadNavigation(){
        navigationManager.setOnNavEvent {
            navigationController.navigate(it)
        }

    }


    private fun setUpNotification() {

        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshCpnWork = PeriodicWorkRequest.Builder(Notifications::class.java,  1, TimeUnit.HOURS)
            .setConstraints(myConstraints)
            .addTag(Notifications.NOTIFICATION_WORK)
            .build()


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            Notifications.NOTIFICATION_WORK,
            ExistingPeriodicWorkPolicy.REPLACE, refreshCpnWork)
    }

}