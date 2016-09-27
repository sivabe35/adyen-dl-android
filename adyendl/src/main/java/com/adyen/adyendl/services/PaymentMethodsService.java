package com.adyen.adyendl.services;

import org.json.JSONObject;

/**
 * Created by andrei on 5/31/16.
 */
public interface PaymentMethodsService {

    JSONObject fetchPaymentMethods();

}
