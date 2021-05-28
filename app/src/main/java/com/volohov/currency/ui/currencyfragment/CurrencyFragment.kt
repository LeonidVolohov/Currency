package com.volohov.currency.ui.currencyfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.volohov.currency.R
import com.volohov.currency.api.currencylayer.CurrencyLayerUtils
import com.volohov.currency.ui.UiConstants
import com.volohov.currency.ui.UiConstants.Companion.PRIMARY_CURRENCIES_NAME
import com.volohov.currency.ui.currency.CurrencyUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_currency.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CurrencyFragment : Fragment() {

    private var defaultCurrencyInd = UiConstants.DEFAULT_CURRENCY_ID

    private var compositeDisposable = CompositeDisposable()
    private var disposable: Disposable? = null
    private lateinit var baseRateSpinnerString: String
    private lateinit var targetRateSpinnerString: String
    private var isNumeric: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratesNameArray = resources.getStringArray(R.array.rates)

        val currencyUtils = CurrencyUtils(disposable = compositeDisposable)

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
                changeToDefaultValue()
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
                changeToDefaultValue()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        calculate_button.setOnClickListener {
            if (baseRateSpinnerString == targetRateSpinnerString) {
                textview_differenies_today_result.text = currencyUtils.stringMultiplication(
                    rate_number.text.toString(),
                    UiConstants.DEFAULT_EDIT_TEXT_NUMBER.toString()
                )
            } else {
                changeToDefaultValue()
                isNumeric = try {
                    rate_number.text.toString().toDouble()
                    true
                } catch (exception: NumberFormatException) {
                    false
                }

                if (isNumeric) {
                    disposable = CurrencyLayerUtils().getTargetRatePrice(
                        baseRate = baseRateSpinnerString,
                        targetRate = "$targetRateSpinnerString,$PRIMARY_CURRENCIES_NAME"
                    )
                        .subscribe(
                            { response ->
                                val date = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss").format(
                                    Date(Timestamp(response.timestamp).time)
                                )
                                last_date_update.text = this.getString(
                                    R.string.last_updated_date,
                                    date.toString()
                                )

                                textview_differenies_today_result.text = currencyUtils.stringMultiplication(
                                    response.quotes[baseRateSpinnerString + targetRateSpinnerString].toString(),
                                    rate_number.text.toString()
                                )

                                first_primary_currency_result.text = response.quotes[baseRateSpinnerString + "USD"].toString()
                                second_primary_currency_result.text = response.quotes[baseRateSpinnerString + "EUR"].toString()
                                third_primary_currency_result.text = response.quotes[baseRateSpinnerString + "GBP"].toString()
                                fourth_primary_currency_result.text = response.quotes[baseRateSpinnerString + "JPY"].toString()
                                fifth_primary_currency_result.text = response.quotes[baseRateSpinnerString + "CHF"].toString()

                                val test = response.quotes[baseRateSpinnerString + targetRateSpinnerString].toString()

                                val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
                                val lastMonth = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
                                val lastYear = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
                                Log.i("date", "$yesterday, $lastMonth, $lastYear")
                                disposable = CurrencyLayerUtils().getHistoricalRatePrice(
                                    baseRate = baseRateSpinnerString,
                                    targetRate = targetRateSpinnerString,
                                    date = yesterday
                                )
                                    .subscribe(
                                        { response ->
                                            textview_differenies_yesterday_result.text = response.quotes[baseRateSpinnerString + targetRateSpinnerString].toString()
                                        },
                                        { failure ->
                                            Toast.makeText(context, failure.message.toString(), Toast.LENGTH_LONG).show()
                                        }
                                    )

                                disposable = CurrencyLayerUtils().getHistoricalRatePrice(
                                    baseRate = baseRateSpinnerString,
                                    targetRate = targetRateSpinnerString,
                                    date = lastMonth
                                )
                                    .subscribe(
                                        { response ->
                                            textview_differenies_last_month_result.text = response.quotes[baseRateSpinnerString + targetRateSpinnerString].toString()
                                        },
                                        { failure ->
                                            Toast.makeText(context, failure.message.toString(), Toast.LENGTH_LONG).show()
                                        }
                                    )

                                disposable = CurrencyLayerUtils().getHistoricalRatePrice(
                                    baseRate = baseRateSpinnerString,
                                    targetRate = targetRateSpinnerString,
                                    date = lastYear
                                )
                                    .subscribe(
                                        { response ->
                                            textview_differenies_last_year_result.text = response.quotes[baseRateSpinnerString + targetRateSpinnerString].toString()
                                        },
                                        { failure ->
                                            Toast.makeText(context, failure.message.toString(), Toast.LENGTH_LONG).show()
                                        }
                                    )
                            },
                            { failure ->
                                Toast.makeText(context, failure.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                } else {
                    Toast.makeText(context, getString(R.string.toast_wrong_input), Toast.LENGTH_LONG).show()
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

    private fun changeToDefaultValue() {
        last_date_update.text = ""
        first_primary_currency_result.text = ""
        second_primary_currency_result.text = ""
        third_primary_currency_result.text = ""
        fourth_primary_currency_result.text = ""
        fifth_primary_currency_result.text = ""
        textview_differenies_today_result.text = ""
        textview_differenies_yesterday_result.text = ""
        textview_differenies_last_month_result.text = ""
        textview_differenies_last_year_result.text = ""
    }
}
