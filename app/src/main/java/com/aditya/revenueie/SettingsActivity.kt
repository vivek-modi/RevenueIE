package com.aditya.revenueie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import androidx.preference.Preference.SummaryProvider


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<EditTextPreference>(getString(R.string.key_ppsn))
                ?.setOnBindEditTextListener { editText ->
                    editText.filters = arrayOf<InputFilter>(
                        LengthFilter(9)
                    )
                }

            findPreference<EditTextPreference>(getString(R.string.key_dobdd))
                ?.setOnBindEditTextListener { editText ->
                    editText.filters = arrayOf<InputFilter>(
                        LengthFilter(2)
                    )
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                }
            findPreference<EditTextPreference>(getString(R.string.key_dobmm))
                ?.setOnBindEditTextListener { editText ->
                    editText.filters = arrayOf<InputFilter>(
                        LengthFilter(2)
                    )
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                }
            findPreference<EditTextPreference>(getString(R.string.key_dobyy))
                ?.setOnBindEditTextListener { editText ->
                    editText.filters = arrayOf<InputFilter>(
                        LengthFilter(4)
                    )
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                }

            val passwordPref = findPreference<EditTextPreference>(getString(R.string.key_password))
            passwordPref?.setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordPref?.summaryProvider = SummaryProvider<EditTextPreference> {
                val sb = StringBuilder()
                for (s in it.text.indices) {
                    sb.append("*")
                }
                sb.toString()
            }
        }
    }
}