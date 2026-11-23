package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.repository.CryptoRepository

class SyncCryptocurrenciesUseCase(
    private val repository: CryptoRepository
) {

    suspend operator fun invoke() {
        repository.syncCryptocurrencies()
    }
}