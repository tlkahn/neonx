package com.neonxorg.neonx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.SimpleWebServer;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private SimpleWebServer server = null;
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    mWebView.loadUrl("https://yahoo.com");
                    return true;
                case R.id.navigation_dashboard:
                    mWebView.loadUrl("http://localhost:3000");
                    return true;
                case R.id.navigation_notifications:
                    mWebView.loadUrl("https://stackexchange.com");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mWebView = findViewById(R.id.webkit);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        if (!haveNetworkConnection()) {
            new AlertDialog.Builder(this)
                .setTitle("You are not connected to internet.")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
        }
        startLocalServer(3000, "www", true, true );
    }

  public void startLocalServer(int port, String root, Boolean localhost, Boolean keepAlive) {
      try {
          File www_root = new File(root);
          server = new WebServer("localhost", port, www_root.getAbsoluteFile());
          server.start();
          printIp();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  private void printIp() {
      WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
      int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
      final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
          (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
      System.out.println("Connected : " + "Please access! http://" + formatedIpAddress + ":" + server.getListeningPort() +" From a web browser");
  }
}
