package com.adyen.adyendl;

import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.services.PaymentMethodServiceImpl;
import com.adyen.adyendl.services.PaymentMethodsService;
import com.adyen.adyendl.services.RetrieveRedirectUrlServiceImpl;

import org.json.JSONObject;

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

    public JSONObject fetchPaymentMethods(Configuration configuration, Payment payment) {
        PaymentMethodsService paymentMethodsService = new PaymentMethodServiceImpl(configuration, payment);
        JSONObject paymentMethodsJSON = paymentMethodsService.fetchPaymentMethods();
        return paymentMethodsJSON;
    }

    public String fetchRedirectUrl(Configuration configuration, Payment payment, String brandCode, String issuerId) {
        RetrieveRedirectUrlServiceImpl retrieveRedirectUrlImpl = new RetrieveRedirectUrlServiceImpl(configuration, payment, brandCode, issuerId);
        String redirectUrlStr = retrieveRedirectUrlImpl.fetchRedirectUrl();
        return redirectUrlStr;
    }

}
