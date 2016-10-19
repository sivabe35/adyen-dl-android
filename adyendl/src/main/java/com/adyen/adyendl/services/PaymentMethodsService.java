package com.adyen.adyendl.services;

import com.adyen.adyendl.util.AsyncOperationCallback;

/**
 * Created by andrei on 5/31/16.
 */
public interface PaymentMethodsService {

    void fetchPaymentMethods(AsyncOperationCallback asyncOperationCallback);

}
