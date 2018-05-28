package com.ats.monginis_cash_mgmt.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.bean.CashBalanceDataBean;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;

import java.io.IOException;
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

public class CashBalanceFragment extends Fragment implements View.OnClickListener {

    private TextView tvRupees, tvCompanyName, tvMoneyOut, tvTrSettle, tvPhysicalRupees;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_balance, container, false);
        getActivity().setTitle("Cash Balance");

        tvRupees = view.findViewById(R.id.tvCashBalance_Rupees);
        tvPhysicalRupees = view.findViewById(R.id.tvCashBalance_PhysicalRupees);
        tvCompanyName = view.findViewById(R.id.tvCashBal_CompanyName);
        tvMoneyOut = view.findViewById(R.id.tvCashBal_MoneyOut);
        tvTrSettle = view.findViewById(R.id.tvCashBal_TrSettle);
        tvTrSettle.setOnClickListener(this);
        tvMoneyOut.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        getCashBalanceData();

        return view;
    }

    public void getCashBalanceData() {
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

        Call<CashBalanceDataBean> dataCall = api.getCashBalance();
        dataCall.enqueue(new Callback<CashBalanceDataBean>() {
            @Override
            public void onResponse(Call<CashBalanceDataBean> call, Response<CashBalanceDataBean> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();

                        CashBalanceDataBean data = response.body();
                        tvRupees.setText("Rs. " + String.format("%.2f", data.getOpBalance()));
                        tvPhysicalRupees.setText("Rs. " + String.format("%.2f", data.getPhysicalCash()));


                    } else {
                        commonDialog.dismiss();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CashBalanceDataBean> call, Throwable t) {
                commonDialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvCashBal_MoneyOut) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutFragment(), "HomeFragment");
            ft.commit();
        } else if (view.getId() == R.id.tvCashBal_TrSettle) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TransactionSettlementFragment(), "HomeFragment");
            ft.commit();
        }
    }
}
