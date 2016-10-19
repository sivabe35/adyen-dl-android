package com.adyen.dl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.adyen.adyendl.PaymentsProcessor;
import com.adyen.adyendl.pojo.Configuration;
import com.adyen.adyendl.pojo.Payment;
import com.adyen.adyendl.pojo.PaymentMethod;
import com.adyen.adyendl.util.AsyncOperationCallback;
import com.adyen.adyendl.util.Environment;
import com.adyen.dl.adapter.PaymentMethodsExpandableListAdapter;

import java.util.ArrayList;


/**
 * Created by andrei on 9/13/16.
 */
public class PaymentMethodsActivity extends Activity {

    private static final String tag = PaymentMethodsActivity.class.getSimpleName();

    private ExpandableListView mPaymentMethodsExpandableListView;
    private PaymentMethodsExpandableListAdapter mPaymentMethodsExpandableListAdapter;

    private ArrayList<PaymentMethod> mPaymentMethods;

    private Configuration configuration;
    private Payment payment;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        Intent resultIntent = getIntent();
        mPaymentMethods = resultIntent.getParcelableArrayListExtra("paymentMethods");

        mPaymentMethodsExpandableListView = (ExpandableListView)findViewById(R.id.paymentMethodsListView);
        mPaymentMethodsExpandableListAdapter = new PaymentMethodsExpandableListAdapter(this, mPaymentMethods);
        mPaymentMethodsExpandableListView.setAdapter(mPaymentMethodsExpandableListAdapter);

        fillPaymentMethodsRequest();

        context = this;

        mPaymentMethodsExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                PaymentMethod paymentMethod = mPaymentMethods.get(groupPosition);
                if(paymentMethod.getIssuers() != null && paymentMethod.getIssuers().size() > 0) {
                    mPaymentMethodsExpandableListView.expandGroup(groupPosition);
                } else {
                    PaymentsProcessor.getInstance().fetchRedirectUrl(configuration, payment, paymentMethod.getBrandCode(), null, new AsyncOperationCallback() {
                        @Override
                        public void onSuccess(String redirectUrlStr) {
                            Intent intent = new Intent(context, PaymentRedirectActivity.class);
                            intent.putExtra("redirectUrl", redirectUrlStr);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable e, String errorMessage) {
                            Log.e(tag, errorMessage, e);
                        }
                    });
                }
                return false;
            }
        });

        mPaymentMethodsExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                PaymentMethod paymentMethod = mPaymentMethods.get(groupPosition);
                if(paymentMethod.getIssuers() != null && paymentMethod.getIssuers().size() > 0) {
                    PaymentMethod issuer = paymentMethod.getIssuers().get(childPosition);
                    PaymentsProcessor.getInstance().fetchRedirectUrl(configuration, payment, paymentMethod.getBrandCode(), issuer.getIssuerId(), new AsyncOperationCallback() {
                        @Override
                        public void onSuccess(String redirectUrlStr) {
                            Intent intent = new Intent(context, PaymentRedirectActivity.class);
                            intent.putExtra("redirectUrl", redirectUrlStr);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable e, String errorMessage) {
                            Log.e(tag, errorMessage, e);
                        }
                    });
                }
                return false;
            }
        });
    }

    private void fillPaymentMethodsRequest() {
        configuration = new Configuration(Environment.LIVE, "http://www.mozuma.nl/adyen/api.php", null, null);
        payment = new Payment();

        payment.setMerchantReference("Reference");
        payment.setCountryCode("NL");
        payment.setCurrency("EUR");
        payment.setAmount(199);
    }
}
