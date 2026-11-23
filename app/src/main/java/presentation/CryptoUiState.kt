package com.example.cryptoexchange.presentation

import androidx.work.WorkInfo
import com.example.cryptoexchange.domain.model.CryptoCurrency

enum class SyncUiState {
    IDLE,
    ENQUEUED,
    RUNNING,
    SUCCEEDED,
    FAILED
}

data class CryptoUiState(
    val currencies: List<CryptoCurrency> = emptyList(),
    val isLoading: Boolean = false,
    val syncState: SyncUiState = SyncUiState.IDLE,
    val lastUpdatedAtMillis: Long? = null,
    val errorMessage: String? = null,
    val isManualSyncActive: Boolean = false
)

internal fun WorkInfo.State.toSyncUiState(): SyncUiState = when (this) {
    WorkInfo.State.ENQUEUED -> SyncUiState.ENQUEUED
    WorkInfo.State.RUNNING -> SyncUiState.RUNNING
    WorkInfo.State.SUCCEEDED -> SyncUiState.SUCCEEDED
    WorkInfo.State.FAILED -> SyncUiState.FAILED
    WorkInfo.State.BLOCKED -> SyncUiState.ENQUEUED
    WorkInfo.State.CANCELLED -> SyncUiState.IDLE
}