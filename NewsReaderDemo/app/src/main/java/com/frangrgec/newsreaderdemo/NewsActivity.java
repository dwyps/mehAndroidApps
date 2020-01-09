package com.frangrgec.newsreaderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends AppCompatActivity {

    WebView newsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsView=findViewById(R.id.newsWebView);
        Intent intent=getIntent();

        newsView.getSettings().setJavaScriptEnabled(true);

        newsView.setWebViewClient(new WebViewClient());

        //Opens the news url in the app
        newsView.loadUrl(intent.getStringExtra("newsUrl"));

    }
}
