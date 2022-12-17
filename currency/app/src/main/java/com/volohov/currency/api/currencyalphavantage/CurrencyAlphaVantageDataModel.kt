package com.volohov.currency.api.currencyalphavantage

import com.google.gson.annotations.SerializedName
import java.util.*

object CurrencyAlphaVantageDataModel {
    data class CurrencyData(
        @SerializedName("Time Series FX (Daily)") val data: SortedMap<String, HashMap<String, String>>
    )

    data class ExchangeRateInformation(
        @SerializedName("Realtime Currency Exchange Rate") val data: HashMap<String, String>
    )
}
