package com.adyen.adyendl.services;

import com.adyen.adyendl.util.AsyncOperationCallback;

/**
 * Created by andrei on 5/20/16.
 */
public interface RegisterMerchantServerService {

    void fetchMerchantSignature(AsyncOperationCallback asyncOperationCallback);

}
