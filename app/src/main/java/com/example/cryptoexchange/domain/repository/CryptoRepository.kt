package com.example.cryptoexchange.domain.repository

import com.example.cryptoexchange.domain.model.CryptoCurrency
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    fun observeCryptocurrencies(): Flow<List<CryptoCurrency>>

    suspend fun syncCryptocurrencies()
}