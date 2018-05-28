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
import com.ats.monginis_cash_mgmt.bean.MoneyOutBean;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
import com.ats.monginis_cash_mgmt.bean.PurposeListData;
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

public class MoneyOutFragment extends Fragment implements View.OnClickListener {

    private EditText edAmount, edSpecify;
    private Spinner spPerson, spDept, spPurpose;
    private TextView tvSubmit, tvCancel, tvDate, tvRupees, tvCompanyName, tvPhysicalRupees;

    private ArrayList<String> purposeNameArray = new ArrayList<>();
    private ArrayList<Integer> purposeIdArray = new ArrayList<>();

    private ArrayList<String> personNameArray = new ArrayList<>();
    private ArrayList<Integer> personIdArray = new ArrayList<>();
    private ArrayList<Integer> deptIdArray = new ArrayList<>();

    int userId, userType;
    float cashBal = 0, physicalCash = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_money_out, container, false);
        getActivity().setTitle("Money Out");

        edAmount = view.findViewById(R.id.edMoneyOut_Amount);
        edSpecify = view.findViewById(R.id.edMoneyOut_Specify);
        spPerson = view.findViewById(R.id.spMoneyOut_Person);
        spPurpose = view.findViewById(R.id.spMoneyOut_Purpose);
        spDept = view.findViewById(R.id.spMoneyOut_Dept);
        tvSubmit = view.findViewById(R.id.tvMoneyOut_Submit);
        tvCancel = view.findViewById(R.id.tvMoneyOut_Cancel);
        tvDate = view.findViewById(R.id.tvMoneyOut_Date);
        tvRupees = view.findViewById(R.id.tvMoneyOut_Rupees);
        tvPhysicalRupees = view.findViewById(R.id.tvMoneyOut_PhysicalRupees);
        tvCompanyName = view.findViewById(R.id.tvMoneyOut_CompanyName);

        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        String currDate = dateFormat.format(cal.getTime());

        tvDate.setText("Date : " + currDate);

        SharedPreferences pref = getContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        Log.e("User Bean : ", "---------------" + userBean);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
                userType = userBean.getUserType();
            }
        } catch (Exception e) {
        }


        getPersonList();
        getPurposeList();
        getCashBalanceData();

        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tvMoneyOut_Submit) {
            if (spPerson.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Person", Toast.LENGTH_SHORT).show();
                spPerson.requestFocus();
            } else if (edAmount.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Amount", Toast.LENGTH_SHORT).show();
                edAmount.requestFocus();
            } else if (spPurpose.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Purpose", Toast.LENGTH_SHORT).show();
                spPurpose.requestFocus();
            } else if (edSpecify.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Specific Remark", Toast.LENGTH_SHORT).show();
                edSpecify.requestFocus();
            } else {
                int person = personIdArray.get(spPerson.getSelectedItemPosition());
                int purpose = purposeIdArray.get(spPurpose.getSelectedItemPosition());
                int dept = deptIdArray.get(spPerson.getSelectedItemPosition());
                float amt = Float.parseFloat(edAmount.getText().toString());
                String remark = edSpecify.getText().toString();

                if (amt > physicalCash) {
                    Toast.makeText(getActivity(), "Amount Cannot Be Greater Than Physical Cash Balance", Toast.LENGTH_SHORT).show();
                    edAmount.requestFocus();
                } else {
                    // MoneyOutBean bean = new MoneyOutBean(0, "", "", amt, person, dept, purpose, remark, userId, 0);

                    MoneyOutBean bean1 = new MoneyOutBean(0, "", "", amt, person, dept, purpose, remark, userId, 0, 0, "0000-00-00", 0, "", 0, "", 0, "0000-00-00", "0000-00-00 00:00:00", 0, "", 0, 0, 0);
                    insertTransaction(bean1);
                }


            }


        } else if (v.getId() == R.id.tvMoneyOut_Cancel) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TransactionSettlementFragment(), "HomeFragment");
            ft.commit();
        }

    }

    public void getPersonList() {
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

        Call<PersonListData> personListDataCall = api.getAllPersonList();
        personListDataCall.enqueue(new Callback<PersonListData>() {
            @Override
            public void onResponse(Call<PersonListData> call, Response<PersonListData> response) {
                try {
                    if (response.body() != null) {
                        PersonListData data = response.body();
                        if (data.getErrorMessage().getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "No Person List Found", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            personIdArray.clear();
                            personNameArray.clear();
                            deptIdArray.clear();
                            personNameArray.add("Select Person");
                            personIdArray.add(0);
                            deptIdArray.add(0);

                            for (int i = 0; i < data.getPersonList().size(); i++) {
                                personIdArray.add(data.getPersonList().get(i).getPersonId());
                                personNameArray.add(data.getPersonList().get(i).getPersonName());
                                deptIdArray.add(data.getPersonList().get(i).getDeptId());
                            }

                            ArrayAdapter<String> personAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, personNameArray);
                            spPerson.setAdapter(personAdapter);
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "No Person List Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "No Person List Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PersonListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No Person List Found", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "No Purpose List Found", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            purposeIdArray.clear();
                            purposeNameArray.clear();
                            purposeNameArray.add("Select Purpose");
                            purposeIdArray.add(0);

                            for (int i = 0; i < data.getPurposeList().size(); i++) {
                                purposeNameArray.add(data.getPurposeList().get(i).getPurpose());
                                purposeIdArray.add(data.getPurposeList().get(i).getPurposeId());
                            }

                            ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, purposeNameArray);
                            spPurpose.setAdapter(purposeAdapter);
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "No Purpose List Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "No Purpose List Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurposeListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No Purpose List Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void insertTransaction(MoneyOutBean moneyOutBean) {
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

        Call<ErrorMessage> errorMessageCall = api.insertMoneyOut(moneyOutBean);
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
                    Log.e("Money OUT : ", " Exception : " + e.getMessage());
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
                Fragment adf = new MoneyOutEntriesFragment();
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

                        cashBal = data.getOpBalance();
                        physicalCash = data.getPhysicalCash();

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
