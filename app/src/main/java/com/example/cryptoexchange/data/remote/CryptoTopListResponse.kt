package com.example.cryptoexchange.data.remote

import com.google.gson.annotations.SerializedName

data class CryptoTopListResponse(
    @SerializedName("data")
    val data: List<CryptoAssetDto>?
)

data class CryptoAssetDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("symbol")
    val symbol: String?,

    @SerializedName("cmc_rank")
    val rank: Int?,

    @SerializedName("quote")
    val quote: List<CryptoQuoteDto>?
)

data class CryptoQuoteDto(
    @SerializedName("symbol")
    val symbol: String?,

    @SerializedName("price")
    val price: Double?
)