package com.adyen.adyendl.services;

import com.adyen.adyendl.util.AsyncOperationCallback;

import org.json.JSONObject;

/**
 * Created by andrei on 5/31/16.
 */
public interface PaymentMethodsService {

    JSONObject fetchPaymentMethods();

    void fetchPaymentMethodsAsync(AsyncOperationCallback asyncOperationCallback);

}
