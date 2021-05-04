package com.volohov.currency.api.currencyalphavantage

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyAlphaVantageRequests {

    @GET("query")
    fun getDataForPeriod(
        @Query("function") function: String,
        @Query("from_symbol") fromCurrencyName: String,
        @Query("to_symbol") toCurrencyName: String,
        @Query("outputsize") outputSize: String,
        @Query("apikey") apiKey: String
    ): Observable<CurrencyAlphaVantageDataModel.CurrencyData>

    @GET("query")
    fun getExchangeRateInformation(
        @Query("function") function: String = "CURRENCY_EXCHANGE_RATE",
        @Query("from_currency") fromCurrencyName: String,
        @Query("to_currency") toCurrencyName: String,
        @Query("apikey") apiKey: String
    ): Observable<CurrencyAlphaVantageDataModel.ExchangeRateInformation>
}
