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
import com.ats.monginis_cash_mgmt.adapter.PurposeMasterAdapter;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.PurposeList;
import com.ats.monginis_cash_mgmt.bean.PurposeListData;
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

public class PurposeMasterFragment extends Fragment implements View.OnClickListener {

    private ListView lvList;
    private EditText edName;
    private TextView tvAdd,tvCompanyName;

    private ArrayList<PurposeList> purposeArray = new ArrayList<>();
    PurposeMasterAdapter purposeAdapter;

    String pName;
    int pId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purpose_master, container, false);
        getActivity().setTitle("Purpose");

        lvList = view.findViewById(R.id.lvPurposeMaster_List);
        edName = view.findViewById(R.id.edPurposeMaster_Name);
        tvAdd = view.findViewById(R.id.tvPurposeMaster_Add);
        tvCompanyName = view.findViewById(R.id.tvPurposeMaster_CompanyName);
        tvAdd.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        try {
            pId = getArguments().getInt("PurposeId");
            pName = getArguments().getString("PurposeName");
            edName.setText("" + pName);
        } catch (Exception e) {
            pId = 0;
            edName.setText("");
        }

        getPurposeList();
        return view;
    }

    public void insertPurpose(PurposeList purposeBean) {
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

        Call<ErrorMessage> errorMessageCall = api.insertPurpose(purposeBean);
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
                            pId = 0;
                            edName.setText("");
                            getPurposeList();
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

    public void getPurposeList() {
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

        Call<PurposeListData> purposeListDataCall = api.getAllPurposeList();
        purposeListDataCall.enqueue(new Callback<PurposeListData>() {
            @Override
            public void onResponse(Call<PurposeListData> call, Response<PurposeListData> response) {
                try {
                    if (response.body() != null) {
                        PurposeListData data = response.body();
                        if (data.getErrorMessage().getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            purposeArray.clear();
                            for (int i = 0; i < data.getPurposeList().size(); i++) {
                                purposeArray.add(data.getPurposeList().get(i));
                            }

                            purposeAdapter = new PurposeMasterAdapter(getContext(), purposeArray);
                            lvList.setAdapter(purposeAdapter);
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurposeListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvPurposeMaster_Add) {
            if (edName.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Purpose", Toast.LENGTH_SHORT).show();
                edName.requestFocus();
            } else {
                String name = edName.getText().toString();
                PurposeList bean = new PurposeList(pId, name, 0, 0);
                insertPurpose(bean);
            }
        }
    }
}
