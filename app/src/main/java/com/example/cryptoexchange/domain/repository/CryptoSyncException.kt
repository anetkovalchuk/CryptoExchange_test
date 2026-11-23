package com.example.cryptoexchange.domain.repository

sealed class CryptoSyncException(
    message: String
) : Exception(message) {

    class RateLimit :
        CryptoSyncException(
            "Перевищено ліміт запитів API"
        )

    class Temporary(
        message: String = "Тимчасова помилка мережі або сервера"
    ) : CryptoSyncException(message)

    class InvalidResponse(
        message: String = "API повернув некоректні дані"
    ) : CryptoSyncException(message)
}