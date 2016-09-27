package com.adyen.adyendl.services;

import org.json.JSONObject;

/**
 * Created by andrei on 5/20/16.
 */
public interface RegisterMerchantServerService {

    JSONObject fetchMerchantSignature();

}
