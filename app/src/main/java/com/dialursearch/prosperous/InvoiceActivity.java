package com.dialursearch.prosperous;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Handler;

public class InvoiceActivity extends Activity {

    WebView webView;
    private ProgressDialog progressBar;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String strcomplain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        webView =(WebView) findViewById(R.id.web);

        pref = getApplicationContext().getSharedPreferences("Invoice", 0); // 0 - for private mode
        editor = pref.edit();

        strcomplain=pref.getString("Invoice","");

        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(this, "", "Loading...");
        progressBar.setCancelable(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Log.i(TAG, "Processing... webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                //Toast.makeText(getActivity(), "Oh no! ", Toast.LENGTH_SHORT).show();
                //progressBar.cancel();
                if (progressBar.isShowing()) {
                    progressBar.cancel();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Log.e(TAG, "Error: " + description);
                //Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl("http://prosperousapp.emeglobal.com/Invoice/!INVOICE.php");
        webView.loadUrl("https://www.google.co.in");

//        webView.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                DownloadManager.Request request = new DownloadManager.Request(
//                        Uri.parse("http://prosperousapp.emeglobal.com/Invoice/invoice.pdf"));
//
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "");
//                DownloadManager dm = (DownloadManager) InvoiceActivity.this.getSystemService(DOWNLOAD_SERVICE);
//                dm.enqueue(request);
//                Toast.makeText(InvoiceActivity.this, "Downloading File", //To notify the Client that the file is being downloaded
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }
}