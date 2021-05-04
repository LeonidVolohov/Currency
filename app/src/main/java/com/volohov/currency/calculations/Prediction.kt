package com.volohov.currency.calculations

import org.nield.kotlinstatistics.SimpleRegression
import org.nield.kotlinstatistics.simpleRegression

class Prediction {
    data class AmountPerDate(val date: Long, val amount: Double)

    private fun prediction(date_array: Array<Long>, amount_array: Array<Double>): SimpleRegression {
        val amountDates = mutableListOf<AmountPerDate>()
        for (i: Int in date_array.indices) {
            amountDates.add(AmountPerDate(date_array[i], amount_array[i]))
        }

        return amountDates.simpleRegression(
                xSelector = { it.date },
                ySelector = { it.amount }
        )
    }

    fun datePrediction(date_array: Array<Long>, amount_array: Array<Double>, days: Int): Double {
        return prediction(date_array, amount_array).predict(date_array[date_array.size - 1].toDouble() + days)
    }
}
