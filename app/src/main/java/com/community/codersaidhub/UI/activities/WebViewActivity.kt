package com.community.codersaidhub.UI.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
private val binding by lazy{
    ActivityWebViewBinding.inflate(layoutInflater)}

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }


        val webView: WebView = binding.webview

        // Enable JavaScript (if needed)
        webView.settings.javaScriptEnabled = true
   val url=intent.getStringExtra("Url")
        // Load the URL
       Log.d("url2",url.toString())
        webView.loadUrl(url.toString())
    }
}