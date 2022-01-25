package com.aditya.revenueie

import android.content.Context
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.aditya.revenueie.pjo.SignUpPage
import com.google.android.material.progressindicator.LinearProgressIndicator


class CustomWebViewClient(
    private var context: Context
) : WebViewClient() {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    private val pjoClients = listOf<PJOInterface>(
        SignUpPage(context, sharedPrefs)
    )

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        for (pjoClient in pjoClients) {
            if (pjoClient.checkPageConditions(view, url)) {
                view?.evaluateJavascript(pjoClient.codeToExecute()) { }
                break
            }
        }

        if (sharedPrefs.getBoolean(context.getString(R.string.key_mode_revenue_tracker), false)
            && url?.startsWith("https://www.ros.ie/myaccount-web/portal.html") == true
        ) {
            view?.evaluateJavascript(
                "javascript:document.getElementById('myreceipts-url').click()"
            ) { }
        }
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        error?.let {
            onReceivedError(
                view,
                it.errorCode,
                error.description.toString(),
                request?.url.toString()
            )
        }
    }
}