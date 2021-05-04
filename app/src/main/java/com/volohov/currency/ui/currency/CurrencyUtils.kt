package com.volohov.currency.ui.currency

import android.content.Context
import android.widget.Toast
import com.volohov.currency.api.currencyalphavantage.CurrencyAlphaVantageUtils
import com.volohov.currency.ui.UiConstants.Companion.DEFAULT_DECIMAL_POINT_PRECISION
import com.volohov.currency.ui.chart.StockLineChart
import io.reactivex.disposables.Disposable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrencyUtils(disposable: Disposable?) {
    private var localDisposable = disposable

    /**
     * Умножает две строки [firstNumber]  и [secondNumber] и возвращает только первые 7 символов
     */
    fun stringMultiplication(
        firstNumber: String,
        secondNumber: String
    ): String {
        return BigDecimal((firstNumber.toDouble() * secondNumber.toDouble())).setScale(
            DEFAULT_DECIMAL_POINT_PRECISION,
            BigDecimal.ROUND_HALF_EVEN
        ).toString()
    }

    /**
     * Возвращает  количество знаков после запятой равное [DEFAULT_DECIMAL_POINT_PRECISION]
     */
    fun doubleScale(number: Double, scaleNumber: Int = DEFAULT_DECIMAL_POINT_PRECISION): String {
        return BigDecimal(number).setScale(
            scaleNumber,
            BigDecimal.ROUND_HALF_EVEN
        ).toString()
    }

    /**
     * Рисует график за какой-то промежуток.
     * Период графика составляет с [startDate] по [endDate] и рисует стоимость [baseRate] относительно [targetRate]
     */
    fun plotRatesPerPeriod(
        startDate: String,
        endDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        baseRate: String,
        targetRate: String,
        stockLineChart: StockLineChart,
        context: Context
    ) {
        var startLocalDate = ""
        var endLocalDate = endDate
        when (startDate) {
            "1W" ->
                startLocalDate = LocalDate.now().minusWeeks(1).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
            "1M" ->
                startLocalDate = LocalDate.now().minusMonths(1).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
            "6M" ->
                startLocalDate = LocalDate.now().minusMonths(6).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
            "1Y" ->
                startLocalDate = LocalDate.now().minusYears(1).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
            "5Y" ->
                startLocalDate = LocalDate.now().minusYears(5).format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
            else -> {
                startLocalDate = startDate
                endLocalDate = endDate
            }
        }

        localDisposable = CurrencyAlphaVantageUtils().getDataForPeriod(
            fromCurrencyName = baseRate,
            toCurrencyName = targetRate
        )
            .subscribe(
                { response ->
                    val rateList: MutableList<Double> = arrayListOf()
                    val dateList: MutableList<String>? =
                        response.data.keys.toMutableList()
                    val processedDateList: MutableList<String>? = mutableListOf()

                    if (dateList != null) {
                        for (date in dateList) {
                            if ((LocalDate.parse(date)
                                    .isEqual(LocalDate.parse(startLocalDate)) || LocalDate.parse(
                                    date
                                ).isAfter(LocalDate.parse(startLocalDate))) &&
                                LocalDate.parse(date)
                                    .isBefore(LocalDate.parse(endLocalDate)) || LocalDate.parse(date)
                                    .isEqual(LocalDate.parse(startLocalDate))
                            ) {
                                response.data[date]?.get("4. close")?.toDouble()
                                    ?.let { rateList.add(it) }
                                processedDateList?.add(date)
                            }
                        }
                    }

                    val entries = rateList.toList()

                    stockLineChart.setXAxis(
                        dateList = processedDateList?.toList(),
                    )

                    val lineChartData = stockLineChart.getLineData(
                        entries = entries,
                        baseCurrency = baseRate,
                        targetCurrency = targetRate
                    )

                    stockLineChart.displayChart(
                        lineChartData = lineChartData
                    )
                },
                { failure ->
                    Toast.makeText(context, failure.message, Toast.LENGTH_SHORT).show()
                }
            )
    }
}
