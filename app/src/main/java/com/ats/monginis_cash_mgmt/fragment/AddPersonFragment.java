package com.ats.monginis_cash_mgmt.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.activity.LoginActivity;
import com.ats.monginis_cash_mgmt.bean.DepartmentList;
import com.ats.monginis_cash_mgmt.bean.DepartmentListData;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.PersonList;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class AddPersonFragment extends Fragment implements View.OnClickListener {

    private EditText edName, edMobile, edLimit;
    private Spinner spDept;
    private TextView tvSubmit, tvCancel, tvDeptId, tvCompanyName;

    private ArrayList<DepartmentList> departmentLists = new ArrayList<>();
    private ArrayList<String> deptNameList = new ArrayList<>();
    private ArrayList<Integer> deptIdList = new ArrayList<>();

    int pId = 0, isActive = 0, deptId, company;
    String pName, pMobile;
    float limit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_person, container, false);
        getActivity().setTitle("Add Person");

        edName = view.findViewById(R.id.edAddPerson_Name);
        edMobile = view.findViewById(R.id.edAddPerson_Mobile);
        edLimit = view.findViewById(R.id.edAddPerson_Limit);
        spDept = view.findViewById(R.id.spAddPerson_Dept);
        tvSubmit = view.findViewById(R.id.tvAddPerson_Submit);
        tvCancel = view.findViewById(R.id.tvAddPerson_Cancel);
        tvDeptId = view.findViewById(R.id.tvAddPerson_DeptId);
        tvCompanyName = view.findViewById(R.id.tvAddPerson_CompanyName);

        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        try {
            pId = getArguments().getInt("PersonId");
            pName = getArguments().getString("PersonName");
            pMobile = getArguments().getString("PersonMobile");
            deptId = getArguments().getInt("PersonDeptId");
            limit = getArguments().getFloat("PersonLimit");
            isActive = getArguments().getInt("PersonISActive");

            edName.setText("" + pName);
            edMobile.setText("" + pMobile);
            edLimit.setText("" + limit);


        } catch (Exception e) {
            pId = 0;
            //Log.e("AddPerson ", " Exception : " + e.getMessage());
            e.printStackTrace();
        }

        spDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvDeptId.setText("" + deptIdList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getAllDepartment();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvAddPerson_Submit) {
            if (spDept.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Department", Toast.LENGTH_SHORT).show();
                spDept.requestFocus();
            } else if (edName.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Person Name", Toast.LENGTH_SHORT).show();
                edName.requestFocus();
            } else if (edMobile.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                edMobile.requestFocus();
            } else if (edMobile.getText().toString().length() != 10) {
                Toast.makeText(getActivity(), "Please Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                edMobile.requestFocus();
            } else if (edLimit.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Person Cash Limit", Toast.LENGTH_SHORT).show();
                edLimit.requestFocus();
            } else {
                String name = edName.getText().toString();
                String mobile = edMobile.getText().toString();
                float limit = Float.parseFloat(edLimit.getText().toString());
                int deptId = 0;
                try {
                    if (!tvDeptId.getText().toString().isEmpty()) {
                        deptId = Integer.parseInt(tvDeptId.getText().toString());
                    }
                } catch (Exception e) {
                }

                PersonList bean = new PersonList(pId, name, deptId, limit, mobile, isActive, 0);
                insertPerson(bean);

            }
        } else if (v.getId() == R.id.tvAddPerson_Cancel) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new PersonMasterFragment(), "HomeFragment");
            ft.commit();
        }
    }

    public void insertPerson(PersonList personBean) {
        final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
        commonDialog.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();

                        okhttp3.Response response = chain.proceed(request);

                        return response;
                    }
                })
                .readTimeout(10000, TimeUnit.SECONDS)
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();

        InterfaceApi api = retrofit.create(InterfaceApi.class);

        Call<ErrorMessage> errorMessageCall = api.insertPerson(personBean);
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
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, new PersonMasterFragment(), "HomeFragment");
                            ft.commit();
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

    public void getAllDepartment() {
        final CommonDialog commonDialog = new CommonDialog(getContext(), "Loading", "Please Wait...");
        commonDialog.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();

                        okhttp3.Response response = chain.proceed(request);

                        return response;
                    }
                })
                .readTimeout(10000, TimeUnit.SECONDS)
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();

        InterfaceApi api = retrofit.create(InterfaceApi.class);


        Call<DepartmentListData> departmentListDataCall = api.getAllDepartmentList();
        departmentListDataCall.enqueue(new Callback<DepartmentListData>() {
            @Override
            public void onResponse(Call<DepartmentListData> call, Response<DepartmentListData> response) {
                try {
                    if (response.body() != null) {
                        DepartmentListData data = response.body();
                        if (data.getErrorMessage().getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "No Departments Found", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            departmentLists.clear();
                            deptNameList.clear();
                            deptIdList.clear();
                            deptNameList.add("Select Department");
                            deptIdList.add(0);

                            for (int i = 0; i < data.getDepartmentList().size(); i++) {
                                departmentLists.add(data.getDepartmentList().get(i));
                                deptIdList.add(data.getDepartmentList().get(i).getDeptId());
                                deptNameList.add(data.getDepartmentList().get(i).getDeptName());
                            }

                            ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, deptNameList);
                            spDept.setAdapter(deptAdapter);
                            int pos = 0;
                            for (int i = 0; i < deptIdList.size(); i++) {
                                if (deptId == deptIdList.get(i)) {
                                    pos = i;
                                }
                            }
                            spDept.setSelection(pos);


                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "No Departments Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "No Departments Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DepartmentListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No Departments Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
