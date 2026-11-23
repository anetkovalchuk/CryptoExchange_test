package com.example.cryptoexchange.di

import android.content.Context
import com.example.cryptoexchange.data.local.CryptoDatabase
import com.example.cryptoexchange.data.remote.CryptoNetworkModule
import com.example.cryptoexchange.data.repository.CryptoRepositoryImpl
import com.example.cryptoexchange.data.worker.CryptoSyncScheduler
import com.example.cryptoexchange.domain.repository.CryptoRepository
import com.example.cryptoexchange.domain.usecase.ObserveCryptocurrenciesUseCase
import com.example.cryptoexchange.domain.usecase.SyncCryptocurrenciesUseCase

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database =
        CryptoDatabase.getDatabase(appContext)

    private val cryptoDao =
        database.cryptoDao()

    private val cryptoApi =
        CryptoNetworkModule.api

    val cryptoRepository: CryptoRepository =
        CryptoRepositoryImpl(
            api = cryptoApi,
            dao = cryptoDao
        )

    val syncCryptocurrenciesUseCase =
        SyncCryptocurrenciesUseCase(
            repository = cryptoRepository
        )

    val observeCryptocurrenciesUseCase =
        ObserveCryptocurrenciesUseCase(
            repository = cryptoRepository
        )

    val cryptoSyncScheduler =
        CryptoSyncScheduler(
            context = appContext
        )
}