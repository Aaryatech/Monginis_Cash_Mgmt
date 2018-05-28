package com.ats.monginis_cash_mgmt.fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.ats.monginis_cash_mgmt.adapter.MoneyInEntryAdapter;
import com.ats.monginis_cash_mgmt.bean.MoneyInEntryBean;
import com.ats.monginis_cash_mgmt.bean.UserList;
import com.ats.monginis_cash_mgmt.bean.UserListData;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;

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

import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class MoneyInEntriesFragment extends Fragment implements View.OnClickListener {

    private RadioButton rbAll, rbDate, rbSource, rbEnterBy;
    private EditText edFromDate, edToDate;
    private Spinner spSource, spEnterBy;
    private ListView lvEntries;
    private LinearLayout llDate, llSource, llEnterBy;
    private ImageView ivDateSearch, ivSourceSearch, ivEnterBySearch;
    private TextView tvCompanyName;

    private ArrayList<MoneyInEntryBean> moneyInEntryArray = new ArrayList<>();

    MoneyInEntryAdapter adapter;

    int yyyy, mm, dd;
    long fromDateMillis, toDateMillis;

    private ArrayList<UserList> userArray = new ArrayList<>();
    private ArrayList<String> userNameArray = new ArrayList<>();
    private ArrayList<Integer> userIdArray = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_money_in_entries, container, false);
        getActivity().setTitle("Money In Entries");

        rbAll = view.findViewById(R.id.rbMoneyInEntry_All);
        rbDate = view.findViewById(R.id.rbMoneyInEntry_Date);
        rbSource = view.findViewById(R.id.rbMoneyInEntry_Source);
        rbEnterBy = view.findViewById(R.id.rbMoneyInEntry_EnterBy);
        edFromDate = view.findViewById(R.id.edMoneyInEntry_FromDate);
        edToDate = view.findViewById(R.id.edMoneyInEntry_ToDate);
        spSource = view.findViewById(R.id.spMoneyInEntry_Source);
        spEnterBy = view.findViewById(R.id.spMoneyInEntry_EnterBy);
        lvEntries = view.findViewById(R.id.lvMoneyInEntry_List);
        llDate = view.findViewById(R.id.llMoneyInEntry_Date);
        llSource = view.findViewById(R.id.llMoneyInEntry_Source);
        llEnterBy = view.findViewById(R.id.llMoneyInEntry_EnterBy);
        tvCompanyName=view.findViewById(R.id.tvMoneyInEntries_CompanyName);

        ivDateSearch = view.findViewById(R.id.ivMoneyInEntry_DateSearch);
        ivSourceSearch = view.findViewById(R.id.ivMoneyInEntry_SourceSearch);
        ivEnterBySearch = view.findViewById(R.id.ivMoneyInEntry_EnterBySearch);
        ivDateSearch.setOnClickListener(this);
        ivSourceSearch.setOnClickListener(this);
        ivEnterBySearch.setOnClickListener(this);

        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        ArrayList<String> sourceArray = new ArrayList<>();
        sourceArray.add("Select Source");
        sourceArray.add("Director");
        sourceArray.add("Bank Withdrawal");

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, sourceArray);
        spSource.setAdapter(sourceAdapter);

        getUserList();

        rbAll.setChecked(true);
        rbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llDate.setVisibility(View.GONE);
                    llSource.setVisibility(View.GONE);
                    llEnterBy.setVisibility(View.GONE);
                    if (moneyInEntryArray.size() > 0) {
                        adapter = new MoneyInEntryAdapter(getContext(), moneyInEntryArray);
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

        rbSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        ArrayList<String> nameArray = new ArrayList<>();
        nameArray.add("Anmol Shirke");
        nameArray.add("Sachin Thackre");
        nameArray.add("Sagar Shinde");
        nameArray.add("Ankit More");

//        TransactionSettlementEntriesAdapter adapter = new TransactionSettlementEntriesAdapter(getContext(), nameArray, 1);
//        lvEntries.setAdapter(adapter);

        getAllMoneyInEntries();
        return view;
    }

    public void getAllMoneyInEntries() {
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

        Call<ArrayList<MoneyInEntryBean>> dataCall = api.getMoneyInEntries();
        dataCall.enqueue(new Callback<ArrayList<MoneyInEntryBean>>() {
            @Override
            public void onResponse(Call<ArrayList<MoneyInEntryBean>> call, Response<ArrayList<MoneyInEntryBean>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyInEntryArray.clear();
                        moneyInEntryArray = response.body();

                        adapter = new MoneyInEntryAdapter(getContext(), moneyInEntryArray);
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
            public void onFailure(Call<ArrayList<MoneyInEntryBean>> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivMoneyInEntry_DateSearch) {

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

                    ArrayList<MoneyInEntryBean> tempArray = new ArrayList<>();
                    tempArray.clear();
                    if (moneyInEntryArray.size() > 0) {
                        //Log.e("Entries : ", "--- " + moneyInEntryArray);
                        for (int i = 0; i < moneyInEntryArray.size(); i++) {
                            String dt = moneyInEntryArray.get(i).getInDate();
                            int year = Integer.parseInt(dt.substring(0, 4));
                            int month = Integer.parseInt(dt.substring(5, 7));
                            int day = Integer.parseInt(dt.substring(8));

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month - 1, day);
                            long dtMillis = cal.getTimeInMillis();
                            //Log.e("Millis : ", " ---- " + cal.getTimeInMillis());


                            if (dtMillis <= toMillis && dtMillis >= fromMillis) {
                                tempArray.add(moneyInEntryArray.get(i));
                            }
                        }
                        adapter = new MoneyInEntryAdapter(getContext(), tempArray);
                        lvEntries.setAdapter(adapter);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.e("Exception : ", " --- " + e.getMessage());
                }


            }

        } else if (v.getId() == R.id.ivMoneyInEntry_SourceSearch) {

            if (spSource.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Source", Toast.LENGTH_SHORT).show();
                spSource.requestFocus();
            } else {
                ArrayList<MoneyInEntryBean> tempArray = new ArrayList<>();
                tempArray.clear();
                int source = spSource.getSelectedItemPosition() - 1;
                if (moneyInEntryArray.size() > 0) {
                    for (int i = 0; i < moneyInEntryArray.size(); i++) {
                        if (moneyInEntryArray.get(i).getInSource() == source) {
                            tempArray.add(moneyInEntryArray.get(i));
                        }
                    }
                    adapter = new MoneyInEntryAdapter(getContext(), tempArray);
                    lvEntries.setAdapter(adapter);
                }
            }


        } else if (v.getId() == R.id.ivMoneyInEntry_EnterBySearch) {

            if (spEnterBy.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select Enter By", Toast.LENGTH_SHORT).show();
                spEnterBy.requestFocus();
            } else {
                int pos = spEnterBy.getSelectedItemPosition();
                int id = userIdArray.get(pos);
                ArrayList<MoneyInEntryBean> tempArray = new ArrayList<>();
                tempArray.clear();
                if (moneyInEntryArray.size() > 0) {
                    for (int i = 0; i < moneyInEntryArray.size(); i++) {
                        if (moneyInEntryArray.get(i).getUserId() == id) {
                            tempArray.add(moneyInEntryArray.get(i));
                        }
                    }
                    adapter = new MoneyInEntryAdapter(getContext(), tempArray);
                    lvEntries.setAdapter(adapter);
                }

            }

        } else if (v.getId() == R.id.edMoneyInEntry_FromDate) {
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
        } else if (v.getId() == R.id.edMoneyInEntry_ToDate) {
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
                            //Log.e("User : ", " ERROR : " + data.getErrorMessage().getError());
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
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                //Log.e("User : ", " FAILURE : " + t.getMessage());
            }
        });
    }
}
