package com.aditya.revenueie.pjo

import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import com.aditya.revenueie.PJOInterface
import com.aditya.revenueie.R

class SignUpPage(
    private val context: Context,
    private val prefs: SharedPreferences,
) : PJOInterface() {

    override fun checkPageConditions(view: WebView?, url: String?): Boolean {
        return url?.startsWith("https://www.ros.ie/myaccount-web/sign_in.html") == true
    }

    override fun codeToExecute(): String {
        return "javascript:" +
                getAutofillForSetting(R.string.key_ppsn, "pps-input") +
                getAutofillForSetting(R.string.key_password, "password-input") +
                getAutofillForSetting(R.string.key_dobdd, "dob-day-input") +
                getAutofillForSetting(R.string.key_dobmm, "dob-month-input") +
                getAutofillForSetting(R.string.key_dobyy, "dob-year-input")
    }

    private fun getAutofillForSetting(keyId: Int, elementIdInBrowser: String): String {
        return if (prefs.contains(context.getString(keyId)))
            getFillValueOfElementByIdScript(
                elementIdInBrowser,
                prefs.getString(context.getString(keyId), "")
            )
        else
            ""
    }

    private fun getFillValueOfElementByIdScript(id: String?, value: String?): String {
        return "document.getElementById('${id}').value = '${value}';"
    }
}