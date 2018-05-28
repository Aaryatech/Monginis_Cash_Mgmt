package com.ats.monginis_cash_mgmt.fragment;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ats.monginis_cash_mgmt.adapter.TransactionSettlementEntriesAdapter;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
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

public class TransactionSettlementFragment extends Fragment implements View.OnClickListener {

    private RadioButton rbAll, rbDate, rbPerson, rbEnterBy;
    private EditText edFromDate, edToDate;
    private Spinner spPerson, spEnterBy;
    private ListView lvEntries;
    private LinearLayout llDate, llPerson, llEnterBy;
    private ImageView ivDateSearch, ivPersonSearch, ivEnterBySearch;
    private TextView tvCompanyName, tvMoneyOut;

    int yyyy, mm, dd;
    long fromDateMillis, toDateMillis;

    private ArrayList<UserList> userArray = new ArrayList<>();
    private ArrayList<String> userNameArray = new ArrayList<>();
    private ArrayList<Integer> userIdArray = new ArrayList<>();

    private ArrayList<String> personNameArray = new ArrayList<>();
    private ArrayList<Integer> personIdArray = new ArrayList<>();

    private ArrayList<GetMoneyOutData> moneyOutEntryArray = new ArrayList<>();
    TransactionSettlementEntriesAdapter adapter;

    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_settlement, container, false);
        getActivity().setTitle("Transaction Settlement");

        SharedPreferences pref = getContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        Log.e("User Bean : ", "---------------" + userBean);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
            }
        } catch (Exception e) {
        }

        rbAll = view.findViewById(R.id.rbTranscSettle_All);
        rbDate = view.findViewById(R.id.rbTranscSettle_Date);
        rbPerson = view.findViewById(R.id.rbTranscSettle_Person);
        rbEnterBy = view.findViewById(R.id.rbTranscSettle_EnterBy);
        edFromDate = view.findViewById(R.id.edTranscSettle_FromDate);
        edToDate = view.findViewById(R.id.edTranscSettle_ToDate);
        spPerson = view.findViewById(R.id.spTranscSettle_Person);
        spEnterBy = view.findViewById(R.id.spTranscSettle_EnterBy);
        lvEntries = view.findViewById(R.id.lvTranscSettle_List);
        llDate = view.findViewById(R.id.llTranscSettle_Date);
        llPerson = view.findViewById(R.id.llTranscSettle_Person);
        llEnterBy = view.findViewById(R.id.llTranscSettle_EnterBy);
        ivDateSearch = view.findViewById(R.id.ivTranscSettle_DateSearch);
        ivPersonSearch = view.findViewById(R.id.ivTranscSettle_PersonSearch);
        ivEnterBySearch = view.findViewById(R.id.ivTranscSettle_EnterBySearch);
        tvCompanyName = view.findViewById(R.id.tvTranscSettle_CompanyName);
        tvMoneyOut = view.findViewById(R.id.tvTranscSettle_MoneyOut);

        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);
        ivDateSearch.setOnClickListener(this);
        ivPersonSearch.setOnClickListener(this);
        ivEnterBySearch.setOnClickListener(this);
        tvMoneyOut.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);


        rbAll.setChecked(true);
        rbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llPerson.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.GONE);
                    if (moneyOutEntryArray.size() > 0) {
                        adapter = new TransactionSettlementEntriesAdapter(getContext(), moneyOutEntryArray);
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
                    llPerson.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.GONE);
                }
            }
        });

        rbPerson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llPerson.setVisibility(View.VISIBLE);
                    llEnterBy.setVisibility(View.GONE);
                }
            }
        });

        rbEnterBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llPerson.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.VISIBLE);
                }
            }
        });

        getUserList();
        getPersonList();

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edTranscSettle_FromDate) {
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
        } else if (v.getId() == R.id.edTranscSettle_ToDate) {
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
        } else if (v.getId() == R.id.ivTranscSettle_DateSearch) {
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

                    Log.e("From Date : ", "---------" + fromMillis);

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

                    Log.e("To Date : ", "---------" + toMillis);

                    ArrayList<GetMoneyOutData> tempArray = new ArrayList<>();
                    tempArray.clear();
                    if (moneyOutEntryArray.size() > 0) {
                        Log.e("Entries : ", "--- " + moneyOutEntryArray);
                        for (int i = 0; i < moneyOutEntryArray.size(); i++) {
                            String dt = moneyOutEntryArray.get(i).getOutDate();
                            int year = Integer.parseInt(dt.substring(0, 4));
                            int month = Integer.parseInt(dt.substring(5, 7));
                            int day = Integer.parseInt(dt.substring(8));

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month - 1, day);
                            long dtMillis = cal.getTimeInMillis();
                            Log.e("Millis : ", " ---- " + cal.getTimeInMillis());


                            if (dtMillis <= toMillis && dtMillis >= fromMillis) {
                                tempArray.add(moneyOutEntryArray.get(i));
                            }
                        }
                        adapter = new TransactionSettlementEntriesAdapter(getContext(), tempArray);
                        lvEntries.setAdapter(adapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception : ", " --- " + e.getMessage());
                }

            }

        } else if (v.getId() == R.id.ivTranscSettle_PersonSearch) {
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
                    adapter = new TransactionSettlementEntriesAdapter(getContext(), tempArray);
                    lvEntries.setAdapter(adapter);
                }
            }
        } else if (v.getId() == R.id.ivTranscSettle_EnterBySearch) {
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
                    adapter = new TransactionSettlementEntriesAdapter(getContext(), tempArray);
                    lvEntries.setAdapter(adapter);
                }
            }
        } else if (v.getId() == R.id.tvTranscSettle_MoneyOut) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutFragment(), "HomeFragment");
            ft.commit();
        }

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
                            Log.e("User : ", " ERROR : " + data.getErrorMessage().getError());
                        } else {
                            commonDialog.dismiss();
                            userArray.clear();
                            userNameArray.clear();
                            userIdArray.clear();
                            userNameArray.add("Select Enter By");
                            userIdArray.add(0);
                            for (int i = 0; i < data.getUserList().size(); i++) {
                                userArray.add(data.getUserList().get(i));
                                userNameArray.add(data.getUserList().get(i).getUserName());
                                userIdArray.add(data.getUserList().get(i).getUserId());
                            }

                            ArrayAdapter userAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, userNameArray);
                            spEnterBy.setAdapter(userAdapter);

                            Log.e("User List : ", " --- " + userArray);
                        }
                    } else {
                        commonDialog.dismiss();
                        Log.e("User : ", " NULL ");
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    e.printStackTrace();
                    Log.e("User : ", " EXCEPTION : " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserListData> call, Throwable t) {
                commonDialog.dismiss();
                t.printStackTrace();
                Log.e("User : ", " FAILURE : " + t.getMessage());
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getTransactionSettlementEntries(userId);
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();

                        adapter = new TransactionSettlementEntriesAdapter(getContext(), moneyOutEntryArray);
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
                Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("-----------", "On Resume");

        getAllMoneyOutEntries();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("-----------", "On Pause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("-----------", "On Start");
    }
}
