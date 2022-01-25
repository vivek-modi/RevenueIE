package com.aditya.revenueie

import android.webkit.WebView

abstract class PJOInterface {
    abstract fun checkPageConditions(view: WebView?, url: String?): Boolean
    abstract fun codeToExecute(): String
}