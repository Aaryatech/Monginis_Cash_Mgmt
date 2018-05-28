package com.ats.monginis_cash_mgmt.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.CompanySelectionActivity;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.activity.ViewImageActivity;
import com.ats.monginis_cash_mgmt.adapter.MoneyOutEntryAdapter;
import com.ats.monginis_cash_mgmt.bean.CashBalanceDataBean;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.LoginData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.MoneyOutBean;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
import com.ats.monginis_cash_mgmt.bean.PurposeListData;
import com.ats.monginis_cash_mgmt.bean.UserList;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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

public class HomeFragment extends Fragment implements View.OnClickListener {

    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "MonginisMgmt");
    File f;

    int userId, userType;

    private ListView lvEntries, lvMoneyOutList;
    private Button btnApprove;
    private LinearLayout llData, llMoneyOut;
    private TextView tvRupees, tvCompanyName, tvCompanyName2, tvMoneyOut, tvTrSettle, tvPhysicalRupees;
    private CheckBox cbCheck;

    private ArrayList<GetMoneyOutData> moneyOutEntryArray = new ArrayList<>();

    public static Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    private ArrayList<GetMoneyOutData> selectedIdArray = new ArrayList<>();

    MoneyOutEntryAdapter mAdapter;
    ApprovalAdapter adapter;

    int yyyy, mm, dd;
    long fromDateMillis, toDateMillis;

    private ArrayList<UserList> userArray = new ArrayList<>();
    private ArrayList<String> userNameArray = new ArrayList<>();
    private ArrayList<Integer> userIdArray = new ArrayList<>();

    private ArrayList<String> personNameArray = new ArrayList<>();
    private ArrayList<Integer> personIdArray = new ArrayList<>();

    private ArrayList<String> purposeNameArray = new ArrayList<>();
    private ArrayList<Integer> purposeIdArray = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");

        createFolder();

        //Log.e("HomeFragment : ", " --- " + HomeActivity.BASE_URL);

        SharedPreferences pref = getContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        //Log.e("User Bean : ", "---------------" + userBean);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
                userType = userBean.getUserType();
            }
        } catch (Exception e) {
        }

        lvEntries = view.findViewById(R.id.lvHome_List);
        cbCheck = view.findViewById(R.id.cbHomeApproval);
        tvRupees = view.findViewById(R.id.tvHome_Rupees);
        tvPhysicalRupees = view.findViewById(R.id.tvHome_PhysicalRupees);
        llData = view.findViewById(R.id.llHomeData);
        llMoneyOut = view.findViewById(R.id.llHomeMoneuOut);
        lvMoneyOutList = view.findViewById(R.id.lvHome_MoneyOutEntry);
        tvCompanyName = view.findViewById(R.id.tvHome_CompanyName);
        tvCompanyName2 = view.findViewById(R.id.tvHome2_CompanyName);
        tvMoneyOut = view.findViewById(R.id.tvHome_MoneyOut);
        tvTrSettle = view.findViewById(R.id.tvHome_TrSettle);
        btnApprove = view.findViewById(R.id.btnHomeApprove);
        btnApprove.setOnClickListener(this);

        tvMoneyOut.setOnClickListener(this);
        tvTrSettle.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);
        tvCompanyName2.setText(HomeActivity.COMPANY_NAME);


        if (userType == 0) {
            llMoneyOut.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);
            btnApprove.setVisibility(View.VISIBLE);
            getApprovalEntries();
            getCashBalanceData();

        } else if (userType == 1) {
            llMoneyOut.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
            btnApprove.setVisibility(View.GONE);
            getAllMoneyOutEntries();
            getPersonList();
            getPurposeList();
        }

        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedIdArray.clear();
                    adapter = new ApprovalAdapter(getContext(), moneyOutEntryArray, 1);
                    lvEntries.setAdapter(adapter);
                } else {
                    selectedIdArray.clear();
                    adapter = new ApprovalAdapter(getContext(), moneyOutEntryArray, 0);
                    lvEntries.setAdapter(adapter);
                }
            }
        });

        return view;
    }

    public void createFolder() {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public void getApprovalEntries() {
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getApprovalEntries();
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();

                        if (moneyOutEntryArray.size() > 0) {
                            for (int i = 0; i < moneyOutEntryArray.size(); i++) {
                                map.put(moneyOutEntryArray.get(i).getTrId(), false);
                            }
                        }

                        adapter = new ApprovalAdapter(getContext(), moneyOutEntryArray, 0);
                        lvEntries.setAdapter(adapter);

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
            public void onFailure(Call<ArrayList<GetMoneyOutData>> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                //Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
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
    public void onClick(View v) {
        if (v.getId() == R.id.btnHomeApprove) {
            //Log.e("Ids List : ", " ------ " + selectedIdArray);

            if (selectedIdArray.size() > 0) {
                ArrayList<MoneyOutBean> beanArray = new ArrayList<>();
                for (int i = 0; i < selectedIdArray.size(); i++) {
                    try {
                        if (selectedIdArray.get(i).getRejReturnAmt() > 0) {
                            MoneyOutBean bean = new MoneyOutBean(selectedIdArray.get(i).getTrId(), selectedIdArray.get(i).getOutDate(), selectedIdArray.get(i).getOutDatetime(), selectedIdArray.get(i).getOutAmt(), selectedIdArray.get(i).getPersonId(), selectedIdArray.get(i).getDeptId(), selectedIdArray.get(i).getPurposeId(), selectedIdArray.get(i).getOutRemark(), selectedIdArray.get(i).getOutBy(), selectedIdArray.get(i).getIsSettle(), selectedIdArray.get(i).getSettleUserid(), selectedIdArray.get(i).getSettleDate(), selectedIdArray.get(i).getReturnAmt(), selectedIdArray.get(i).getReturnReason(), selectedIdArray.get(i).getIsBill(), selectedIdArray.get(i).getBillPhoto(), 1, "", "", userId, selectedIdArray.get(i).getRejReason(), selectedIdArray.get(i).getExpAmt(), selectedIdArray.get(i).getRejReturnAmt(), selectedIdArray.get(i).getRejReturnAmt());
                            beanArray.add(bean);
                        } else {
                            float trCloseAmt = selectedIdArray.get(i).getOutAmt() - selectedIdArray.get(i).getReturnAmt();
                            MoneyOutBean bean = new MoneyOutBean(selectedIdArray.get(i).getTrId(), selectedIdArray.get(i).getOutDate(), selectedIdArray.get(i).getOutDatetime(), selectedIdArray.get(i).getOutAmt(), selectedIdArray.get(i).getPersonId(), selectedIdArray.get(i).getDeptId(), selectedIdArray.get(i).getPurposeId(), selectedIdArray.get(i).getOutRemark(), selectedIdArray.get(i).getOutBy(), selectedIdArray.get(i).getIsSettle(), selectedIdArray.get(i).getSettleUserid(), selectedIdArray.get(i).getSettleDate(), selectedIdArray.get(i).getReturnAmt(), selectedIdArray.get(i).getReturnReason(), selectedIdArray.get(i).getIsBill(), selectedIdArray.get(i).getBillPhoto(), 1, "", "", userId, selectedIdArray.get(i).getRejReason(), selectedIdArray.get(i).getExpAmt(), selectedIdArray.get(i).getRejReturnAmt(), trCloseAmt);
                            beanArray.add(bean);
                        }
                    } catch (Exception e) {
                    }
                }
                insertApproveTransaction(beanArray);
            } else {
                Toast.makeText(getContext(), "Please Select Entries", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.tvHome_MoneyOut) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutFragment(), "HomeFragment");
            ft.commit();
        } else if (v.getId() == R.id.tvHome_TrSettle) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TransactionSettlementFragment(), "HomeFragment");
            ft.commit();
        }
    }

    public void getAllMoneyOutEntries() {
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getAllMoneyOutEntries(userId);
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();
                        //Log.e("MONEY OUT ENTRY : ", " --- " + response.body());
                        mAdapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvMoneyOutList.setAdapter(mAdapter);

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
            public void onFailure(Call<ArrayList<GetMoneyOutData>> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                //Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void getAllMoneyOutEntriesByDate(String fromDate, String toDate) {
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getAllMoneyOutEntriesByDate(userId, fromDate, toDate);
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();
                        //Log.e("MONEY OUT ENTRY : ", " --- " + response.body());
                        mAdapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvMoneyOutList.setAdapter(mAdapter);

                        toDateMillis = 0;
                        fromDateMillis = 0;


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
            public void onFailure(Call<ArrayList<GetMoneyOutData>> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                //Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void getAllMoneyOutEntriesByDateAndPerson(String fromDate, String toDate, int perId) {
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getAllMoneyOutEntriesByPerson(userId, fromDate, toDate, perId);
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();
                        //Log.e("MONEY OUT ENTRY : ", " --- " + response.body());
                        mAdapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvMoneyOutList.setAdapter(mAdapter);

                        toDateMillis = 0;
                        fromDateMillis = 0;


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
            public void onFailure(Call<ArrayList<GetMoneyOutData>> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                //Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void getAllMoneyOutEntriesByDateAndPurpose(String fromDate, String toDate, int purposeId) {
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getAllMoneyOutEntriesByPurpose(userId, fromDate, toDate, purposeId);
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();
                        //Log.e("MONEY OUT ENTRY : ", " --- " + response.body());
                        mAdapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvMoneyOutList.setAdapter(mAdapter);

                        toDateMillis = 0;
                        fromDateMillis = 0;


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
            public void onFailure(Call<ArrayList<GetMoneyOutData>> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                //Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void insertApproveTransaction(ArrayList<MoneyOutBean> moneyOutBean) {
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
        Call<ErrorMessage> errorMessageCall = api.insertApproveRejectMoneyOut(moneyOutBean);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable To Process" + message.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            getApprovalEntries();
                            getCashBalanceData();
                            cbCheck.setChecked(false);
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-----------------------------------------------------------------------------
    //Adapter---------------

    public class ApprovalAdapter extends BaseAdapter {

        Context context;
        private ArrayList<GetMoneyOutData> originalValues;
        private ArrayList<GetMoneyOutData> displayedValues;
        LayoutInflater inflater;
        int cbStatus;
        private Boolean isTouched = false;

        public ApprovalAdapter(Context context, ArrayList<GetMoneyOutData> catArray, int cbStatus) {
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

        public class ViewHolder {
            CheckBox cbCheck;
            TextView tvName, tvDate, tvReject, tvGivenAmt, tvReturnAmt, tvBillAmt, tvExpAmt, tvRejReturnAmt, tvPurpose, tvRemark;
            ImageView ivPhoto;
            LinearLayout llHead, llData, llExpAmt, llRejReturnAmt, llPurpose, llRemark;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            ApprovalAdapter.ViewHolder holder = null;

            if (v == null) {
                v = inflater.inflate(R.layout.custom_approval_layout, null);
                holder = new ApprovalAdapter.ViewHolder();
                holder.llHead = v.findViewById(R.id.llCustomApproval_Head);
                holder.llData = v.findViewById(R.id.llCustomApproval_Data);
                holder.tvName = v.findViewById(R.id.tvCustomApproval_Name);
                holder.tvDate = v.findViewById(R.id.tvCustomApproval_Date);
                holder.cbCheck = v.findViewById(R.id.cbCustomApproval);
                holder.tvReject = v.findViewById(R.id.tvCustomApproval_Reject);
                holder.tvGivenAmt = v.findViewById(R.id.tvCustomApproval_GivenAmt);
                holder.tvReturnAmt = v.findViewById(R.id.tvCustomApproval_ReturnAmt);
                holder.tvBillAmt = v.findViewById(R.id.tvCustomApproval_BillAmt);
                holder.tvExpAmt = v.findViewById(R.id.tvCustomApproval_ExpAmt);
                holder.tvRejReturnAmt = v.findViewById(R.id.tvCustomApproval_RejReturnAmt);
                holder.ivPhoto = v.findViewById(R.id.ivCustomApproval_Img);
                holder.llExpAmt = v.findViewById(R.id.llCustomApproval_ExpAmt);
                holder.llRejReturnAmt = v.findViewById(R.id.llCustomApproval_RejReturnAmt);
                holder.llPurpose = v.findViewById(R.id.llCustomApproval_Purpose);
                holder.llRemark = v.findViewById(R.id.llCustomApproval_Reason);
                holder.tvPurpose = v.findViewById(R.id.tvCustomApproval_Purpose);
                holder.tvRemark = v.findViewById(R.id.tvCustomApproval_Reason);


                v.setTag(holder);
            } else {
                holder = (ApprovalAdapter.ViewHolder) v.getTag();
            }

            Boolean value = map.get(displayedValues.get(position).getTrId());
            holder.cbCheck.setChecked(value);

            isTouched = false;
            holder.cbCheck.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isTouched = true;
                    return false;
                }
            });

            holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isTouched) {
                        isTouched = false;
                        if (isChecked) {
                            map.put(displayedValues.get(position).getTrId(), true);
                            selectedIdArray.add(displayedValues.get(position));
                        } else {
                            map.put(displayedValues.get(position).getTrId(), false);
                            selectedIdArray.remove(displayedValues.get(position));
                        }
                    }
                }
            });

            if (displayedValues.get(position).getExpAmt() > 0) {
                holder.llExpAmt.setVisibility(View.VISIBLE);
                holder.llRejReturnAmt.setVisibility(View.VISIBLE);
            }

            holder.llPurpose.setVisibility(View.VISIBLE);
            holder.llRemark.setVisibility(View.VISIBLE);

            holder.tvName.setText("" + displayedValues.get(position).getPersonName());

            holder.tvPurpose.setText("" + displayedValues.get(position).getPurpose());
            holder.tvRemark.setText("" + displayedValues.get(position).getOutRemark());

            String dt = displayedValues.get(position).getOutDate();
            String yr = dt.substring(0, 4);
            String mn = dt.substring(5, 7);
            String dy = dt.substring(8);
            holder.tvDate.setText(dy + "/" + mn + "/" + yr);

            holder.tvGivenAmt.setText("Given Amount: " + displayedValues.get(position).getOutAmt());
            holder.tvReturnAmt.setText("Return Amount: " + displayedValues.get(position).getReturnAmt());
            holder.tvBillAmt.setText("Bill Amount: " + (displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt()));
            holder.tvExpAmt.setText("Expected Amount: " + displayedValues.get(position).getExpAmt());
            holder.tvRejReturnAmt.setText("Rejected Return Amount: " + displayedValues.get(position).getRejReturnAmt());

            String image = displayedValues.get(position).getBillPhoto();
            SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
            int company = pref.getInt("Company", 0);

            try {

                String IMAGE_URL = "";
                if (company == 1) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_1;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                } else if (company == 2) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_2;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                } else if (company == 3) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_3;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                } else if (company == 4) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_4;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            if (cbStatus == 1) {
                holder.cbCheck.setChecked(true);
                selectedIdArray.add(displayedValues.get(position));
            }

            final ApprovalAdapter.ViewHolder finalHolder = holder;
            holder.llHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalHolder.llData.getVisibility() == View.GONE) {
                        finalHolder.llData.setVisibility(View.VISIBLE);
                    } else if (finalHolder.llData.getVisibility() == View.VISIBLE) {
                        finalHolder.llData.setVisibility(View.GONE);
                    }
                }
            });

            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("BillImage", displayedValues.get(position).getBillPhoto());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            holder.tvReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
                    dialog.setContentView(R.layout.custom_reject_transaction);

                    final TextView btnCancle = dialog.findViewById(R.id.tvCustomReject_Cancel);
                    final TextView btnReject = dialog.findViewById(R.id.tvCustomReject_Rej);
                    final EditText edRemark = dialog.findViewById(R.id.edCustomReject_RejRemark);
                    final EditText edExpAmt = dialog.findViewById(R.id.edCustomReject_ExpAmt);
                    final EditText edBillAmt = dialog.findViewById(R.id.edCustomReject_BillAmt);
                    final float billAmt = displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt();
                    edBillAmt.setText("" + (displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt()));
                    edExpAmt.setText("" + displayedValues.get(position).getExpAmt());

                    btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edExpAmt.getText().toString().isEmpty()) {
                                edExpAmt.setError("Required");
                                edExpAmt.requestFocus();
                            } else if (Float.parseFloat(edExpAmt.getText().toString()) > billAmt) {
                                Toast.makeText(getActivity(), "Expected Amount Should Not Be More Than Bill Amount", Toast.LENGTH_SHORT).show();
                                edExpAmt.requestFocus();
                            } else if (edRemark.getText().toString().isEmpty()) {
                                edRemark.setError("Required");
                                edRemark.requestFocus();
                            } else {
                                float expAmt = Float.parseFloat(edExpAmt.getText().toString());
                                String rejRemark = edRemark.getText().toString();
                                MoneyOutBean bean = new MoneyOutBean(displayedValues.get(position).getTrId(), displayedValues.get(position).getOutDate(), displayedValues.get(position).getOutDatetime(), displayedValues.get(position).getOutAmt(), displayedValues.get(position).getPersonId(), displayedValues.get(position).getDeptId(), displayedValues.get(position).getPurposeId(), displayedValues.get(position).getOutRemark(), displayedValues.get(position).getOutBy(), displayedValues.get(position).getIsSettle(), displayedValues.get(position).getSettleUserid(), displayedValues.get(position).getSettleDate(), displayedValues.get(position).getReturnAmt(), displayedValues.get(position).getReturnReason(), displayedValues.get(position).getIsBill(), displayedValues.get(position).getBillPhoto(), 2, "", "", userId, rejRemark, expAmt, displayedValues.get(position).getRejReturnAmt(), displayedValues.get(position).getTrClosedAmt());
                                ArrayList<MoneyOutBean> beanArray = new ArrayList<>();
                                beanArray.add(bean);
                                insertApproveTransaction(beanArray);
//                                insertTransaction(bean);
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


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        if (userType == 1) {
            item.setVisible(true);
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                new FilterDialog(getContext(), personIdArray, personNameArray, purposeIdArray, purposeNameArray).show();
                //showDialog(personIdArray, personNameArray, userIdArray, userNameArray);
                return true;

            default:
                return super.onOptionsItemSelected(item);

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
                        } else {
                            commonDialog.dismiss();
                            personNameArray.clear();
                            personIdArray.clear();
                            personNameArray.add("Select Person");
                            personIdArray.add(0);
                            for (int i = 0; i < data.getPersonList().size(); i++) {
                                personNameArray.add(data.getPersonList().get(i).getPersonName());
                                personIdArray.add(data.getPersonList().get(i).getPersonId());
                            }

                            //ArrayAdapter<String> personAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, personNameArray);
                            //spPerson.setAdapter(personAdapter);
                        }
                    } else {
                        commonDialog.dismiss();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PersonListData> call, Throwable t) {
                commonDialog.dismiss();
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

//                            ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, purposeNameArray);
//                            spPurpose.setAdapter(purposeAdapter);
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


    public class FilterDialog extends Dialog {

        ArrayList<Integer> personId;
        ArrayList<Integer> purposeId;
        ArrayList<String> personName;
        ArrayList<String> purposeName;


        public FilterDialog(@NonNull Context context, ArrayList<Integer> personIdList, ArrayList<String> personNameList, ArrayList<Integer> purposeIdList, ArrayList<String> purposeNameList) {
            super(context);
            this.personId = personIdList;
            this.personName = personNameList;
            this.purposeId = purposeIdList;
            this.purposeName = purposeNameList;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setTitle("Filter");
            setContentView(R.layout.custom_filter_dialog_layout);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().setAttributes(lp);

            final RadioButton rbAll = findViewById(R.id.rbFilterDialog_All);
            final RadioButton rbDate = findViewById(R.id.rbFilterDialog_Date);
            final RadioButton rbPerson = findViewById(R.id.rbFilterDialog_Person);
            final RadioButton rbPurpose = findViewById(R.id.rbFilterDialog_Purpose);

            final LinearLayout llDateLayout = findViewById(R.id.llFilterDialog_Date);
            final LinearLayout llPersonLayout = findViewById(R.id.llFilterDialog_Person);
            final LinearLayout llPurposeLayout = findViewById(R.id.llFilterDialog_Purpose);

            final Spinner spPerson = findViewById(R.id.spFilterDialog_Person);
            final Spinner spPurpose = findViewById(R.id.spFilterDialog_Purpose);

            final EditText edFromDate = findViewById(R.id.edFilterDialog_FromDate);
            final EditText edToDate = findViewById(R.id.edFilterDialog_ToDate);

            TextView tvSearch = findViewById(R.id.tvFilterDialog_Search);

            final TextView tvFromDate = findViewById(R.id.tvFilterDialog_FromDate);
            final TextView tvToDate = findViewById(R.id.tvFilterDialog_ToDate);

            rbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        llDateLayout.setVisibility(View.GONE);
                        llPersonLayout.setVisibility(View.GONE);
                        llPurposeLayout.setVisibility(View.GONE);

                    }
                }
            });

            rbDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        llDateLayout.setVisibility(View.VISIBLE);
                        llPersonLayout.setVisibility(View.GONE);
                        llPurposeLayout.setVisibility(View.GONE);
                    }
                }
            });

            rbPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        llDateLayout.setVisibility(View.VISIBLE);
                        llPersonLayout.setVisibility(View.VISIBLE);
                        llPurposeLayout.setVisibility(View.GONE);
                    }
                }
            });

            rbPurpose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        llDateLayout.setVisibility(View.VISIBLE);
                        llPersonLayout.setVisibility(View.GONE);
                        llPurposeLayout.setVisibility(View.VISIBLE);
                    }
                }
            });


            ArrayAdapter<String> personAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, personName);
            spPerson.setAdapter(personAdapter);
            ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, purposeName);
            spPurpose.setAdapter(purposeAdapter);


            edFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int yr, mn, dy;
                    if (fromDateMillis > 0) {
                        Calendar purchaseCal = Calendar.getInstance();
                        purchaseCal.setTimeInMillis(fromDateMillis);
                        yr = purchaseCal.get(Calendar.YEAR);
                        mn = purchaseCal.get(Calendar.MONTH);
                        dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
                    } else {
                        Calendar purchaseCal = Calendar.getInstance();
                        yr = purchaseCal.get(Calendar.YEAR);
                        mn = purchaseCal.get(Calendar.MONTH);
                        dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
                    }

                    DatePickerDialog.OnDateSetListener fromDtListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            yyyy = year;
                            mm = month + 1;
                            dd = dayOfMonth;
                            edFromDate.setText(dd + "-" + mm + "-" + yyyy);
                            tvFromDate.setText(yyyy + "-" + mm + "-" + dd);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(yyyy, mm - 1, dd);
                            calendar.set(Calendar.MILLISECOND, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.HOUR, 0);
                            fromDateMillis = calendar.getTimeInMillis();
                        }
                    };


                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), fromDtListener, yr, mn, dy);
                    dialog.show();
                }
            });


            edToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int yr, mn, dy;
                    if (toDateMillis > 0) {
                        Calendar purchaseCal = Calendar.getInstance();
                        purchaseCal.setTimeInMillis(toDateMillis);
                        yr = purchaseCal.get(Calendar.YEAR);
                        mn = purchaseCal.get(Calendar.MONTH);
                        dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
                    } else {
                        Calendar purchaseCal = Calendar.getInstance();
                        yr = purchaseCal.get(Calendar.YEAR);
                        mn = purchaseCal.get(Calendar.MONTH);
                        dy = purchaseCal.get(Calendar.DAY_OF_MONTH);
                    }

                    DatePickerDialog.OnDateSetListener toDtListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            yyyy = year;
                            mm = month + 1;
                            dd = dayOfMonth;
                            edToDate.setText(dd + "-" + mm + "-" + yyyy);
                            tvToDate.setText(yyyy + "-" + mm + "-" + dd);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(yyyy, mm - 1, dd);
                            calendar.set(Calendar.MILLISECOND, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.HOUR, 0);
                            toDateMillis = calendar.getTimeInMillis();
                        }
                    };

                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), toDtListener, yr, mn, dy);
                    dialog.show();
                }
            });


            tvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (rbAll.isChecked()) {
                        dismiss();
                        getAllMoneyOutEntries();
                    } else if (rbDate.isChecked()) {
                        if (edFromDate.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Select From Date", Toast.LENGTH_SHORT).show();
                            edFromDate.requestFocus();
                        } else if (edToDate.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Select To Date", Toast.LENGTH_SHORT).show();
                            edToDate.requestFocus();
                        } else {
                            String from = tvFromDate.getText().toString();
                            String to = tvToDate.getText().toString();
                            dismiss();
                            getAllMoneyOutEntriesByDate(from, to);


                        }
                    } else if (rbPerson.isChecked()) {
                        if (edFromDate.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Select From Date", Toast.LENGTH_SHORT).show();
                            edFromDate.requestFocus();
                        } else if (edToDate.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Select To Date", Toast.LENGTH_SHORT).show();
                            edToDate.requestFocus();
                        } else if (spPerson.getSelectedItemPosition() == 0) {
                            Toast.makeText(getActivity(), "Please Select Person", Toast.LENGTH_SHORT).show();
                            spPerson.requestFocus();
                        } else {
                            String from = tvFromDate.getText().toString();
                            String to = tvToDate.getText().toString();
                            int person = personId.get(spPerson.getSelectedItemPosition());
                            dismiss();
                            getAllMoneyOutEntriesByDateAndPerson(from, to, person);

                        }
                    } else if (rbPurpose.isChecked()) {
                        if (edFromDate.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Select From Date", Toast.LENGTH_SHORT).show();
                            edFromDate.requestFocus();
                        } else if (edToDate.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Please Select To Date", Toast.LENGTH_SHORT).show();
                            edToDate.requestFocus();
                        } else if (spPurpose.getSelectedItemPosition() == 0) {
                            Toast.makeText(getActivity(), "Please Select Person", Toast.LENGTH_SHORT).show();
                            spPurpose.requestFocus();
                        } else {
                            String from = tvFromDate.getText().toString();
                            String to = tvToDate.getText().toString();
                            int purpose = purposeId.get(spPurpose.getSelectedItemPosition());
                            dismiss();
                            getAllMoneyOutEntriesByDateAndPurpose(from, to, purpose);

                        }
                    }

                }
            });


        }
    }

}
