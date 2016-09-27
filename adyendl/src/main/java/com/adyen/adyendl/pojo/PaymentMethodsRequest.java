package com.adyen.adyendl.pojo;

/**
 * Created by andrei on 6/1/16.
 */
public class PaymentMethodsRequest {

    private String merchantAccount;
    private String merchantReference;
    private String shopperIP;
    private String sessionValidity;
    private String shipBeforeDate;
    private String countryCode;
    private String shopperLocale;
    private String getHtml;
    private String amountCurrency;
    private String amountValue;


    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getShopperIP() {
        return shopperIP;
    }

    public void setShopperIP(String shopperIP) {
        this.shopperIP = shopperIP;
    }

    public String getSessionValidity() {
        return sessionValidity;
    }

    public void setSessionValidity(String sessionValidity) {
        this.sessionValidity = sessionValidity;
    }

    public String getShipBeforeDate() {
        return shipBeforeDate;
    }

    public void setShipBeforeDate(String shipBeforeDate) {
        this.shipBeforeDate = shipBeforeDate;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getShopperLocale() {
        return shopperLocale;
    }

    public void setShopperLocale(String shopperLocale) {
        this.shopperLocale = shopperLocale;
    }

    public String getGetHtml() {
        return getHtml;
    }

    public void setGetHtml(String getHtml) {
        this.getHtml = getHtml;
    }

    public String getAmountCurrency() {
        return amountCurrency;
    }

    public void setAmountCurrency(String amountCurrency) {
        this.amountCurrency = amountCurrency;
    }

    public String getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(String amountValue) {
        this.amountValue = amountValue;
    }
}
