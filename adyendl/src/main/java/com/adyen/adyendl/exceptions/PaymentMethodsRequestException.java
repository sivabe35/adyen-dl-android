package com.adyen.adyendl.exceptions;

/**
 * Created by andrei on 6/6/16.
 */
public class PaymentMethodsRequestException extends Exception {

    private static final long serialVersionUID = 1811717770670999989L;

    public PaymentMethodsRequestException(String message) {
        super(message);
    }
}
