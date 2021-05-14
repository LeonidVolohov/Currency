package com.volohov.currency.api.currencylayer

import com.volohov.currency.api.ApiConstants.Companion.CURRENCYLAYER_API_KEY
import com.volohov.currency.api.currencyLayerApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CurrencyLayerUtils {

    fun getTargetRatePrice(
        baseRate: String,
        targetRate: String
    ): Observable<CurrencyLayerDataModel.CurrencyTargetRateData> {
        return currencyLayerApi.getTargetRatePrice(
            baseRate = baseRate,
            targetRate = targetRate,
            apiKey = CURRENCYLAYER_API_KEY
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getConvertRatePrice (
        baseRate: String,
        targetRate: String,
        amount: String
    ): Observable<CurrencyLayerDataModel.CurrencyConvert> {
        return currencyLayerApi.getConvertRatePrice(
            baseRate = baseRate,
            targetRate = targetRate,
            amount = amount,
            apiKey = CURRENCYLAYER_API_KEY
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
