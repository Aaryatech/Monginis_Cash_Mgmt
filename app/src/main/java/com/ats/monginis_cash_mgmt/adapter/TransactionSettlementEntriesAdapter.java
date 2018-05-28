package com.ats.monginis_cash_mgmt.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.TransactionSettleStatusActivity;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by maxadmin on 18/12/17.
 */

public class TransactionSettlementEntriesAdapter extends BaseAdapter {

    Context context;
    private ArrayList<GetMoneyOutData> originalValues;
    private ArrayList<GetMoneyOutData> displayedValues;
    LayoutInflater inflater;

    public TransactionSettlementEntriesAdapter(Context context, ArrayList<GetMoneyOutData> catArray) {
        this.context = context;
        this.originalValues = catArray;
        this.displayedValues = catArray;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return displayedValues.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.custom_transc_settle_entries_layout, null);

        LinearLayout llHead = v.findViewById(R.id.llCustomTranscSettle_Head);
        final LinearLayout llData = v.findViewById(R.id.llCustomTranscSettle_Data);
        final LinearLayout llForm = v.findViewById(R.id.llCustomTranscSettle_Form);
        final LinearLayout llUpload = v.findViewById(R.id.llCustomTranscSettle_Upload);
        TextView tvName = v.findViewById(R.id.tvCustomTranscSettle_Name);
        TextView tvAmount = v.findViewById(R.id.tvCustomTranscSettle_Amount);
        TextView tvDate = v.findViewById(R.id.tvCustomTranscSettle_Date);
        TextView tvStatus = v.findViewById(R.id.tvCustomTranscSettle_Status);
        EditText edReturnAmt = v.findViewById(R.id.edCustomTranscSettle_ReturnAmt);
        EditText edTotalAmt = v.findViewById(R.id.edCustomTranscSettle_TotalAmount);
        EditText edReason = v.findViewById(R.id.edCustomTranscSettle_Reason);
        Spinner spBill = v.findViewById(R.id.spCustomTranscSettle_Bill);
        TextView tvUploadBill = v.findViewById(R.id.tvCustomTranscSettle_UploadBill);
        TextView tvBillName = v.findViewById(R.id.tvCustomTranscSettle_BillName);
        TextView tvSubmit = v.findViewById(R.id.tvCustomTranscSettle_Submit);
        TextView tvCancel = v.findViewById(R.id.tvCustomTranscSettle_Status);
        final TextView tvIcon = v.findViewById(R.id.tvCustomTranscSettle_Icon);
        TextView tvPurpose = v.findViewById(R.id.tvCustomTranscSettle_Purpose);
        TextView tvEnterBy = v.findViewById(R.id.tvCustomTranscSettle_EnterBy);
        TextView tvEdit = v.findViewById(R.id.tvCustomTranscSettle_Edit);

        tvName.setText("" + displayedValues.get(position).getPersonName());
        tvAmount.setText("Rs. " + displayedValues.get(position).getOutAmt());
        tvPurpose.setText("Purpose: " + displayedValues.get(position).getPurpose());
        tvEnterBy.setText("Enter By: " + displayedValues.get(position).getUserName());

        String dt = displayedValues.get(position).getOutDate();
        String yr = dt.substring(0, 4);
        String mn = dt.substring(5, 7);
        String dy = dt.substring(8);
        tvDate.setText(dy + "/" + mn + "/" + yr);

        llHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llData.getVisibility() == View.GONE) {
                    tvIcon.setText(R.string.icon_minus);
                    llData.setVisibility(View.VISIBLE);
                } else if (llData.getVisibility() == View.VISIBLE) {
                    llData.setVisibility(View.GONE);
                    llForm.setVisibility(View.GONE);
                    tvIcon.setText(R.string.icon_add);

                }
            }
        });

        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
//                context.startActivity(new Intent(context, TransactionSettleStatusActivity.class));
                Intent intent = new Intent(context, TransactionSettleStatusActivity.class);
                Bundle bundle = new Bundle();
                String json = gson.toJson(displayedValues.get(position));
                bundle.putString("TransactionBean", json);
                intent.putExtras(bundle);
                context.startActivity(intent);
/*
                if (llForm.getVisibility() == View.GONE) {
                    llForm.setVisibility(View.VISIBLE);
                } else if (llForm.getVisibility() == View.VISIBLE) {
                    llForm.setVisibility(View.GONE);
                }
*/
            }
        });

        ArrayList<String> billStatus = new ArrayList<>();
        billStatus.add("Yes");
        billStatus.add("No");

        ArrayAdapter spAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, billStatus);
        spBill.setAdapter(spAdapter);

        spBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    llUpload.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    llUpload.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

}
