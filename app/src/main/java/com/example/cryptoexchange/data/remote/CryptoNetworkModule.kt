package com.example.cryptoexchange.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CryptoNetworkModule {

    private const val BASE_URL =
        "https://pro-api.coinmarketcap.com/public-api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: CryptoApi by lazy {
        retrofit.create(CryptoApi::class.java)
    }
}