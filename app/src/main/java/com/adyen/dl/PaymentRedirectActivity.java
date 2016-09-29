package com.adyen.dl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by andrei on 9/15/16.
 */
public class PaymentRedirectActivity extends Activity {

    private WebView mPaymentWebView;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_redirect);

        context = this;

        Intent resultIntent = getIntent();
        String redirectUrl = resultIntent.getStringExtra("redirectUrl");

        mPaymentWebView = (WebView)findViewById(R.id.paymentWebView);
        mPaymentWebView.setWebViewClient(new CustomWebBrowser());

        mPaymentWebView.getSettings().setLoadsImagesAutomatically(true);
        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mPaymentWebView.getSettings().setSupportMultipleWindows(true);
        mPaymentWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mPaymentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mPaymentWebView.loadUrl(redirectUrl);
    }

    private class CustomWebBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("adyendl")) {
                Intent intent = new Intent(context, PaymentResultActivity.class);
                intent.putExtra("callbackUrl", url);
                startActivity(intent);
                finish();
            } else {
                view.loadUrl(url);
            }

            return true;
        }
    }
}
