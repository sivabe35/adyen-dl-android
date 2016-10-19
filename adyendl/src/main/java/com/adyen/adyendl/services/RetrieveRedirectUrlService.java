package com.adyen.adyendl.services;

import com.adyen.adyendl.util.AsyncOperationCallback;

/**
 * Created by andrei on 9/15/16.
 */
public interface RetrieveRedirectUrlService {

    void fetchRedirectUrl(AsyncOperationCallback asyncOperationCallback);

}
