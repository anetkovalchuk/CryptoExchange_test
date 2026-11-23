package com.example.cryptoexchange.data.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class CryptoSyncScheduler(
    context: Context
) {

    companion object {
        const val MANUAL_SYNC_WORK_NAME =
            "crypto_manual_sync"

        const val PERIODIC_SYNC_WORK_NAME =
            "crypto_periodic_sync"
    }

    private val workManager =
        WorkManager.getInstance(
            context.applicationContext
        )

    private val networkConstraints =
        Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()

    fun scheduleManualSync() {

        val request =
            OneTimeWorkRequestBuilder<CryptoSyncWorker>()
                .setConstraints(
                    networkConstraints
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

        workManager.enqueueUniqueWork(
            MANUAL_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun schedulePeriodicSync() {

        val request =
            PeriodicWorkRequestBuilder<CryptoSyncWorker>(
                1,
                TimeUnit.HOURS
            )
                .setConstraints(
                    networkConstraints
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}