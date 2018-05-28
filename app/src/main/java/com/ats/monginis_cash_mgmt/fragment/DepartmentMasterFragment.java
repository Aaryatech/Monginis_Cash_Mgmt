package com.ats.monginis_cash_mgmt.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.adapter.DepartmentMasterAdapter;
import com.ats.monginis_cash_mgmt.bean.DepartmentList;
import com.ats.monginis_cash_mgmt.bean.DepartmentListData;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
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

import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class DepartmentMasterFragment extends Fragment implements View.OnClickListener {

    private ListView lvList;
    private EditText edName;
    private TextView tvAdd,tvCompanyName;

    ArrayList<DepartmentList> departmentLists = new ArrayList<>();
    DepartmentMasterAdapter deptAdapter;

    String dName;
    int dId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department_master, container, false);
        getActivity().setTitle("Department");

        lvList = view.findViewById(R.id.lvDeptMaster_List);
        edName = view.findViewById(R.id.edDeptMaster_Name);
        tvAdd = view.findViewById(R.id.tvDeptMaster_Add);
        tvCompanyName = view.findViewById(R.id.tvDeptMaster_CompanyName);
        tvAdd.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        try {
            dName = getArguments().getString("DeptName");
            dId = getArguments().getInt("DeptId");
            edName.setText("" + dName);
        } catch (Exception e) {
            dId = 0;
            edName.setText("");
        }

        ArrayList<String> nameArray = new ArrayList<>();
        nameArray.add("Account");
        nameArray.add("Production");
        nameArray.add("Sales");

        getAllDepartment();

        return view;
    }

    public void insertDepartment(DepartmentList departmentBean) {
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

        Call<ErrorMessage> errorMessageCall = api.insertDepartment(departmentBean);
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
                            dId = 0;
                            edName.setText("");
                            getAllDepartment();
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
                            for (int i = 0; i < data.getDepartmentList().size(); i++) {
                                departmentLists.add(data.getDepartmentList().get(i));
                            }

                            deptAdapter = new DepartmentMasterAdapter(getContext(), departmentLists);
                            lvList.setAdapter(deptAdapter);


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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvDeptMaster_Add) {
            if (edName.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Department Name", Toast.LENGTH_SHORT).show();
                edName.requestFocus();
            } else {
                String name = edName.getText().toString();
                DepartmentList bean = new DepartmentList(dId, name, name, 0, 0);
                insertDepartment(bean);
            }

        }
    }
}
