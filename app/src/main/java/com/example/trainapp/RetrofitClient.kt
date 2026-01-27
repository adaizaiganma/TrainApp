package com.example.trainapp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
        // 1. For Authentication (Identity Server)
        private val authRetrofit = Retrofit.Builder()
            .baseUrl("https://tdx.transportdata.tw/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 2. For Data (API Server)
        private val dataRetrofit = Retrofit.Builder()
            .baseUrl("https://tdx.transportdata.tw/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val authService: TdxApiService = authRetrofit.create(TdxApiService::class.java)
    val dataService: TdxApiService = dataRetrofit.create(TdxApiService::class.java)
}