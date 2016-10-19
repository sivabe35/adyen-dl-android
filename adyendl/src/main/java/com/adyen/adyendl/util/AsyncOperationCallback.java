package com.adyen.adyendl.util;

/**
 * Created by andrei on 10/18/16.
 */
public interface AsyncOperationCallback {

    void onSuccess(String response);

    void onFailure(Throwable e, String errorMessage);

}
