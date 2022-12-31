package com.aditya.revenueie

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import com.aditya.revenueie.pjo.RedirectToTracker
import com.aditya.revenueie.pjo.SignUpPage
import com.aditya.revenueie.pjo.TextVerificationPageReceiverStart


class CustomWebViewClient(
    private var context: Context
) : WebViewClient() {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    private val pjoStartedClients = mutableListOf(
        TextVerificationPageReceiverStart(context, sharedPrefs)
    )

    private val pjoFinishedClients = mutableListOf(
        SignUpPage(context, sharedPrefs),
        RedirectToTracker(context, sharedPrefs)
    )

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        Log.e("Remote Url Started", url.toString())

        for (pjoClient in pjoStartedClients) {
            if (pjoClient.checkPageConditions(view, url)) {
                view?.evaluateJavascript(pjoClient.codeToExecute()) { }
                break
            }
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        Log.e("Remote Url Finished", url.toString())

        for (pjoClient in pjoFinishedClients) {
            if (pjoClient.checkPageConditions(view, url)) {
                view?.evaluateJavascript(pjoClient.codeToExecute()) { }
                break
            }
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