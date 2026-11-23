package com.example.cryptoexchange.data.repository

import com.example.cryptoexchange.data.local.CryptoEntity
import com.example.cryptoexchange.data.remote.CryptoAssetDto
import com.example.cryptoexchange.domain.model.CryptoCurrency

fun CryptoAssetDto.toEntity(updatedAt: Long): CryptoEntity? {
    val validId = id ?: return null
    val validName = name?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val validSymbol = symbol?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val validRank = rank ?: return null

    val usdQuote = quote
        ?.firstOrNull { it.symbol == "USD" }

    val validPrice = usdQuote
        ?.price
        ?.takeIf { it.isFinite() && it >= 0.0 }

    val logoUrl =
        "https://s2.coinmarketcap.com/static/img/coins/64x64/$validId.png"

    return CryptoEntity(
        id = validId.toString(),
        name = validName,
        symbol = validSymbol,
        logoUrl = logoUrl,
        priceUsd = validPrice,
        rank = validRank,
        updatedAt = updatedAt
    )
}

fun CryptoEntity.toDomain(): CryptoCurrency =
    CryptoCurrency(
        id = id,
        name = name,
        symbol = symbol,
        logoUrl = logoUrl,
        priceUsd = priceUsd,
        rank = rank,
        updatedAt = updatedAt
    )