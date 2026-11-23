package com.example.cryptoexchange.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.cryptoexchange.data.worker.CryptoSyncScheduler
import com.example.cryptoexchange.data.worker.CryptoSyncWorker
import com.example.cryptoexchange.domain.usecase.ObserveCryptocurrenciesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CryptoViewModel(
    observeCryptocurrenciesUseCase: ObserveCryptocurrenciesUseCase,
    private val syncScheduler: CryptoSyncScheduler,
    private val workManager: WorkManager
) : ViewModel() {

    private val syncMetaState = MutableStateFlow(SyncMeta())

    val uiState: StateFlow<CryptoUiState> = combine(
        observeCryptocurrenciesUseCase(),
        workManager.getWorkInfosForUniqueWorkFlow(CryptoSyncScheduler.MANUAL_SYNC_WORK_NAME),
        syncMetaState
    ) { currencies, workInfos, meta ->
        val latestWork = workInfos.firstOrNull()
        val workState = latestWork?.state?.toSyncUiState() ?: SyncUiState.IDLE
        val isActive = latestWork?.state == WorkInfo.State.ENQUEUED ||
                latestWork?.state == WorkInfo.State.RUNNING

        val workerOutputMessage = latestWork
            ?.outputData
            ?.getString(CryptoSyncWorker.OUTPUT_MESSAGE)
            ?.takeIf { it.isNotBlank() }

        val resolvedError = when (workState) {
            SyncUiState.FAILED -> workerOutputMessage ?: meta.manualError ?: "Помилка синхронізації"
            SyncUiState.RUNNING, SyncUiState.ENQUEUED -> null
            else -> meta.manualError
        }

        val maxUpdatedInDb = currencies.maxOfOrNull { it.updatedAt }
        val resolvedLastUpdated = maxUpdatedInDb ?: meta.lastSuccessfulSyncMillis

        val resolvedLoading = isActive || meta.isStartingManualSync

        CryptoUiState(
            currencies = currencies,
            isLoading = resolvedLoading,
            syncState = workState,
            lastUpdatedAtMillis = resolvedLastUpdated,
            errorMessage = resolvedError,
            isManualSyncActive = isActive
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CryptoUiState()
    )

    init {
        viewModelScope.launch {
            workManager
                .getWorkInfosForUniqueWorkFlow(CryptoSyncScheduler.MANUAL_SYNC_WORK_NAME)
                .collect { workInfos ->
                    val latest = workInfos.firstOrNull() ?: return@collect
                    when (latest.state) {
                        WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> {
                            syncMetaState.update {
                                it.copy(
                                    isStartingManualSync = false,
                                    manualError = null
                                )
                            }
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            syncMetaState.update {
                                it.copy(
                                    isStartingManualSync = false,
                                    manualError = null,
                                    lastSuccessfulSyncMillis = System.currentTimeMillis()
                                )
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            val msg = latest.outputData
                                .getString(CryptoSyncWorker.OUTPUT_MESSAGE)
                                ?.takeIf { it.isNotBlank() }
                                ?: "Не вдалося оновити дані"
                            syncMetaState.update {
                                it.copy(
                                    isStartingManualSync = false,
                                    manualError = msg
                                )
                            }
                        }
                        WorkInfo.State.CANCELLED -> {
                            syncMetaState.update {
                                it.copy(isStartingManualSync = false)
                            }
                        }
                        WorkInfo.State.BLOCKED -> Unit
                    }
                }
        }
    }

    fun onRefreshClicked() {
        if (uiState.value.isManualSyncActive || uiState.value.isLoading) return

        syncMetaState.update {
            it.copy(
                isStartingManualSync = true,
                manualError = null
            )
        }
        syncScheduler.scheduleManualSync()
    }

    private data class SyncMeta(
        val isStartingManualSync: Boolean = false,
        val manualError: String? = null,
        val lastSuccessfulSyncMillis: Long? = null
    )

    class Factory(
        private val observeCryptocurrenciesUseCase: ObserveCryptocurrenciesUseCase,
        private val syncScheduler: CryptoSyncScheduler,
        private val workManager: WorkManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
                "Unknown ViewModel class: ${modelClass.name}"
            }
            return CryptoViewModel(
                observeCryptocurrenciesUseCase = observeCryptocurrenciesUseCase,
                syncScheduler = syncScheduler,
                workManager = workManager
            ) as T
        }
    }
}