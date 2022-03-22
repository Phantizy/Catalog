package com.example.catalog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsReader extends AppCompatActivity {

    WebView newsHtmlPreview = null;
    String thisUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);

        // Get URL passed in with the intent
        thisUrl = getIntent().getExtras().getString("newsItemUrl");

        // Prevent clicks and redirect from going to default browser on the device
        newsHtmlPreview = (android.webkit.WebView) findViewById(R.id.newsReader);
        newsHtmlPreview.setWebViewClient(new NewWebViewClient());

        WebSettings myBrowserSettings = newsHtmlPreview.getSettings();
        myBrowserSettings.setBuiltInZoomControls(true);
        myBrowserSettings.setLoadWithOverviewMode(true);
        myBrowserSettings.setUseWideViewPort(true);

        // load the page
        newsHtmlPreview.loadUrl(thisUrl);
    }

        private class NewWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }
}