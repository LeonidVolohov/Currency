package com.volohov.currency.api

import com.volohov.currency.api.ApiConstants.Companion.CURRENCY_API_ALPHAVANTAGE_BASE_URL
import com.volohov.currency.api.currencyalphavantage.CurrencyAlphaVantageRequests
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val interceptor: HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BODY }

val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

val currencyAlphaVantageApi: CurrencyAlphaVantageRequests = Retrofit.Builder()
    .baseUrl(CURRENCY_API_ALPHAVANTAGE_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .client(client)
    .build()
    .create(CurrencyAlphaVantageRequests::class.java)
