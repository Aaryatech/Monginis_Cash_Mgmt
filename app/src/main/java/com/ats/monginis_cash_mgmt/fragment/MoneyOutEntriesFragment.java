package com.ats.monginis_cash_mgmt.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.adapter.MoneyInEntryAdapter;
import com.ats.monginis_cash_mgmt.adapter.MoneyOutEntryAdapter;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
import com.ats.monginis_cash_mgmt.bean.PurposeListData;
import com.ats.monginis_cash_mgmt.bean.UserList;
import com.ats.monginis_cash_mgmt.bean.UserListData;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;

import java.io.IOException;
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

public class MoneyOutEntriesFragment extends Fragment implements View.OnClickListener {

    private RadioButton rbAll, rbDate, rbPerson, rbEnterBy;
    private EditText edFromDate, edToDate;
    private Spinner spPerson, spEnterBy;
    private ListView lvEntries;
    private LinearLayout llDate, llSource, llEnterBy;
    private ImageView ivDateSearch, ivPersonSearch, ivEnterBySearch;
    private TextView tvCompanyName, tvMoneyOut, tvTrSettle;


    private ArrayList<UserList> userArray = new ArrayList<>();
    private ArrayList<String> userNameArray = new ArrayList<>();
    private ArrayList<Integer> userIdArray = new ArrayList<>();

    private ArrayList<String> personNameArray = new ArrayList<>();
    private ArrayList<Integer> personIdArray = new ArrayList<>();

    private ArrayList<String> purposeNameArray = new ArrayList<>();
    private ArrayList<Integer> purposeIdArray = new ArrayList<>();

    int userId;

    int yyyy, mm, dd;
    long fromDateMillis, toDateMillis;

