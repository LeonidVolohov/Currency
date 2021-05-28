package com.volohov.currency.ui.chartfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.volohov.currency.R
import com.volohov.currency.ui.UiConstants
import com.volohov.currency.ui.chart.StockLineChart
import com.volohov.currency.ui.currency.CurrencyUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_chart.*

class ChartFragment : Fragment() {
    private var defaultCurrencyInd = UiConstants.DEFAULT_CURRENCY_ID
    private var compositeDisposable = CompositeDisposable()

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
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyUtils = CurrencyUtils(disposable = compositeDisposable)
        val currencyLineChart = StockLineChart(currency_chart)

        val ratesNameArray = resources.getStringArray(R.array.rates)

        base_rate_spinner.setSelection(defaultCurrencyInd)
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
                }
            }
        }
    }
}
