package com.volohov.currency.ui.onboardingactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.volohov.currency.R
import com.volohov.currency.ui.UiConstants
import com.volohov.currency.ui.main.MainActivity
import java.util.*

class OnboardingActivity : AppCompatActivity() {

    private lateinit var buttonSavePrefs: Button
    private lateinit var spinnerDefaultCurrency: Spinner
    private lateinit var groupLanguageSelection: RadioGroup

    private val sharedPrefs: SharedPreferences
        get() = getPreferences(Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        var (firstLaunch, language, defaultCurrencyId) = retrievePrefs()

        if (firstLaunch) {
            setContentView(R.layout.activity_onboarding)
            buttonSavePrefs = findViewById(R.id.button_save_preferences)
            spinnerDefaultCurrency = findViewById(R.id.default_currency_selector_spinner)
            groupLanguageSelection = findViewById(R.id.language_selection_group)

            spinnerDefaultCurrency.setSelection(UiConstants.DEFAULT_CURRENCY_ID)
            spinnerDefaultCurrency.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        defaultCurrencyId = position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            buttonSavePrefs.setOnClickListener {
                when (groupLanguageSelection.checkedRadioButtonId) {
                    R.id.english_language -> {
                        language = "en"
                    }
                    R.id.russian_language -> {
                        language = "ru"
                    }
                }
                with(sharedPrefs.edit()) {
                    putBoolean("firstLaunch", false)
                    putInt("defaultCurrencyInd", defaultCurrencyId)
                    putString("language", language)
                    commit()
                }
                callMain(language, firstLaunch, defaultCurrencyId)
            }
        } else {
            callMain(language, firstLaunch, defaultCurrencyId)
        }
    }

    private fun callMain(language: String, firstLaunch: Boolean, defaultCurrencyInd: Int) {
        updateLocale(language)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("firstLaunch", firstLaunch)
        intent.putExtra("defaultCurrencyInd", defaultCurrencyInd)
        startActivity(intent)
    }

    private fun retrievePrefs(): Triple<Boolean, String, Int> {
        val firstLaunch = sharedPrefs.getBoolean("firstLaunch", true)
        val language = sharedPrefs.getString("language", "en").toString()
        val defaultCurrencyInd = sharedPrefs.getInt("defaultCurrencyInd", 0)
        return Triple(firstLaunch, language, defaultCurrencyInd)
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
