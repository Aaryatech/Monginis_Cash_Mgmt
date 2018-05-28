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
import com.ats.monginis_cash_mgmt.adapter.UserMasterAdapter;
import com.ats.monginis_cash_mgmt.bean.UserList;
import com.ats.monginis_cash_mgmt.bean.UserListData;
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

public class UserManagementFragment extends Fragment implements View.OnClickListener {

    private ListView lvUsers;
    private FloatingActionButton fab;
    private TextView tvCompanyName;

    private ArrayList<UserList> userArray = new ArrayList<>();
    UserMasterAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        getActivity().setTitle("User Management");

        lvUsers = view.findViewById(R.id.lvUserMaster);
        fab = view.findViewById(R.id.fabUserMaster);
        fab.setOnClickListener(this);

        tvCompanyName = view.findViewById(R.id.tvUserMaster_CompanyName);
        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        getUserList();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabUserMaster) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new AddUserFragment(), "UserMaster");
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
                            Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                            Log.e("User : ", " ERROR : " + data.getErrorMessage().getError());
                        } else {
                            commonDialog.dismiss();
                            userArray.clear();
                            for (int i = 0; i < data.getUserList().size(); i++) {
                                userArray.add(data.getUserList().get(i));
                            }
                            userAdapter = new UserMasterAdapter(getContext(), userArray);
                            lvUsers.setAdapter(userAdapter);

                            Log.e("User List : ", " --- " + userArray);
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                        Log.e("User : ", " NULL ");
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Log.e("User : ", " EXCEPTION : " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserListData> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "No List Found", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                Log.e("User : ", " FAILURE : " + t.getMessage());
            }
        });
    }
}
