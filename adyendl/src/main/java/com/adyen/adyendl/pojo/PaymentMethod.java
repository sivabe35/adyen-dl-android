package com.adyen.adyendl.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andrei on 1/14/16.
 */
public class PaymentMethod implements Parcelable {

    private static final String[] creditCardBrands = {
            "mc",
            "amex",
            "jcb",
            "diners",
            "kcp_creditcard",
            "hipercard",
            "discover",
            "elo",
            "visa",
            "unionpay",
            "maestro",
            "bcmc",
            "cartebancaire",
            "visadankort",
            "bijcard",
            "dankort",
            "uatp",
            "maestrouk",
            "accel",
            "cabal",
            "pulse",
            "star",
            "nyce",
            "hiper",
            "cu24",
            "argencard",
            "netplus",
            "shopping",
            "warehouse",
            "oasis",
            "cencosud",
            "chequedejeneur",
            "karenmillen"
    };

    private int logoId;
    private String brandCode;
    private String name;
    private String issuerId;
    private List<PaymentMethod> issuers;
    private PaymentMethodType paymentMethodType;

    public PaymentMethod() {

    }

    private PaymentMethod(Parcel in) {
        readFromParcel(in);
    }

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public List<PaymentMethod> getIssuers() {
        return issuers;
    }

    public void setIssuers(List<PaymentMethod> issuers) {
        this.issuers = issuers;
    }

    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(PaymentMethodType paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public boolean contains(String brandCode) {
        List<String> brandCodesList = Arrays.asList(creditCardBrands);
        return brandCode.contains(brandCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(logoId);
        dest.writeString(brandCode);
        dest.writeString(name);
        dest.writeString(issuerId);
        if (issuers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(issuers);
        }
    }

    private void readFromParcel(Parcel in) {
        this.logoId = in.readInt();
        this.brandCode = in.readString();
        this.name = in.readString();
        this.issuerId = in.readString();
        if (in.readByte() == 0x01) {
            issuers = new ArrayList<PaymentMethod>();
            in.readList(issuers, PaymentMethod.class.getClassLoader());
        } else {
            issuers = null;
        }
    }

    public static final Parcelable.Creator<PaymentMethod> CREATOR
            = new Parcelable.Creator<PaymentMethod>() {
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        public PaymentMethod[] newArray(int size) {
            return new PaymentMethod[size];
        }
    };

    public enum PaymentMethodType {
        CARD,
        OTHER
    }
}
