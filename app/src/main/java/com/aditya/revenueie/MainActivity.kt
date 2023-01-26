package com.aditya.revenueie

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aditya.revenueie.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class MainActivity : AppCompatActivity() {
    var uploadMessage: ValueCallback<Array<Uri>>? = null

    private lateinit var binding: ActivityMainBinding

    private lateinit var smsCallback: ValueCallback<String>

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get consent intent
                        val consentIntent =
                            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            smsRecLauncher.launch(consentIntent)
                        } catch (e: ActivityNotFoundException) {
                            Log.e("Broadcast Error", e.toString())
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }

    private var smsRecLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    Log.e("Message Rec", message.toString())

                    smsCallback.onReceiveValue(message.toString().split(" ")[3])
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)

        smsCallback = ValueCallback<String> { it ->
            binding.contents.webView.evaluateJavascript("javascript: document.getElementById('passcode-input').value = '${it}'; document.getElementById('passcodeForm').submit();", null)
        }

        val webViewClient = CustomWebViewClient(this)

        binding.contents.webView.webViewClient = webViewClient
        binding.contents.webView.settings.javaScriptEnabled = true
        binding.contents.webView.settings.domStorageEnabled = true
        binding.contents.webView.settings.builtInZoomControls = true
        binding.contents.webView.settings.displayZoomControls = false
        binding.contents.webView.settings.allowFileAccess = true
        binding.contents.webView.settings.allowContentAccess = true
        binding.contents.webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }

                uploadMessage = filePathCallback

                try {
                    uploadMessage = filePathCallback

                    val intent = fileChooserParams?.createIntent()

                    try {
                        showFileChooserLauncher.launch(intent)
                    } catch (e: ActivityNotFoundException) {
                        uploadMessage = null
                        Toast.makeText(
                            this@MainActivity,
                            "Cannot Open File Chooser",
                            Toast.LENGTH_LONG
                        ).show()
                        return false
                    }
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(
                        this@MainActivity,
                        "Cannot Open File Chooser",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
                return true
            }
        }

        binding.contents.webView.loadUrl("https://www.ros.ie/myaccount-web/sign_in.html")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var showFileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    if (uploadMessage == null) return@registerForActivityResult
                    uploadMessage!!.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            result.resultCode,
                            data
                        )
                    )
                    uploadMessage = null
                }
            }
        }

    override fun onBackPressed() {
        if (binding.contents.webView.canGoBack()) {
            binding.contents.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}