package com.aditya.revenueie.pjo

import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import com.aditya.revenueie.PJOInterface
import com.aditya.revenueie.R

class RedirectToTracker(
    private val context: Context,
    private val prefs: SharedPreferences,
) : PJOInterface() {
    private var redirectedAlready = false

    override fun checkPageConditions(view: WebView?, url: String?): Boolean {
        return !redirectedAlready
                && prefs.getBoolean(context.getString(R.string.key_mode_revenue_tracker), false)
                && url?.startsWith("https://www.ros.ie/myaccount-web/portal.html") == true
    }

    override fun codeToExecute(): String {
        redirectedAlready = true
        return "javascript:document.getElementById('myreceipts-url').click()"
    }
}