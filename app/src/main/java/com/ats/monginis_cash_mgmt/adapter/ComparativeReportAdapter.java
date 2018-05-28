package com.ats.monginis_cash_mgmt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;

import java.util.ArrayList;

/**
 * Created by maxadmin on 18/12/17.
 */

public class ComparativeReportAdapter extends BaseAdapter {


    Context context;
    private ArrayList<String> originalValues;
    private ArrayList<String> displayedValues;
    LayoutInflater inflater;

    public ComparativeReportAdapter(Context context, ArrayList<String> catArray) {
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
        v = inflater.inflate(R.layout.custom_report_layout, null);

        TextView tvName = v.findViewById(R.id.tvCustomReport_Name);
        TextView tvAmtType = v.findViewById(R.id.tvCustomReport_Type);

        tvName.setText("" + displayedValues.get(position));
        if (position % 2 == 0) {
            tvAmtType.setText("Cr");
        } else {
            tvAmtType.setText("Dr");
        }

        return v;
    }
}
