package com.ats.monginis_cash_mgmt.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.bean.CashBalanceDataBean;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.MoneyInBean;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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


public class MoneyInFragment extends Fragment implements View.OnClickListener {

    private EditText edAmount, edRemark;
    private TextView tvSubmit, tvCancel, tvDate, tvRupees, tvCompanyName, tvPhysicalRupees;
    private Spinner spSource;

    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_money_in, container, false);
        getActivity().setTitle("Money In");

        SharedPreferences pref = getContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        //Log.e("User Bean : ", "---------------" + userBean);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
            }
        } catch (Exception e) {
        }

        edAmount = view.findViewById(R.id.edMoneyIn_Amount);
        edRemark = view.findViewById(R.id.edMoneyIn_Remark);
        tvSubmit = view.findViewById(R.id.tvMoneyIn_Submit);
        tvCancel = view.findViewById(R.id.tvMoneyIn_Cancel);
        spSource = view.findViewById(R.id.spMoneyIn_Source);
        tvDate = view.findViewById(R.id.tvMoneyIn_Date);
        tvRupees = view.findViewById(R.id.tvMoneyIn_Rupees);
        tvPhysicalRupees = view.findViewById(R.id.tvMoneyIn_PhysicalRupees);
        tvCompanyName = view.findViewById(R.id.tvMoneyIn_CompanyName);

        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        String currDate = dateFormat.format(cal.getTime());

        tvDate.setText("Date : " + currDate);

        ArrayList<String> sourceArray = new ArrayList<>();
        sourceArray.add("Select Source");
        sourceArray.add("Director");
        sourceArray.add("Bank Withdrawal");

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, sourceArray);
        spSource.setAdapter(sourceAdapter);

        getCashBalanceData();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvMoneyIn_Submit) {
            if (edAmount.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Amount", Toast.LENGTH_SHORT).show();
                edAmount.requestFocus();
            } else if (spSource.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Source", Toast.LENGTH_SHORT).show();
                spSource.requestFocus();
            } else if (edRemark.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Remark", Toast.LENGTH_SHORT).show();
                edRemark.requestFocus();
            } else {
                float amount = Float.parseFloat(edAmount.getText().toString());
                int sourceId = spSource.getSelectedItemPosition() - 1;
                String remark = edRemark.getText().toString();

                MoneyInBean bean = new MoneyInBean(0, amount, sourceId, userId, remark);
                //Log.e("Money In : ", "---- " + bean);
                insertTransaction(bean);

            }


        } else if (v.getId() == R.id.tvMoneyIn_Cancel) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment(), "Home");
            ft.commit();
        }
    }

    public void insertTransaction(MoneyInBean moneyInBean) {
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

        Call<ErrorMessage> errorMessageCall = api.insertMoneyIn(moneyInBean);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "" + message.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, new HomeFragment(), "Home");
                            ft.commit();
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "Transaction Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "Transaction Failed", Toast.LENGTH_SHORT).show();
                    //Log.e("Money IN : ", " Exception : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "Transaction Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_report);
        item.setVisible(true);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_report:
                Fragment adf = new MoneyInEntriesFragment();
                Bundle args = new Bundle();
                args.putInt("Id", 0);
                adf.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "HomeFragment").commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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

}
