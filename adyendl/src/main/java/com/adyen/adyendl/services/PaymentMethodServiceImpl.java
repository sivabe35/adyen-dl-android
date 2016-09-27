package com.adyen.adyendl.services;

import android.util.Log;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.util.CheckoutHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by andrei on 5/31/16.
 */
public class PaymentMethodServiceImpl implements PaymentMethodsService {

    private static final String tag = PaymentMethodServiceImpl.class.getSimpleName();

    private Configuration configuration;
    private Payment payment;

    public PaymentMethodServiceImpl(Configuration configuration, Payment payment) {
        this.configuration = configuration;
        this.payment = payment;
    }

    public class PaymentMethodsRequestThread implements Callable {

        private URL url;
        private String requestBody;

        public PaymentMethodsRequestThread(URL url, String requestBody) {
            this.url = url;
            this.requestBody = requestBody;
        }

        @Override
        public String call() throws Exception {
            Log.i(tag, "Payment methods request body: " + requestBody);

            CheckoutHttpRequest<String> checkoutHttpRequest = new CheckoutHttpRequest<>(url, requestBody);
            String response = checkoutHttpRequest.stringPostRequestWithBody();

            Log.i(tag, "Payment methods response: " + response);

            return response;
        }

    }

    @Override
    public JSONObject fetchPaymentMethods() {
        JSONObject merchantSignatureResponse = new RegisterMerchantServerServiceImpl(configuration, payment).fetchMerchantSignature();
        JSONObject paymentMethodsResponseJson = null;
        Log.i(tag, "Merchant signature response: " + merchantSignatureResponse.toString());
        try {
            String httpPostBody = buildPostBodyForPaymentMethodsRequest(merchantSignatureResponse);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Callable<String> callable = new PaymentMethodsRequestThread(Configuration.URLS.getHppDirectoryUrl(configuration.getEnvironment()), httpPostBody);
            Future<String> futurePaymentMethodsResponse = executor.submit(callable);
            if(futurePaymentMethodsResponse != null) {
                String paymentMethodsResponse = futurePaymentMethodsResponse.get();
                paymentMethodsResponseJson = new JSONObject(paymentMethodsResponse);
            }
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(tag, e.getMessage(), e);
        }

        return paymentMethodsResponseJson;
    }

    private String buildPostBodyForPaymentMethodsRequest(JSONObject merchantSignatureResponse) {
        String httpPostBody = null;
        try {
            String paymentAmount = URLEncoder.encode(merchantSignatureResponse.getString("paymentAmount"), "UTF-8");
            String merchantReference = URLEncoder.encode(merchantSignatureResponse.getString("merchantReference"), "UTF-8");
            String skinCode = URLEncoder.encode(merchantSignatureResponse.getString("skinCode"), "UTF-8");
            String merchantAccount = URLEncoder.encode(merchantSignatureResponse.getString("merchantAccount"), "UTF-8");
            String countryCode = URLEncoder.encode(merchantSignatureResponse.getString("countryCode"), "UTF-8");
            String currencyCode = URLEncoder.encode(merchantSignatureResponse.getString("currencyCode"), "UTF-8");
            String sessionValidity = URLEncoder.encode(merchantSignatureResponse.getString("sessionValidity"), "UTF-8");
            String merchantSig = URLEncoder.encode(merchantSignatureResponse.getString("merchantSig"), "UTF-8");
            httpPostBody = "paymentAmount=" + paymentAmount +
                           "&merchantReference=" + merchantReference +
                           "&skinCode=" + skinCode +
                           "&merchantAccount=" + merchantAccount +
                           "&countryCode=" + countryCode +
                           "&currencyCode=" + currencyCode +
                           "&sessionValidity=" + sessionValidity +
                           "&merchantSig=" + merchantSig;
        } catch (UnsupportedEncodingException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        }

        return httpPostBody;
    }

}
