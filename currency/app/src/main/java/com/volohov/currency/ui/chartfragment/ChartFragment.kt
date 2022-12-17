package com.volohov.currency.ui.chartfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.volohov.currency.R
import com.volohov.currency.ui.CurrencyUtils
import com.volohov.currency.ui.chart.StockLineChart
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_chart.*
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChartFragment(private val defaultCurrencyId: Int) : Fragment() {
    private var compositeDisposable = CompositeDisposable()

    private lateinit var baseRateSpinnerString: String
    private lateinit var targetRateSpinnerString: String

    private var count: Int = 0

    private val startCustomDateLimit: ZonedDateTime
        get() = ZonedDateTime.now(ZoneId.systemDefault()) - Period.of(5, 0, 0)
    private val endCustomDateLimit: ZonedDateTime
        get() = ZonedDateTime.now(ZoneId.systemDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyUtils = CurrencyUtils(disposable = compositeDisposable)
        val currencyLineChart = StockLineChart(currency_chart)

        val ratesNameArray = resources.getStringArray(R.array.rates)

        base_rate_spinner.setSelection(defaultCurrencyId)
        base_rate_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseRateSpinnerString = ratesNameArray[position].split(",")[0]
                currencyLineChart.clearChart()
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
                currencyLineChart.clearChart()

                if(count > 1) {
                    currencyUtils.plotRatesPerPeriod(
                        startDate = "1W",
                        baseRate = baseRateSpinnerString,
                        targetRate = targetRateSpinnerString,
                        stockLineChart = currencyLineChart,
                        context = requireContext()
                    )
                }

                count += 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                            context = requireContext()
                        )
                    }
                    R.id.currency_togglebutton_one_month_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                            startDate = "1M",
                            targetRate = targetRateSpinnerString,
                            baseRate = baseRateSpinnerString,
                            stockLineChart = currencyLineChart,
                            context = requireContext()
                        )
                    }
                    R.id.currency_togglebutton_six_months_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                            startDate = "6M",
                            targetRate = targetRateSpinnerString,
                            baseRate = baseRateSpinnerString,
                            stockLineChart = currencyLineChart,
                            context = requireContext()
                        )
                    }
                    R.id.currency_togglebutton_one_year_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                            startDate = "1Y",
                            targetRate = targetRateSpinnerString,
                            baseRate = baseRateSpinnerString,
                            stockLineChart = currencyLineChart,
                            context = requireContext()
                        )
                    }
                    R.id.currency_togglebutton_five_years_selector -> {
                        currencyUtils.plotRatesPerPeriod(
                            startDate = "5Y",
                            targetRate = targetRateSpinnerString,
                            baseRate = baseRateSpinnerString,
                            stockLineChart = currencyLineChart,
                            context = requireContext()
                        )
                    }
                    R.id.currency_togglebutton_custom_period_selector -> {
                        var startDateTime: String
                        var endDateTime: String
                        val now = Calendar.getInstance()
                        val picker = MaterialDatePicker.Builder.dateRangePicker()
                            .setSelection(
                                androidx.core.util.Pair(
                                    now.timeInMillis,
                                    now.timeInMillis
                                )
                            )
                            .setCalendarConstraints(
                                CalendarConstraints.Builder()
                                    .setStart(startCustomDateLimit.toInstant().toEpochMilli())
                                    .setEnd(endCustomDateLimit.toInstant().toEpochMilli())
                                    .build()
                            )
                            .build()
                        picker.addOnPositiveButtonClickListener {
                            val startInstant = Instant.ofEpochMilli(it.first ?: 0)
                            val endInstant = Instant.ofEpochMilli(it.second ?: 0)
                            startDateTime =
                                ZonedDateTime.ofInstant(startInstant, ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            endDateTime =
                                ZonedDateTime.ofInstant(endInstant, ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                            currencyUtils.plotRatesPerPeriod(
                                startDate = startDateTime,
                                endDate = endDateTime,
                                targetRate = targetRateSpinnerString,
                                baseRate = baseRateSpinnerString,
                                stockLineChart = currencyLineChart,
                                context = requireContext()
                            )
                        }
                        picker.addOnNegativeButtonClickListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_calendar_canceled_selection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        picker.show(activity?.supportFragmentManager!!, picker.toString())
                    }
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
