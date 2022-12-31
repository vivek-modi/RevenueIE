package com.aditya.revenueie.pjo

import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import com.aditya.revenueie.PJOInterface
import com.aditya.revenueie.R
import com.google.android.gms.auth.api.phone.SmsRetriever

class TextVerificationPageReceiverStart(
    private val context: Context,
    private val prefs: SharedPreferences,
) : PJOInterface() {

    override fun checkPageConditions(view: WebView?, url: String?): Boolean {
        return (url?.startsWith("https://www.ros.ie/myaccount-web/sign_in.html") == true
                && url.endsWith("s2")
                && prefs.getBoolean(context.getString(R.string.mode_autofill_key), false))
    }

    override fun codeToExecute(): String {
        SmsRetriever.getClient(context).startSmsUserConsent(null);
        return ""
    }
}