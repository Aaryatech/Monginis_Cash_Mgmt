package com.ats.monginis_cash_mgmt.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.ViewImageActivity;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by maxadmin on 18/12/17.
 */

public class ApprovalListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<GetMoneyOutData> originalValues;
    private ArrayList<GetMoneyOutData> displayedValues;
    LayoutInflater inflater;
    int cbStatus;

    public ApprovalListAdapter(Context context, ArrayList<GetMoneyOutData> catArray, int cbStatus) {
        this.context = context;
        this.originalValues = catArray;
        this.displayedValues = catArray;
        inflater = LayoutInflater.from(context);
        this.cbStatus = cbStatus;
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
        v = inflater.inflate(R.layout.custom_approval_layout, null);

        LinearLayout llHead = v.findViewById(R.id.llCustomApproval_Head);
        final LinearLayout llData = v.findViewById(R.id.llCustomApproval_Data);
        TextView tvName = v.findViewById(R.id.tvCustomApproval_Name);
        TextView tvDate = v.findViewById(R.id.tvCustomApproval_Date);
        final CheckBox cbCheck = v.findViewById(R.id.cbCustomApproval);
        TextView tvReject = v.findViewById(R.id.tvCustomApproval_Reject);
        TextView tvGivenAmt = v.findViewById(R.id.tvCustomApproval_GivenAmt);
        TextView tvReturnAmt = v.findViewById(R.id.tvCustomApproval_ReturnAmt);
        ImageView ivPhoto = v.findViewById(R.id.ivCustomApproval_Img);

        tvName.setText("" + displayedValues.get(position).getPersonName());

        String dt = displayedValues.get(position).getOutDate();
        String yr = dt.substring(0, 4);
        String mn = dt.substring(5, 7);
        String dy = dt.substring(8);
        tvDate.setText(dy + "/" + mn + "/" + yr);

        tvGivenAmt.setText("Given Amount: " + displayedValues.get(position).getOutAmt());
        tvReturnAmt.setText("Return Amount: " + displayedValues.get(position).getReturnAmt());

        String image=displayedValues.get(position).getBillPhoto();
        try {

            Picasso.with(context)
                    .load(image)
                    .placeholder(R.drawable.default_img)
                    .error(R.drawable.default_img)
                    .into(ivPhoto);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ApprovalAdapter : ", " Image : " + e.getMessage());
        }


        if (cbStatus == 1) {
            cbCheck.setChecked(true);
        } else {
            cbCheck.setChecked(false);
        }

        llHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llData.getVisibility() == View.GONE) {
                    llData.setVisibility(View.VISIBLE);
                } else if (llData.getVisibility() == View.VISIBLE) {
                    llData.setVisibility(View.GONE);
                }
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ViewImageActivity.class));
            }
        });

        tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
                dialog.setContentView(R.layout.custom_reject_transaction);

                final TextView btnCancle = dialog.findViewById(R.id.tvCustomReject_Cancel);
                final TextView btnReject = dialog.findViewById(R.id.tvCustomReject_Rej);
                final EditText edRemark = dialog.findViewById(R.id.edCustomReject_RejRemark);
                final EditText edExpAmt = dialog.findViewById(R.id.edCustomReject_ExpAmt);

                btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edExpAmt.getText().toString().isEmpty()) {
                            edExpAmt.setError("Required");
                            edExpAmt.requestFocus();
                        } else if (edRemark.getText().toString().isEmpty()) {
                            edRemark.setError("Required");
                            edRemark.requestFocus();
                        } else {

                            dialog.dismiss();
                        }
                    }
                });

                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return v;
    }

}
