package com.example.aplikasi_berita.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.aplikasi_berita.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String url = getIntent().getStringExtra("url");
        WebView webView = findViewById(R.id.webViewDetail);

        // 1. Dapatkan objek WebSettings dari WebView
        WebSettings webSettings = webView.getSettings();

        // 2. Aktifkan JavaScript (banyak situs modern memerlukannya)
        webSettings.setJavaScriptEnabled(true);

        // 3. Atur agar WebView memuat konten sesuai lebar layar
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // 4. (Opsional) Mengaktifkan fitur zoom bawaan
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false); // Sembunyikan tombol +/- zoom

        // Mengatur WebViewClient agar link terbuka di dalam aplikasi
        webView.setWebViewClient(new WebViewClient());

        // Memuat URL ke dalam WebView
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}