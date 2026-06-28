package com.chenboda01.bnotesv1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;

    public class AndroidBridge {
        @JavascriptInterface
        public void openBMap() {
            runOnUiThread(() -> {
                try {
                    PackageManager pm = getPackageManager();
                    Intent launch = pm.getLaunchIntentForPackage("com.chenboda01.bmapv2osflow96");
                    if (launch != null) {
                        startActivity(launch);
                    } else {
                        Toast.makeText(MainActivity.this, "B-Map V1 is not installed yet.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Could not open B-Map V1.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript("window.bnotesBack && window.bnotesBack()", null);
    }
}