    private ArrayList<GetMoneyOutData> moneyOutEntryArray = new ArrayList<>();
    MoneyOutEntryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_money_out_entries, container, false);
        getActivity().setTitle("Money Out Entries");

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

        rbAll = view.findViewById(R.id.rbMoneyOutEntry_All);
        rbDate = view.findViewById(R.id.rbMoneyOutEntry_Date);
        rbPerson = view.findViewById(R.id.rbMoneyOutEntry_Source);
        rbEnterBy = view.findViewById(R.id.rbMoneyOutEntry_EnterBy);
        edFromDate = view.findViewById(R.id.edMoneyOutEntry_FromDate);
        edToDate = view.findViewById(R.id.edMoneyOutEntry_ToDate);
        spPerson = view.findViewById(R.id.spMoneyOutEntry_Source);
        spEnterBy = view.findViewById(R.id.spMoneyOutEntry_EnterBy);
        lvEntries = view.findViewById(R.id.lvMoneyOutEntry_List);
        llDate = view.findViewById(R.id.llMoneyOutEntry_Date);
        llSource = view.findViewById(R.id.llMoneyOutEntry_Source);
        llEnterBy = view.findViewById(R.id.llMoneyOutEntry_EnterBy);
        tvCompanyName = view.findViewById(R.id.tvMoneyOutEntries_CompanyName);
        tvMoneyOut = view.findViewById(R.id.tvMoneyOutEntry_MoneyOut);
        tvTrSettle = view.findViewById(R.id.tvMoneyOutEntry_TrSettle);

        ivDateSearch = view.findViewById(R.id.ivMoneyOutEntry_DateSearch);
        ivPersonSearch = view.findViewById(R.id.ivMoneyOutEntry_PersonSearch);
        ivEnterBySearch = view.findViewById(R.id.ivMoneyOutEntry_EnterBySearch);
        ivDateSearch.setOnClickListener(this);
        ivPersonSearch.setOnClickListener(this);
        ivEnterBySearch.setOnClickListener(this);
        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);
        tvTrSettle.setOnClickListener(this);
        tvMoneyOut.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        //getUserList();
        getPurposeList();
        getPersonList();

        rbAll.setChecked(true);
        rbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llSource.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.GONE);
                    if (moneyOutEntryArray.size() > 0) {
                        adapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvEntries.setAdapter(adapter);
                    }
                }
            }
        });

        rbDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.VISIBLE);
                    llSource.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.GONE);
                }
            }
        });

        rbPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llSource.setVisibility(View.VISIBLE);
                    llEnterBy.setVisibility(View.GONE);
                }
            }
        });

        rbEnterBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llSource.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.VISIBLE);
                }
            }
        });

      /*  ArrayList<String> nameArray = new ArrayList<>();
        nameArray.add("Anmol Shirke");
        nameArray.add("Sachin Thackre");
        nameArray.add("Sagar Shinde");
        nameArray.add("Ankit More");

        TransactionSettlementEntriesAdapter adapter = new TransactionSettlementEntriesAdapter(getContext(), nameArray, 1);
        lvEntries.setAdapter(adapter);*/

        getAllMoneyOutEntries();
        return view;
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
                        adapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvEntries.setAdapter(adapter);

                        toDateMillis=0;
                        fromDateMillis=0;

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
                        adapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvEntries.setAdapter(adapter);

                        toDateMillis=0;
                        fromDateMillis=0;


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
                        adapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvEntries.setAdapter(adapter);

                        toDateMillis=0;
                        fromDateMillis=0;


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
                        adapter = new MoneyOutEntryAdapter(getContext(), moneyOutEntryArray);
                        lvEntries.setAdapter(adapter);

                        toDateMillis=0;
                        fromDateMillis=0;


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


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ivMoneyOutEntry_DateSearch) {
            if (edFromDate.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Select From Date", Toast.LENGTH_SHORT).show();
                edFromDate.requestFocus();
            } else if (edToDate.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Select To Date", Toast.LENGTH_SHORT).show();
                edToDate.requestFocus();
            } else if (toDateMillis < fromDateMillis) {
                Toast.makeText(getActivity(), "From Date Should Be Less Than To Date", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String fromDt = edFromDate.getText().toString();
                    int dy = Integer.parseInt(fromDt.substring(0, 2));
                    int mn = Integer.parseInt(fromDt.substring(3, 5));
                    int yr = Integer.parseInt(fromDt.substring(6));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(yr, mn - 1, dy);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    long fromMillis = calendar.getTimeInMillis();

                    //Log.e("From Date : ", "---------" + fromMillis);

                    String tODt = edToDate.getText().toString();
                    int dy1 = Integer.parseInt(tODt.substring(0, 2));
                    int mn1 = Integer.parseInt(tODt.substring(3, 5));
                    int yr1 = Integer.parseInt(tODt.substring(6));

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(yr1, mn1 - 1, dy1);
                    calendar1.set(Calendar.MILLISECOND, 0);
                    calendar1.set(Calendar.SECOND, 59);
                    calendar1.set(Calendar.MINUTE, 59);
                    calendar1.set(Calendar.HOUR, 23);
                    long toMillis = calendar1.getTimeInMillis();

                    //Log.e("To Date : ", "---------" + toMillis);

                    ArrayList<GetMoneyOutData> tempArray = new ArrayList<>();
                    tempArray.clear();
                    if (moneyOutEntryArray.size() > 0) {
                        //Log.e("Entries : ", "--- " + moneyOutEntryArray);
                        for (int i = 0; i < moneyOutEntryArray.size(); i++) {
                            String dt = moneyOutEntryArray.get(i).getOutDate();
                            int year = Integer.parseInt(dt.substring(0, 4));
                            int month = Integer.parseInt(dt.substring(5, 7));
                            int day = Integer.parseInt(dt.substring(8));

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month - 1, day);
                            long dtMillis = cal.getTimeInMillis();
                            //Log.e("Millis : ", " ---- " + cal.getTimeInMillis());


                            if (dtMillis <= toMillis && dtMillis >= fromMillis) {
                                tempArray.add(moneyOutEntryArray.get(i));
                            }
                        }
                        adapter = new MoneyOutEntryAdapter(getContext(), tempArray);
                        lvEntries.setAdapter(adapter);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.e("Exception : ", " --- " + e.getMessage());
                }


            }
        } else if (v.getId() == R.id.ivMoneyOutEntry_PersonSearch) {
            if (spPerson.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Person", Toast.LENGTH_SHORT).show();
                spPerson.requestFocus();
            } else {
                ArrayList<GetMoneyOutData> tempArray = new ArrayList<>();
                tempArray.clear();
                int person = personIdArray.get(spPerson.getSelectedItemPosition());

                if (moneyOutEntryArray.size() > 0) {
                    for (int i = 0; i < moneyOutEntryArray.size(); i++) {
                        if (moneyOutEntryArray.get(i).getPersonId() == person) {
                            tempArray.add(moneyOutEntryArray.get(i));
                        }
                    }
                    adapter = new MoneyOutEntryAdapter(getContext(), tempArray);
                    lvEntries.setAdapter(adapter);
                }
            }
        } else if (v.getId() == R.id.ivMoneyOutEntry_EnterBySearch) {
            if (spEnterBy.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Enter By", Toast.LENGTH_SHORT).show();
                spEnterBy.requestFocus();
            } else {
                int pos = spEnterBy.getSelectedItemPosition();
                int id = userIdArray.get(pos);
                ArrayList<GetMoneyOutData> tempArray = new ArrayList<>();
                tempArray.clear();
                if (moneyOutEntryArray.size() > 0) {
                    for (int i = 0; i < moneyOutEntryArray.size(); i++) {
                        if (moneyOutEntryArray.get(i).getOutBy() == id) {
                            tempArray.add(moneyOutEntryArray.get(i));
                        }
                    }
                    adapter = new MoneyOutEntryAdapter(getContext(), tempArray);
                    lvEntries.setAdapter(adapter);
                }
            }
        } else if (v.getId() == R.id.edMoneyOutEntry_FromDate) {
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
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), fromDateListener, yr, mn, dy);
            dialog.show();
        } else if (v.getId() == R.id.edMoneyOutEntry_ToDate) {
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
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), toDateListener, yr, mn, dy);
            dialog.show();
        } else if (v.getId() == R.id.tvMoneyOutEntry_MoneyOut) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutFragment(), "HomeFragment");
            ft.commit();
        } else if (v.getId() == R.id.tvMoneyOutEntry_TrSettle) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TransactionSettlementFragment(), "HomeFragment");
            ft.commit();
        }


    }

    public void getUserList() {
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

        Call<UserListData> userListDataCall = api.getAllUserList();
        userListDataCall.enqueue(new Callback<UserListData>() {
            @Override
            public void onResponse(Call<UserListData> call, Response<UserListData> response) {
                try {
                    if (response.body() != null) {
                        UserListData data = response.body();
                        if (data.getErrorMessage().getError()) {
                            commonDialog.dismiss();
                            //Log.e("User : ", " ERROR : " + data.getErrorMessage().getError());
                        } else {
                            commonDialog.dismiss();
                            userArray.clear();
                            userNameArray.clear();
                            userIdArray.clear();
                            userNameArray.add("Select Purpose");
                            userIdArray.add(0);
                            for (int i = 0; i < data.getUserList().size(); i++) {
                                userArray.add(data.getUserList().get(i));
                                userNameArray.add(data.getUserList().get(i).getUserName());
                                userIdArray.add(data.getUserList().get(i).getUserId());
                            }

                            ArrayAdapter userAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, userNameArray);
                            spEnterBy.setAdapter(userAdapter);

                            //Log.e("User List : ", " --- " + userArray);
                        }
                    } else {
                        commonDialog.dismiss();
                        //Log.e("User : ", " NULL ");
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    e.printStackTrace();
                    //Log.e("User : ", " EXCEPTION : " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserListData> call, Throwable t) {
                commonDialog.dismiss();
                t.printStackTrace();
                //Log.e("User : ", " FAILURE : " + t.getMessage());
            }
        });
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

                            ArrayAdapter<String> personAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, personNameArray);
                            spPerson.setAdapter(personAdapter);
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

    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yyyy = year;
            mm = month + 1;
            dd = dayOfMonth;
            edFromDate.setText(dd + "-" + mm + "-" + yyyy);

            Calendar calendar = Calendar.getInstance();
            calendar.set(yyyy, mm - 1, dd);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            fromDateMillis = calendar.getTimeInMillis();
        }
    };

    private DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yyyy = year;
            mm = month + 1;
            dd = dayOfMonth;
            edToDate.setText(dd + "-" + mm + "-" + yyyy);

            Calendar calendar = Calendar.getInstance();
            calendar.set(yyyy, mm - 1, dd);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            toDateMillis = calendar.getTimeInMillis();
        }
    };


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_filter);
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
            case R.id.action_filter:
                new FilterDialog(getContext(), personIdArray, personNameArray, purposeIdArray, purposeNameArray).show();
                //showDialog(personIdArray, personNameArray, userIdArray, userNameArray);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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
