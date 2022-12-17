package com.volohov.currency.api.currencyalphavantage

import com.volohov.currency.api.ApiConstants.Companion.CURRENCY_API_KEY_DATE
import com.volohov.currency.api.ApiConstants.Companion.CURRENCY_API_KEY_PERIOD_DATA
import com.volohov.currency.api.ApiConstants.Companion.CURRENCY_API_KEY_TARGETRATE_PRICE
import com.volohov.currency.api.currencyAlphaVantageApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CurrencyAlphaVantageUtils {

    fun getDataForPeriod(
        fromCurrencyName: String,
        toCurrencyName: String
    ): Observable<CurrencyAlphaVantageDataModel.CurrencyData> {
        return currencyAlphaVantageApi.getDataForPeriod(
            function = "FX_DAILY",
            fromCurrencyName = fromCurrencyName,
            toCurrencyName = toCurrencyName,
            outputSize = "full",
            apiKey = CURRENCY_API_KEY_PERIOD_DATA
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getLastRefreshedDate(): Observable<CurrencyAlphaVantageDataModel.ExchangeRateInformation> {
        return currencyAlphaVantageApi.getExchangeRateInformation(
            fromCurrencyName = "USD",
            toCurrencyName = "RUB",
            apiKey = CURRENCY_API_KEY_DATE
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTargetRatePrice(
        fromCurrencyName: String,
        toCurrencyName: String
    ): Observable<CurrencyAlphaVantageDataModel.ExchangeRateInformation> {
        return currencyAlphaVantageApi.getExchangeRateInformation(
            fromCurrencyName = fromCurrencyName,
            toCurrencyName = toCurrencyName,
            apiKey = CURRENCY_API_KEY_TARGETRATE_PRICE
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
