package com.adyen.adyendl;

import android.net.Uri;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.services.PaymentMethodServiceImpl;
import com.adyen.adyendl.services.PaymentMethodsService;
import com.adyen.adyendl.services.RetrieveRedirectUrlServiceImpl;
import com.adyen.adyendl.util.AsyncOperationCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrei on 9/26/16.
 */
public class PaymentsProcessor {

    private static PaymentsProcessor mInstance = null;

    private static final String tag = PaymentsProcessor.class.getSimpleName();

    private PaymentsProcessor() {

    }

    public static PaymentsProcessor getInstance() {
        if(mInstance == null) {
            mInstance = new PaymentsProcessor();
        }
        return mInstance;
    }

    public void fetchPaymentMethods(Configuration configuration, Payment payment, final AsyncOperationCallback asyncOperationCallback) {
        PaymentMethodsService paymentMethodsService = new PaymentMethodServiceImpl(configuration, payment);
        paymentMethodsService.fetchPaymentMethods(new AsyncOperationCallback() {
            @Override
            public void onSuccess(String response) {
                asyncOperationCallback.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable e, String errorMessage) {
                asyncOperationCallback.onFailure(e, errorMessage);
            }
        });

    }

    public String fetchRedirectUrl(Configuration configuration, Payment payment, String brandCode, String issuerId) {
        RetrieveRedirectUrlServiceImpl retrieveRedirectUrlImpl = new RetrieveRedirectUrlServiceImpl(configuration, payment, brandCode, issuerId);
        String redirectUrlStr = retrieveRedirectUrlImpl.fetchRedirectUrl();
        return redirectUrlStr;
    }

    public String verifyResultUrl(String resultUrl) {
        Uri uri = Uri.parse(resultUrl);
        Map<String, String> queryStringMap = new HashMap<>();
        try {
            queryStringMap = convertUriQueryStringToMap(uri);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(queryStringMap != null && queryStringMap.keySet().size() > 0) {
            return queryStringMap.get("authResult");
        } else {
            return null;
        }
    }

    private Map<String, String> convertUriQueryStringToMap(Uri uri) throws UnsupportedEncodingException {
        Map<String, String> queryStringMap = new HashMap<>();

        String query = uri.getQuery();
        String[] queryStringPairs = query.split("&");
        for (String queryStringPair : queryStringPairs) {
            int idx = queryStringPair.indexOf("=");
            queryStringMap.put(URLDecoder.decode(queryStringPair.substring(0, idx), "UTF-8"), URLDecoder.decode(queryStringPair.substring(idx + 1), "UTF-8"));
        }

        return queryStringMap;
    }

}
