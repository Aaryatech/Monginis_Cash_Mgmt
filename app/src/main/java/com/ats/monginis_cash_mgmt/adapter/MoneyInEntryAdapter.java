package com.ats.monginis_cash_mgmt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.bean.MoneyInEntryBean;

import java.util.ArrayList;

/**
 * Created by maxadmin on 23/12/17.
 */

public class MoneyInEntryAdapter extends BaseAdapter {

    Context context;
    private ArrayList<MoneyInEntryBean> originalValues;
    private ArrayList<MoneyInEntryBean> displayedValues;
    LayoutInflater inflater;

    public MoneyInEntryAdapter(Context context, ArrayList<MoneyInEntryBean> catArray) {
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

        tvStatus.setVisibility(View.GONE);
        llEnterBy.setVisibility(View.GONE);
        tvName.setText("" + displayedValues.get(position).getUserName());
        tvAmount.setText("Rs. " + displayedValues.get(position).getInAmt());
        if (displayedValues.get(position).getInSource() == 0) {
            tvPurpose.setText("Source: Director");
        } else if (displayedValues.get(position).getInSource() == 1) {
            tvPurpose.setText("Source: Bank Withdrawal");
        }

        String dt = displayedValues.get(position).getInDate();
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


        return v;
    }
}
