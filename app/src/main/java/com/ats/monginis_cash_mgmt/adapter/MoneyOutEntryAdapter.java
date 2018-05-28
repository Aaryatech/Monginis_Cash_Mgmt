package com.ats.monginis_cash_mgmt.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.activity.LoginActivity;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.fragment.EditMoneyOutFragment;
import com.ats.monginis_cash_mgmt.fragment.TransactionSettlementFragment;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maxadmin on 25/12/17.
 */

public class MoneyOutEntryAdapter extends BaseAdapter {

    Context context;
    private ArrayList<GetMoneyOutData> originalValues;
    private ArrayList<GetMoneyOutData> displayedValues;
    LayoutInflater inflater;

    public MoneyOutEntryAdapter(Context context, ArrayList<GetMoneyOutData> catArray) {
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
        final LinearLayout llEnterBy = v.findViewById(R.id.llCustomTranscSettle_EnterBy);
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
        TextView tvApproveStatus = v.findViewById(R.id.tvCustomTranscSettle_ApproveStatus);
        TextView tvEdit = v.findViewById(R.id.tvCustomTranscSettle_Edit);
        LinearLayout llButtonPanel = v.findViewById(R.id.llCustomTranscSettle_Button);

        tvApproveStatus.setVisibility(View.VISIBLE);

        int userId = 0;
        SharedPreferences pref = context.getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
            }

        } catch (Exception e) {
        }

        if (displayedValues.get(position).getIsApproved() == 0) {
            tvApproveStatus.setText("Pending");
            if (displayedValues.get(position).getOutBy() == userId) {
                tvEdit.setVisibility(View.VISIBLE);
            }
        } else if (displayedValues.get(position).getIsApproved() == 2) {
            tvApproveStatus.setText("Rejected");
            tvApproveStatus.setTextColor(Color.RED);
            tvEdit.setVisibility(View.INVISIBLE);
        } else if (displayedValues.get(position).getIsApproved() == 1) {
            tvApproveStatus.setText("Approved");
            tvApproveStatus.setTextColor(Color.parseColor("#43a472"));
            llButtonPanel.setVisibility(View.GONE);
        }


        tvStatus.setVisibility(View.INVISIBLE);
        // llEnterBy.setVisibility(View.GONE);
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

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPreferences pref = context.getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                Gson gson = new Gson();
//                String json = gson.toJson(displayedValues.get(position));
//                editor.putString("moneyOutEntry", json);
//                editor.apply();

                Gson gson = new Gson();
                String json = gson.toJson(displayedValues.get(position));
                Bundle bundle = new Bundle();
                bundle.putString("moneyOutEntry", json);
                HomeActivity activity = (HomeActivity) context;
                Fragment fragment = new EditMoneyOutFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "MoneyOutEntry");
                ft.commit();

            }
        });

        return v;
    }
}
