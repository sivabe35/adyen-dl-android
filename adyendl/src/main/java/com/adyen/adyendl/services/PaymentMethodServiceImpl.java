package com.adyen.adyendl.services;

import android.util.Log;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.util.AsyncOperationCallback;
import com.adyen.adyendl.util.CheckoutHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Override
    public void fetchPaymentMethodsAsync(final AsyncOperationCallback asyncOperationCallback) {
        JSONObject merchantSignatureResponse = new RegisterMerchantServerServiceImpl(configuration, payment).fetchMerchantSignature();

        String httpPostBody = buildPostBodyForPaymentMethodsRequest(merchantSignatureResponse);
        final CheckoutHttpRequest<String> checkoutHttpRequest = new CheckoutHttpRequest<>(Configuration.URLS.getHppDirectoryUrl(configuration.getEnvironment()), httpPostBody);
        
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if(subscriber.isUnsubscribed()) {
                    return;
                }
                String response = null;
                try {
                    response = checkoutHttpRequest.stringPostRequestWithBody();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(response);
                subscriber.onCompleted();
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
            public void onNext(String s) {
                asyncOperationCallback.onSuccess(s);
            }
        });
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
