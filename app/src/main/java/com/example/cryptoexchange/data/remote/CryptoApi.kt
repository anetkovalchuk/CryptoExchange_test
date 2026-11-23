package com.example.cryptoexchange.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoApi {

    @GET("v3/cryptocurrency/listings/latest")
    suspend fun getTopCryptocurrencies(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("convert") convert: String = "USD"
    ): Response<CryptoTopListResponse>
}