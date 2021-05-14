package com.volohov.currency.api.currencylayer

import com.google.gson.annotations.SerializedName

object CurrencyLayerDataModel {
    data class CurrencyTargetRateData(
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("quotes") val quotes: HashMap<String, Double>
    )

    data class CurrencyConvert(
        @SerializedName("info") val info: CurrencyInfo,
        @SerializedName("result") val result: Double
    )

    data class CurrencyInfo(
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("quote") val quote: Double
    )
}
