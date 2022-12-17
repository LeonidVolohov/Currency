package com.volohov.currency.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.volohov.currency.R
import com.volohov.currency.ui.UiConstants
import com.volohov.currency.ui.chartfragment.ChartFragment
import com.volohov.currency.ui.currencyfragment.CurrencyFragment
import com.volohov.currency.ui.predictionfragment.PredictionFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var selectedFragment: Fragment

    private var defaultCurrencyId = UiConstants.DEFAULT_CURRENCY_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        defaultCurrencyId =
            intent.getIntExtra("defaultCurrencyInd", UiConstants.DEFAULT_CURRENCY_ID)

        selectedFragment = CurrencyFragment(defaultCurrencyId)

        val firstLaunch = intent.getBooleanExtra("firstLaunch", true)

        if (firstLaunch) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commitNow()

            if (!isOnline(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_LONG).show()
            }
        } else {

            if (!isOnline(this)) {
                Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_LONG).show()
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, selectedFragment)
            .commitNow()
        bottom_navigation_view.setOnNavigationItemSelectedListener(navigationListener)
        bottom_navigation_view.selectedItemId = R.id.currency_tab
    }

    private val navigationListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            selectedFragment = when(item.itemId) {
                R.id.chart_tab -> {
                    ChartFragment(defaultCurrencyId = defaultCurrencyId)
                }

                R.id.currency_tab -> {
                    CurrencyFragment(defaultCurrencyId = defaultCurrencyId)
                }

                R.id.prediction_tab -> {
                    PredictionFragment(defaultCurrencyId = defaultCurrencyId)
                }

                else -> TODO("?")
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commitNow()

            true
        }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }
}
