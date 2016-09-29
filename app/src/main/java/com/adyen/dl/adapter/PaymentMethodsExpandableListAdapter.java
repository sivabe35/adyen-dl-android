package com.adyen.dl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.adyen.adyendl.pojo.PaymentMethod;
import com.adyen.dl.R;

import java.util.List;


/**
 * Created by andrei on 9/14/16.
 */
public class PaymentMethodsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<PaymentMethod> paymentMethodsList;

    public PaymentMethodsExpandableListAdapter(Context context, List<PaymentMethod> paymentMethodsList) {
        this.context = context;
        this.paymentMethodsList = paymentMethodsList;
    }

    @Override
    public int getGroupCount() {
        return this.paymentMethodsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(this.paymentMethodsList.get(groupPosition).getIssuers() != null) {
            return this.paymentMethodsList.get(groupPosition).getIssuers().size();
        } else {
            return 0;
        }

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.paymentMethodsList.get(groupPosition).getName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(getChildrenCount(groupPosition) > 0) {
            return this.paymentMethodsList.get(groupPosition).getIssuers().get(childPosition).getName();
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String paymentMethodText = (String)getGroup(groupPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.payment_methods_list_group, null);
        }
        TextView paymentMethodNameTextView = (TextView)convertView.findViewById(R.id.paymentMethodName);
        paymentMethodNameTextView.setText(paymentMethodText);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String paymentSubmethodText = (String)getChild(groupPosition, childPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.payment_methods_list_item, null);
        }
        TextView paymentSubmethodNameTextView = (TextView)convertView.findViewById(R.id.paymentSubmethodName);
        if(paymentSubmethodText != null) {
            paymentSubmethodNameTextView.setText(paymentSubmethodText);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if(getChildrenCount(groupPosition) > 0) {
            return true;
        } else {
            return false;
        }
    }
}
