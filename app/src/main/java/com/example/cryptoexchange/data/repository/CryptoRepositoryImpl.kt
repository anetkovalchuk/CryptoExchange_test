package com.example.cryptoexchange.data.repository

import com.example.cryptoexchange.data.local.CryptoDao
import com.example.cryptoexchange.data.remote.CryptoApi
import com.example.cryptoexchange.domain.model.CryptoCurrency
import com.example.cryptoexchange.domain.repository.CryptoRepository
import com.example.cryptoexchange.domain.repository.CryptoSyncException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class CryptoRepositoryImpl(
    private val api: CryptoApi,
    private val dao: CryptoDao
) : CryptoRepository {

    override fun observeCryptocurrencies(): Flow<List<CryptoCurrency>> =
        dao.observeCryptocurrencies().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }

    override suspend fun syncCryptocurrencies() {
        val response = try {
            api.getTopCryptocurrencies()
        } catch (e: IOException) {
            throw CryptoSyncException.Temporary(
                "Немає з'єднання з мережею"
            )
        }

        when (response.code()) {
            429 -> throw CryptoSyncException.RateLimit()
        }

        if (response.code() in 500..599) {
            throw CryptoSyncException.Temporary(
                "Тимчасова помилка сервера: ${response.code()}"
            )
        }

        if (!response.isSuccessful) {
            throw CryptoSyncException.InvalidResponse(
                "Помилка API: ${response.code()}"
            )
        }

        val dtoList = response.body()?.data
            ?: throw CryptoSyncException.InvalidResponse(
                "API не повернув список криптовалют"
            )

        if (dtoList.size < 30) {
            throw CryptoSyncException.InvalidResponse(
                "API повернув менше 30 криптовалют"
            )
        }

        val updatedAt = System.currentTimeMillis()

        val entities = dtoList
            .take(30)
            .map { dto ->
                dto.toEntity(updatedAt)
                    ?: throw CryptoSyncException.InvalidResponse(
                        "У відповіді відсутні обов'язкові дані"
                    )
            }

        val uniqueIds = entities
            .map { it.id }
            .toSet()

        if (uniqueIds.size != 30) {
            throw CryptoSyncException.InvalidResponse(
                "API повернув дублікати криптовалют"
            )
        }

        dao.replaceAll(entities)
    }
}