package com.adyen.adyendl.services;

import android.text.TextUtils;
import android.util.Log;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.util.CheckoutHttpRequest;
import com.adyen.adyendl.util.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by andrei on 5/20/16.
 */
public class RegisterMerchantServerServiceImpl implements RegisterMerchantServerService {

    private static final String tag = RegisterMerchantServerServiceImpl.class.getSimpleName();

    private Configuration configuration;
    private Payment payment;

    private String brandCode;
    private String issuerId;

    /*
    * Used for getting merchant signature for retrieving payment methods
    * */
    public RegisterMerchantServerServiceImpl(Configuration configuration, Payment payment) {
        this.configuration = configuration;
        this.payment = payment;
    }

    /*
    * Used for getting merchant signature for retrieving the redirect URL
    * */
    public RegisterMerchantServerServiceImpl(Configuration configuration, Payment payment, String brandCode, String issuerId) {
        this.configuration = configuration;
        this.payment = payment;
        this.brandCode = brandCode;
        this.issuerId = issuerId;
    }

    public class MerchantSignatureRequestThread implements Callable {

        private URL url;

        public MerchantSignatureRequestThread(URL url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            CheckoutHttpRequest<String> checkoutHttpRequest = new CheckoutHttpRequest<>(url, null);
            String response = checkoutHttpRequest.stringPostRequest();
            return response;
        }

    }

    @Override
    public JSONObject fetchMerchantSignature() {
        JSONObject merchantSignatureResponseJson = null;
        try {
            String merchantSignatureRequestUrl = buildMerchantSignatureRequestURL();
            Log.i(tag, "Merchant singature request URL: " + merchantSignatureRequestUrl);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Callable<String> callable = new MerchantSignatureRequestThread(new URL(merchantSignatureRequestUrl));
            if(executor.submit(callable) != null) {
                String merchantSignatureResponse = executor.submit(callable).get();
                Log.i(tag, "Merchant signature response: " + merchantSignatureResponse);
                merchantSignatureResponseJson = new JSONObject(merchantSignatureResponse);
            }
        } catch (MalformedURLException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        }
        return merchantSignatureResponseJson;
    }

    private String buildMerchantSignatureRequestURL() {
        StringBuilder merchantSignatureRequestURL = new StringBuilder(configuration.getPaymentSignatureURL() + "?method=calculateSignature&action=paymentInitiation");

        String environment;
        if(Environment.LIVE.equals(configuration.getEnvironment())) {
            environment = "live";
        } else {
            environment = "test";
        }
        merchantSignatureRequestURL.append("&environment=" + environment);

        merchantSignatureRequestURL.append("&paymentAmount=" + payment.getAmount());
        merchantSignatureRequestURL.append("&merchantReference=" + payment.getMerchantReference());
        merchantSignatureRequestURL.append("&countryCode=" + payment.getCountryCode());
        merchantSignatureRequestURL.append("&currencyCode=" + payment.getCurrency());

        if(!TextUtils.isEmpty(brandCode)) {
            merchantSignatureRequestURL.append("&brandCode=" + brandCode);
        }

        if(!TextUtils.isEmpty(issuerId)) {
            merchantSignatureRequestURL.append("&issuerId=" + issuerId);
        }

        return merchantSignatureRequestURL.toString();
    }

}
