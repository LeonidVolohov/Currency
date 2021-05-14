package com.volohov.currency.api.currencylayer

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerRequests {

    @GET("live")
    fun getTargetRatePrice(
        @Query("source") baseRate: String,
        @Query("currencies") targetRate: String,
        @Query("access_key") apiKey: String,
        @Query("format") format: String = "1"
    ): Observable<CurrencyLayerDataModel.CurrencyTargetRateData>

    @GET("convert")
    fun getConvertRatePrice (
        @Query("from") baseRate: String,
        @Query("to") targetRate: String,
        @Query("amount") amount: String,
        @Query("access_key") apiKey: String,
        @Query("format") format: String = "1"
    ): Observable<CurrencyLayerDataModel.CurrencyConvert>
}
