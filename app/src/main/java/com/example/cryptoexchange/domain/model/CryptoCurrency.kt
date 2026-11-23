package com.example.cryptoexchange.domain.model

data class CryptoCurrency(
    val id: String,
    val name: String,
    val symbol: String,
    val logoUrl: String?,
    val priceUsd: Double?,
    val rank: Int,
    val updatedAt: Long
)