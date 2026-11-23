package com.example.cryptoexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import com.example.cryptoexchange.presentation.CryptoExchangeRoute
import com.example.cryptoexchange.presentation.CryptoViewModel
import com.example.cryptoexchange.ui.theme.CryptoExchangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CryptoExchangeApplication
        val container = app.container
        val workManager = WorkManager.getInstance(applicationContext)

        setContent {
            CryptoExchangeTheme {
                val viewModel: CryptoViewModel = viewModel(
                    factory = CryptoViewModel.Factory(
                        observeCryptocurrenciesUseCase = container.observeCryptocurrenciesUseCase,
                        syncScheduler = container.cryptoSyncScheduler,
                        workManager = workManager
                    )
                )

                CryptoExchangeRoute(viewModel = viewModel)
            }
        }
    }
}