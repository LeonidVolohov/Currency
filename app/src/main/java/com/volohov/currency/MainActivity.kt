package com.volohov.currency

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.volohov.currency.ui.chartfragment.ChartFragment
import com.volohov.currency.ui.currencyfragment.CurrencyFragment
import com.volohov.currency.ui.predictionfragment.PredictionFragment
import kotlinx.android.synthetic.main.activity_main_bottomnavview.*

class MainActivity : AppCompatActivity() {
    private lateinit var selectedFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_bottomnavview)

        selectedFragment = CurrencyFragment()

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
                    ChartFragment()
                }

                R.id.currency_tab -> {
                    CurrencyFragment()
                }

                R.id.prediction_tab -> {
                    PredictionFragment()
                }

                else -> TODO("?")
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commitNow()

            true
        }
}
