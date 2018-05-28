package com.ats.monginis_cash_mgmt.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.adapter.PersonMasterAdapter;
import com.ats.monginis_cash_mgmt.bean.PersonList;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
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

public class PersonMasterFragment extends Fragment implements View.OnClickListener {

    private ListView lvList;
    private TextView tvCompanyName;
    private FloatingActionButton fab;

    private ArrayList<PersonList> personArray = new ArrayList<>();
    PersonMasterAdapter personAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_master, container, false);
        getActivity().setTitle("Person Management");

        lvList = view.findViewById(R.id.lvPersonMaster);
        tvCompanyName = view.findViewById(R.id.tvPersonMaster_CompanyName);
        fab = view.findViewById(R.id.fabPersonMaster);
        fab.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        getPersonList();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabPersonMaster) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddPersonFragment(), "PersonMaster");
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
                            Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            personArray.clear();
                            for (int i = 0; i < data.getPersonList().size(); i++) {
                                personArray.add(data.getPersonList().get(i));
                            }
                            Log.e("Person List : ", "--- " + personArray);
                            personAdapter = new PersonMasterAdapter(getContext(), personArray);
                            lvList.setAdapter(personAdapter);
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
            public void onFailure(Call<PersonListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
