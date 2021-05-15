package com.volohov.currency.api

import com.volohov.currency.BuildConfig


class ApiConstants {
    companion object {
        const val CURRENCY_API_ALPHAVANTAGE_BASE_URL = "https://www.alphavantage.co/"
        const val CURRENCY_API_KEY_DATE = BuildConfig.CURRENCY_API_KEY_DATE
        const val CURRENCY_API_KEY_TARGETRATE_PRICE = BuildConfig.CURRENCY_API_KEY_TARGETRATE_PRICE
        const val CURRENCY_API_KEY_PERIOD_DATA = BuildConfig.CURRENCY_API_KEY_PERIOD_DATA

        const val CURRENCYLAYER_BASE_URL = "https://api.currencylayer.com/"
        const val CURRENCYLAYER_API_KEY = BuildConfig.CURRENCYLAYER_API_KEY
    }
}
