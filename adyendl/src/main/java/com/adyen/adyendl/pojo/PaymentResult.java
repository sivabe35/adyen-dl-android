package com.adyen.adyendl.pojo;

/**
 * Created by andrei on 9/16/16.
 */
public class PaymentResult {

    private enum PaymentStatus {
        AUTHORISED,
        REFUSED,
        CANCELLED,
        PENDING,
        ERROR
    }

    private Payment payment;
    private PaymentStatus paymentStatus;

    public PaymentResult(Payment payment, PaymentStatus paymentStatus) {
        this.payment = payment;
        this.paymentStatus = paymentStatus;
    }

}
