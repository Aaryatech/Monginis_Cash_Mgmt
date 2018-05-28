package com.ats.monginis_cash_mgmt.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.adapter.CompanyMasterAdapter;
import com.ats.monginis_cash_mgmt.bean.CompanyListData;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyMasterFragment extends Fragment implements View.OnClickListener {

    private ListView lvList;
    private EditText edName, edCashBal;
    private TextView tvAdd,tvCompanyName;
    CompanyMasterAdapter compAdapter;
    String cName;
    int cId = 0;
    float cBal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_master, container, false);
        getActivity().setTitle("Company");

        lvList = view.findViewById(R.id.lvCompanyMaster_List);
        edName = view.findViewById(R.id.edCompanyMaster_Name);
        edCashBal = view.findViewById(R.id.edCompanyMaster_CashBal);
        tvAdd = view.findViewById(R.id.tvCompanyMaster_Add);
        tvAdd.setOnClickListener(this);

        try {
            cName = getArguments().getString("CompName");
            cBal = getArguments().getFloat("CompBal");
            cId = getArguments().getInt("CompId");
            Log.e("Name : ", "--- " + cName);
            Log.e("Bal : ", "--- " + cBal);
            Log.e("Id : ", "--- " + cId);
            if (cBal <= 0) {
                edCashBal.setText("");
            } else {
                edCashBal.setText("" + cBal);
            }
            edName.setText("" + cName);
        } catch (Exception e) {
            Log.e("Exception : ", "--- " + e.getMessage());
            cId = 0;
            edCashBal.setText("");
            edName.setText("");
        }

        ArrayList<String> nameArray = new ArrayList<>();
        nameArray.add("Monginis Aurangabad Pvt.Ltd.");

      //  getAllCompany();

        return view;
    }

   /* public void insertCompany(List companyBean) {
        final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
        commonDialog.show();

        Call<ErrorMessage> errorMessageCall = Constants.myInterface.insertCompany(companyBean);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            getAllCompany();
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllCompany() {
        final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
        commonDialog.show();

        Call<CompanyListData> companyListDataCall = Constants.myInterface.getAllCompanyList();
        companyListDataCall.enqueue(new Callback<CompanyListData>() {
            @Override
            public void onResponse(Call<CompanyListData> call, Response<CompanyListData> response) {
                try {
                    if (response.body() != null) {
                        CompanyListData data = response.body();
                        if (data.getErrorMessage().getError()) {
                            commonDialog.dismiss();
                            Log.e("CompanyMaster : ", " : ERROR : " + data.getErrorMessage().getMessage());
                            Toast.makeText(getActivity(), "No Company Found", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            companyListArray.clear();
                            for (int i = 0; i < data.getList().size(); i++) {
                                companyListArray.add(data.getList().get(i));
                            }

                            compAdapter = new CompanyMasterAdapter(getContext(), companyListArray);
                            lvList.setAdapter(compAdapter);
                        }
                    } else {
                        commonDialog.dismiss();
                        Log.e("CompanyMaster : ", " : NULL");
                        Toast.makeText(getActivity(), "No Company Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("CompanyMaster : ", "Exception : " + e.getMessage());
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "No Company Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CompanyListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No Company Found", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCompanyMaster_Add) {
            if (edName.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Company Name", Toast.LENGTH_SHORT).show();
                edName.requestFocus();
            } else if (edCashBal.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Cash Balance", Toast.LENGTH_SHORT).show();
                edCashBal.requestFocus();
            } else {
                String name = edName.getText().toString();
                float bal = Float.parseFloat(edCashBal.getText().toString());
              //  List bean = new List(cId, name, name, bal, 0, 0);
             //   insertCompany(bean);
            }
        }
    }
}
