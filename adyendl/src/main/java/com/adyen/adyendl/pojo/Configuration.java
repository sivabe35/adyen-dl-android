package com.adyen.adyendl.pojo;

import android.util.Log;

import com.adyen.adyendl.util.Environment;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by andrei on 8/2/16.
 */
public class Configuration {

    private static final String tag = Configuration.class.getSimpleName();

    private Environment environment;
    private String paymentSignatureURL;
    private String paymentResultSignatureURL;
    private String paymentStatusURL;

    public Configuration(Environment environment,
                         String paymentSignatureURL,
                         String paymentResultSignatureURL,
                         String paymentStatusURL) {

        this.environment = environment;
        this.paymentSignatureURL = paymentSignatureURL;
        this.paymentResultSignatureURL = paymentResultSignatureURL;
        this.paymentStatusURL = paymentStatusURL;

    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getPaymentSignatureURL() {
        return paymentSignatureURL;
    }

    public void setPaymentSignatureURL(String paymentSignatureURL) {
        this.paymentSignatureURL = paymentSignatureURL;
    }

    public String getPaymentResultSignatureURL() {
        return paymentResultSignatureURL;
    }

    public void setPaymentResultSignatureURL(String paymentResultSignatureURL) {
        this.paymentResultSignatureURL = paymentResultSignatureURL;
    }

    public String getPaymentStatusURL() {
        return paymentStatusURL;
    }

    public void setPaymentStatusURL(String paymentStatusURL) {
        this.paymentStatusURL = paymentStatusURL;
    }

    public static class URLS {

        public static URL getHppDirectoryUrl(Environment environment) {
            try {
                if(environment == Environment.LIVE) {
                    return new URL("https://live.adyen.com/hpp/directory.shtml");
                } else {
                    return new URL("https://test.adyen.com/hpp/directory.shtml");
                }
            } catch (MalformedURLException e) {
                Log.e(tag, e.getMessage(), e);
            }
            return null;
        }

        public static URL getHppDetailsUrl(Environment environment) {
            try {
                if(environment == Environment.LIVE) {
                    return new URL("https://live.adyen.com/hpp/details.shtml");
                } else {
                    return new URL("https://test.adyen.com/hpp/details.shtml");
                }
            } catch (MalformedURLException e) {
                Log.e(tag, e.getMessage(), e);
            }
            return null;
        }

    }
}
