package com.adyen.adyendl.services;

import android.util.Log;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.util.AsyncOperationCallback;
import com.adyen.adyendl.util.CheckoutHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by andrei on 9/15/16.
 */
public class RetrieveRedirectUrlServiceImpl implements RetrieveRedirectUrlService {

    private static final String tag = RetrieveRedirectUrlServiceImpl.class.getSimpleName();

    private Configuration configuration;
    private Payment payment;

    private String brandCode;
    private String issuerId;

    public RetrieveRedirectUrlServiceImpl(Configuration configuration, Payment payment, String brandCode, String issuerId) {
        this.configuration = configuration;
        this.payment = payment;
        this.brandCode = brandCode;
        this.issuerId = issuerId;
    }

    public class RedirectUrlRequestThread implements Callable {

        private URL url;
        private String requestBody;

        public RedirectUrlRequestThread(URL url, String requestBody) {
            this.url = url;
            this.requestBody = requestBody;
        }

        @Override
        public String call() throws Exception {
            Log.i(tag, "Redirect URL request body: " + requestBody);

            CheckoutHttpRequest<String> checkoutHttpRequest = new CheckoutHttpRequest<>(url, requestBody);
            String responseUrl = checkoutHttpRequest.fetchRedirectUrl();

            Log.i(tag, "Redirect URL response: " + responseUrl);

            return responseUrl;
        }

    }

    @Override
    public void fetchRedirectUrl(final AsyncOperationCallback asyncOperationCallback) {
        RegisterMerchantServerServiceImpl registerMerchantServerService = new RegisterMerchantServerServiceImpl(configuration, payment, brandCode, issuerId);
        registerMerchantServerService.fetchMerchantSignature(new AsyncOperationCallback() {
            @Override
            public void onSuccess(final String response) {
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        try {
                            String httpPostBody = buildPostBodyForRedirectUrlRequest(new JSONObject(response));
                            CheckoutHttpRequest<String> checkoutHttpRequest = new CheckoutHttpRequest<>(Configuration.URLS.getHppDetailsUrl(configuration.getEnvironment()), httpPostBody);
                            String responseUrl = checkoutHttpRequest.fetchRedirectUrl();
                            Log.i(tag, "Redirect URL response: " + responseUrl);
                            subscriber.onNext(responseUrl);
                            subscriber.onCompleted();
                        } catch (JSONException e) {
                            onFailure(e, e.getMessage());
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        asyncOperationCallback.onFailure(e, e.getMessage());
                    }

                    @Override
                    public void onNext(String response) {
                        asyncOperationCallback.onSuccess(response);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e, String errorMessage) {

            }
        });
    }

    private String buildPostBodyForRedirectUrlRequest(JSONObject merchantSignatureResponse) {
        String httpPostBody = null;
        try {
            String brandCode = URLEncoder.encode(merchantSignatureResponse.getString("brandCode"), "UTF-8");
            String issuerId = null;
            String paymentAmount = URLEncoder.encode(merchantSignatureResponse.getString("paymentAmount"), "UTF-8");
            String merchantReference = URLEncoder.encode(merchantSignatureResponse.getString("merchantReference"), "UTF-8");
            String skinCode = URLEncoder.encode(merchantSignatureResponse.getString("skinCode"), "UTF-8");
            String merchantAccount = URLEncoder.encode(merchantSignatureResponse.getString("merchantAccount"), "UTF-8");
            String countryCode = URLEncoder.encode(merchantSignatureResponse.getString("countryCode"), "UTF-8");
            String currencyCode = URLEncoder.encode(merchantSignatureResponse.getString("currencyCode"), "UTF-8");
            String sessionValidity = URLEncoder.encode(merchantSignatureResponse.getString("sessionValidity"), "UTF-8");
            String merchantSig = URLEncoder.encode(merchantSignatureResponse.getString("merchantSig"), "UTF-8");
            httpPostBody = "brandCode=" + brandCode +
                           "&paymentAmount=" + paymentAmount +
                           "&merchantReference=" + merchantReference +
                           "&skinCode=" + skinCode +
                           "&merchantAccount=" + merchantAccount +
                           "&countryCode=" + countryCode +
                           "&currencyCode=" + currencyCode +
                           "&sessionValidity=" + sessionValidity +
                           "&merchantSig=" + merchantSig;
            if(!merchantSignatureResponse.isNull("issuerId")){
                issuerId = URLEncoder.encode(merchantSignatureResponse.getString("issuerId"), "UTF-8");
                httpPostBody = httpPostBody + "&issuerId=" + issuerId;
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        }

        return httpPostBody;
    }

}
