package com.volohov.currency.ui.predictionfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.volohov.currency.R
import com.volohov.currency.api.currencyalphavantage.CurrencyAlphaVantageUtils
import com.volohov.currency.calculations.Prediction
import com.volohov.currency.ui.UiConstants
import com.volohov.currency.ui.chart.StockLineChart
import com.volohov.currency.ui.currencyfragment.CurrencyUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_prediction.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PredictionFragment(private val defaultCurrencyId: Int) : Fragment() {
    private var compositeDisposable = CompositeDisposable()
    private var disposable: Disposable? = null

    private lateinit var baseRateSpinnerString: String
    private lateinit var targetRateSpinnerString: String

    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_prediction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratesNameArray = resources.getStringArray(R.array.rates)

        val currencyUtils = CurrencyUtils(disposable = compositeDisposable)
        val currencyLineChart = StockLineChart(currency_chart)

        base_rate_spinner.setSelection(defaultCurrencyId)
        base_rate_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseRateSpinnerString = ratesNameArray[position].split(",")[0]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        target_rate_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                targetRateSpinnerString = ratesNameArray[position].split(",")[0]

                if(count > 1) {
                    disposable = CurrencyAlphaVantageUtils().getDataForPeriod(
                        fromCurrencyName = baseRateSpinnerString,
                        toCurrencyName = targetRateSpinnerString
                    )
                        .subscribe(
                            { response ->
                                currencyUtils.plotPredictionRatesPerMonth(
                                    data = response.data,
                                    baseRate = baseRateSpinnerString,
                                    targetRate = targetRateSpinnerString,
                                    stockLineChart = currencyLineChart
                                )

                                val startLocalDate = LocalDate.now().minusMonths(1).format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                )
                                val endLocalDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

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

                                val dateListLong: ArrayList<Long> = arrayListOf()
                                if (processedDateList != null) {
                                    for (item in processedDateList) {
                                        dateListLong.add(
                                            (SimpleDateFormat("yyyy-MM-dd").parse(item).toInstant()
                                                .toEpochMilli())
                                        )
                                    }
                                }

                                val predictionRate = Prediction().datePrediction(
                                    dateListLong.toTypedArray(),
                                    rateList.toTypedArray(),
                                    1
                                )

                                textview_today_rate.text = currencyUtils.stringMultiplication(
                                    rateList[rateList.size - 1].toString(),
                                    UiConstants.DEFAULT_EDIT_TEXT_NUMBER.toString()
                                )

                                textview_tomorrow_rate.text = currencyUtils.stringMultiplication(
                                    predictionRate.toString(),
                                    UiConstants.DEFAULT_EDIT_TEXT_NUMBER.toString()
                                )
                            },
                            { failure ->
                                Toast.makeText(context, failure.message, Toast.LENGTH_SHORT).show()
                            }
                        )
                }

                count += 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
