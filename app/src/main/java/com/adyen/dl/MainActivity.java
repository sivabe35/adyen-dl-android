package com.adyen.dl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.adyen.adyendl.PaymentsProcessor;
import com.adyen.adyendl.exceptions.PaymentMethodsRequestException;
import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.pojo.PaymentMethod;
import com.adyen.adyendl.services.PaymentMethodServiceImpl;
import com.adyen.adyendl.util.AsyncOperationCallback;
import com.adyen.adyendl.util.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final String tag = MainActivity.class.getSimpleName();

    private InitPaymentFragment mInitPaymentFragment;

    private Context context;

    private Configuration configuration;
    private Payment payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        mInitPaymentFragment = new InitPaymentFragment();

        initView();
    }

    private void initView() {
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_container, mInitPaymentFragment).
                addToBackStack(null).
                commit();
    }

    public void initPayment(View view) {
        fillPaymentMethodsRequest();


        PaymentMethodServiceImpl paymentMethodService = new PaymentMethodServiceImpl(configuration, payment);
        paymentMethodService.fetchPaymentMethodsAsync(new AsyncOperationCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i("=======> ", response);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        JSONObject paymentMethodsJSON = PaymentsProcessor.getInstance().fetchPaymentMethods(configuration, payment);
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        try {
            paymentMethods = jsonPaymentMethodsToArray(paymentMethodsJSON);
        } catch (PaymentMethodsRequestException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        }

        Log.i(tag, "Payment methods size: " + paymentMethods.size());

        Intent intent = new Intent(this, PaymentMethodsActivity.class);
        if(paymentMethods.size() > 0) {
            intent.putParcelableArrayListExtra("paymentMethods", paymentMethods);
        }
        startActivity(intent);
    }

    private void fillPaymentMethodsRequest() {
        configuration = new Configuration(Environment.LIVE, "http://www.mozuma.nl/adyen/api.php", null, null);
        payment = new Payment();

        payment.setMerchantReference("Reference");
        payment.setCountryCode("NL");
        payment.setCurrency("EUR");
        payment.setAmount(199);
    }

    private ArrayList<PaymentMethod> jsonPaymentMethodsToArray(JSONObject paymentMethodsJson) throws PaymentMethodsRequestException, JSONException {
        if(paymentMethodsJson.isNull("paymentMethods")) {
            throw new PaymentMethodsRequestException("No payment methods received from the server");
        }

        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();

        JSONArray paymentMethodsJsonArray = paymentMethodsJson.getJSONArray("paymentMethods");
        for(int i=0; i<paymentMethodsJsonArray.length(); i++) {
            PaymentMethod paymentMethod = new PaymentMethod();
            JSONObject paymentMethodJson = (JSONObject)paymentMethodsJsonArray.get(i);
            paymentMethod.setBrandCode(paymentMethodJson.getString("brandCode"));
            paymentMethod.setName(paymentMethodJson.getString("name"));
            if(paymentMethod.contains(paymentMethodJson.getString("brandCode"))) {
                paymentMethod.setPaymentMethodType(PaymentMethod.PaymentMethodType.CARD);
            } else {
                paymentMethod.setPaymentMethodType(PaymentMethod.PaymentMethodType.OTHER);
            }
            List<PaymentMethod> issuers = new ArrayList<>();
            if(!paymentMethodJson.isNull("issuers")) {
                JSONArray issuersJsonArray = paymentMethodJson.getJSONArray("issuers");
                for(int j=0; j<issuersJsonArray.length(); j++) {
                    PaymentMethod issuer = new PaymentMethod();
                    JSONObject issuerJson = (JSONObject)issuersJsonArray.get(j);
                    issuer.setIssuerId(issuerJson.getString("issuerId"));
                    issuer.setName(issuerJson.getString("name"));
                    issuers.add(issuer);
                }
            }
            if(issuers.size() > 0) {
                paymentMethod.setIssuers(issuers);
            }
            paymentMethods.add(paymentMethod);
        }

        return paymentMethods;
    }

}
