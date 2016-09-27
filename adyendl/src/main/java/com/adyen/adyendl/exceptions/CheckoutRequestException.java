package com.adyen.adyendl.exceptions;

/**
 * Created by andrei on 12/22/15.
 */
public class CheckoutRequestException extends Exception {

    private static final long serialVersionUID = 2699577096011945290L;

    /**
     * Wrapping exception for all JCE encryption related exceptions
     * @param message
     */
    public CheckoutRequestException(String message) {
        super(message);
    }

}
