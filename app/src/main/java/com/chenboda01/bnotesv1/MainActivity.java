package com.chenboda01.bnotesv1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
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
                    if (launch == null) {
                        launch = new Intent(Intent.ACTION_MAIN);
                        launch.addCategory(Intent.CATEGORY_LAUNCHER);
                        launch.setClassName("com.chenboda01.bmapv2osflow96", "com.chenboda01.bmapv2osflow96.MainActivity");
                    }
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launch);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "B-Map V1 is not installed yet.", Toast.LENGTH_LONG).show();
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

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String js = "window.deleteNote=function(){" +
                        "var n=cur&&cur();" +
                        "if(!n){alert('Select a note first.');return;}" +
                        "var name=n.title||'Untitled note';" +
                        "notes=notes.filter(function(x){return x.id!==n.id});" +
                        "currentId=notes[0]?notes[0].id:null;" +
                        "saveStore();renderList();renderEditor();" +
                        "alert('Deleted '+name);" +
                        "};";
                view.evaluateJavascript(js, null);
            }
        });

        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript("window.bnotesBack && window.bnotesBack()", null);
    }
}
