package com.example.cryptoexchange.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.cryptoexchange.CryptoExchangeApplication
import com.example.cryptoexchange.domain.repository.CryptoSyncException
import kotlinx.coroutines.CancellationException

class CryptoSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val OUTPUT_MESSAGE = "output_message"
        private const val TAG = "CryptoSyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Worker started")

            val application =
                applicationContext as? CryptoExchangeApplication
                    ?: return failure("Неправильний Application class")

            val syncUseCase =
                application.container.syncCryptocurrenciesUseCase

            syncUseCase()

            Log.d(TAG, "Synchronization successful")

            Result.success(
                Data.Builder()
                    .putString(
                        OUTPUT_MESSAGE,
                        "Криптовалюти успішно оновлено"
                    )
                    .build()
            )

        } catch (e: CancellationException) {
            Log.d(TAG, "Worker cancelled")
            throw e

        } catch (e: CryptoSyncException.Temporary) {
            Log.e(TAG, "Temporary error", e)

            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                failure(
                    e.message
                        ?: "Тимчасова помилка синхронізації"
                )
            }

        } catch (e: CryptoSyncException.RateLimit) {
            Log.e(TAG, "Rate limit error", e)

            failure(
                e.message
                    ?: "Перевищено ліміт API"
            )

        } catch (e: CryptoSyncException.InvalidResponse) {
            Log.e(TAG, "Invalid API response", e)

            failure(
                e.message
                    ?: "Некоректна відповідь API"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)

            failure(
                e.message
                    ?: e.javaClass.simpleName
            )
        }
    }

    private fun failure(message: String): Result {
        Log.e(TAG, "Worker failed: $message")

        return Result.failure(
            Data.Builder()
                .putString(
                    OUTPUT_MESSAGE,
                    message
                )
                .build()
        )
    }
}