package com.example.cryptoexchange

import android.app.Application
import com.example.cryptoexchange.di.AppContainer

class CryptoExchangeApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = AppContainer(
            context = applicationContext
        )

        container
            .cryptoSyncScheduler
            .schedulePeriodicSync()
            }
}