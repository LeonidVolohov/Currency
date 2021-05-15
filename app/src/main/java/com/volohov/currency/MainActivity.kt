package com.volohov.currency

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.volohov.currency.api.ApiConstants
import com.volohov.currency.api.currencylayer.CurrencyLayerUtils
import com.volohov.currency.ui.UiConstants
import com.volohov.currency.ui.chart.StockLineChart
import com.volohov.currency.ui.currency.CurrencyUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var defaultCurrencyInd = UiConstants.DEFAULT_CURRENCY_ID

    private var compositeDisposable = CompositeDisposable()
    private var disposable: Disposable? = null
    private lateinit var baseRateSpinnerString: String
    private lateinit var targetRateSpinnerString: String
    private var isNumeric: Boolean = false
    private var isPrediction: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ratesNameArray = resources.getStringArray(R.array.rates)

        val currencyUtils = CurrencyUtils(disposable = compositeDisposable)
        val currencyLineChart = StockLineChart(currency_chart)

        base_rate_spinner.setSelection(defaultCurrencyInd)
        base_rate_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                baseRateSpinnerString = ratesNameArray[position].split(",")[0]
                rate_number.setText(UiConstants.DEFAULT_EDIT_TEXT_NUMBER.toString())
                currencyLineChart.clearChart()
                rate_result.text = ""
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
                rate_number.setText(UiConstants.DEFAULT_EDIT_TEXT_NUMBER.toString())
                currencyLineChart.clearChart()
                rate_result.text = ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        currency_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            isPrediction = currency_switch.isChecked
        }

        currency_button_group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (currency_button_group.checkedButtonId) {
                    R.id.currency_togglebutton_one_week_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                                startDate = "1W",
                                targetRate = targetRateSpinnerString,
                                baseRate = baseRateSpinnerString,
                                stockLineChart = currencyLineChart,
                                context = this
                        )
                    }
                    R.id.currency_togglebutton_one_month_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                                startDate = "1M",
                                targetRate = targetRateSpinnerString,
                                baseRate = baseRateSpinnerString,
                                stockLineChart = currencyLineChart,
                                context = this
                        )
                    }
                    R.id.currency_togglebutton_six_months_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                                startDate = "6M",
                                targetRate = targetRateSpinnerString,
                                baseRate = baseRateSpinnerString,
                                stockLineChart = currencyLineChart,
                                context = this
                        )
                    }
                    R.id.currency_togglebutton_one_year_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                                startDate = "1Y",
                                targetRate = targetRateSpinnerString,
                                baseRate = baseRateSpinnerString,
                                stockLineChart = currencyLineChart,
                                context = this
                        )
                    }
                    R.id.currency_togglebutton_five_years_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                                startDate = "5Y",
                                targetRate = targetRateSpinnerString,
                                baseRate = baseRateSpinnerString,
                                stockLineChart = currencyLineChart,
                                context = this
                        )
                    }
                }
            }
        }

        calculate_button.setOnClickListener {
            if (baseRateSpinnerString == targetRateSpinnerString) {
                rate_result.text = currencyUtils.stringMultiplication(
                        rate_number.text.toString(),
                        UiConstants.DEFAULT_EDIT_TEXT_NUMBER.toString()
                )
            } else {
                isNumeric = try {
                    rate_number.text.toString().toDouble()
                    true
                } catch (exception: NumberFormatException) {
                    false
                }

                if (isNumeric) {
                    if (rate_number.text.toString() == (1.0).toString()) {
                        disposable = CurrencyLayerUtils().getTargetRatePrice(
                            baseRate = baseRateSpinnerString,
                            targetRate = targetRateSpinnerString
                        )
                            .subscribe(
                                { response ->
                                    val date = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss").format(Date(Timestamp(response.timestamp).time))
                                    last_date_update.text = this.getString(
                                        R.string.last_updated_date,
                                        date.toString()
                                    )

                                    rate_result.text = response.quotes[baseRateSpinnerString + targetRateSpinnerString].toString()
                                },
                                { failure ->
                                    Toast.makeText(this, failure.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                    } else {
                        disposable = CurrencyLayerUtils().getConvertRatePrice(
                            baseRate = baseRateSpinnerString,
                            targetRate = targetRateSpinnerString,
                            amount =  rate_number.text.toString()
                        )
                            .subscribe(
                                { response ->
                                    val date = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss").format(Date(Timestamp(response.info.timestamp).time))
                                    last_date_update.text = this.getString(
                                        R.string.last_updated_date,
                                        date.toString()
                                    )

                                    rate_result.text = response.result.toString()
                                },
                                { failure ->
                                    Toast.makeText(this, failure.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )

                    }

                    currencyUtils.plotRatesPerPeriod(
                            startDate = "1W",
                            targetRate = targetRateSpinnerString,
                            baseRate = baseRateSpinnerString,
                            stockLineChart = currencyLineChart,
                            context = this
                    )
                } else {
                    Toast.makeText(this, getString(R.string.toast_wrong_input), Toast.LENGTH_LONG).show()
                }
            }
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
