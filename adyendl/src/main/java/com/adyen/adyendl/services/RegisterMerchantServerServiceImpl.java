package com.adyen.adyendl.services;

import android.text.TextUtils;
import android.util.Log;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.util.AsyncOperationCallback;
import com.adyen.adyendl.util.CheckoutHttpRequest;
import com.adyen.adyendl.util.Environment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


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

    @Override
    public void fetchMerchantSignature(final AsyncOperationCallback asyncOperationCallback) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                String merchantSignatureRequestUrl = buildMerchantSignatureRequestURL();
                Log.i(tag, "Merchant singature request URL: " + merchantSignatureRequestUrl);
                CheckoutHttpRequest<String> checkoutHttpRequest = null;
                try {
                    checkoutHttpRequest = new CheckoutHttpRequest<>(new URL(merchantSignatureRequestUrl), null);
                    String response = checkoutHttpRequest.stringPostRequest();
                    Log.i(tag, "Merchant signature response: " + response);
                    subscriber.onNext(response);
                } catch (MalformedURLException e) {
                    subscriber.onError(e);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
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
            public void onNext(String response) {
                asyncOperationCallback.onSuccess(response);
            }
        });
    }

    private String buildMerchantSignatureRequestURL() {
        StringBuilder merchantSignatureRequestURL = new StringBuilder(configuration.getPaymentSignatureURL() + "/payment/signature");

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
