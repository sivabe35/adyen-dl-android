package com.adyen.dl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;

import com.adyen.dl.shared.CustomTabsHelper;
import com.adyen.dl.shared.ServiceConnectionCallback;

/**
 * Created by andrei on 9/15/16.
 */
public class PaymentRedirectActivity extends Activity implements ServiceConnectionCallback {

    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;

    private String mPackageNameToBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_redirect);

        Intent resultIntent = getIntent();
        String redirectUrl = resultIntent.getStringExtra("redirectUrl");

        if(!TextUtils.isEmpty(redirectUrl)) {
            bindCustomTabsService(redirectUrl);
        }
    }

    private void bindCustomTabsService(final String url) {
        if (mClient != null) return;
        if (TextUtils.isEmpty(mPackageNameToBind)) {
            mPackageNameToBind = CustomTabsHelper.getPackageNameToUse(this);
            if (mPackageNameToBind == null) {
                return;
            }
        }

        mConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;
                prefetchContent(url);
                loadCustomTabs(url);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(this, mPackageNameToBind, mConnection);
    }

    public void prefetchContent(String url) {
        if (mClient != null) {
            mClient.warmup(0);
            CustomTabsSession customTabsSession = getSession();
            customTabsSession.mayLaunchUrl(Uri.parse(url), null, null);
        }
    }

    public void loadCustomTabs(String url) {
        CustomTabsIntent.Builder mBuilder = new CustomTabsIntent.Builder(getSession());
        CustomTabsIntent mIntent = mBuilder.build();
        mIntent.launchUrl(this, Uri.parse(url));
    }

    private CustomTabsSession getSession() {
        return mClient.newSession(new CustomTabsCallback() {
            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                super.onNavigationEvent(navigationEvent, extras);
            }
        });
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        mClient = client;
    }

    @Override
    public void onServiceDisconnected() {
        mClient = null;
    }
}
