package com.ats.monginis_cash_mgmt.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.bean.LoginData;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanySelectionActivity extends AppCompatActivity {

    private RadioButton rbCompany1, rbCompany2, rbCompany3, rbCompany4;
    private Button btnSubmit, btnLogin;
    String mobile, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_selection);
        setTitle("Select Company");

        rbCompany1 = findViewById(R.id.rbCompany1);
        rbCompany2 = findViewById(R.id.rbCompany2);
        rbCompany3 = findViewById(R.id.rbCompany3);
        rbCompany4 = findViewById(R.id.rbCompany4);

        btnSubmit = findViewById(R.id.btnCompSelection_Submit);
        btnLogin = findViewById(R.id.btnCompSelection_Login);

        int company = 0;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        try {
            company = pref.getInt("Company", 0);
        } catch (Exception e) {
        }

        if (company == 1) {
            rbCompany1.setChecked(true);
        } else if (company == 2) {
            rbCompany2.setChecked(true);
        } else if (company == 3) {
            rbCompany3.setChecked(true);
        } else if (company == 4) {
            rbCompany4.setChecked(true);
        }


        try {
            mobile = getIntent().getExtras().getString("mobile");
            password = getIntent().getExtras().getString("password");
        } catch (Exception e) {
            //Log.e("Company Selection", " : Exception : " + e.getMessage());
            e.getMessage();
        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.e("Click", "*---------------");

                if (rbCompany1.isChecked()) {
                    BASE_URL = Constants.BASE_URL_1;
                    doLogin(mobile, password, 1);
                } else if (rbCompany2.isChecked()) {
                    BASE_URL = Constants.BASE_URL_2;
                    doLogin(mobile, password, 2);
                } else if (rbCompany3.isChecked()) {
                    BASE_URL = Constants.BASE_URL_3;
                    doLogin(mobile, password, 3);
                } else if (rbCompany4.isChecked()) {
                    BASE_URL = Constants.BASE_URL_4;
                    doLogin(mobile, password, 4);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(CompanySelectionActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }


    public void doLogin(String mobile, String pass, final int company) {
        if (Constants.isOnline(this)) {
            final CommonDialog commonDialog = new CommonDialog(CompanySelectionActivity.this, "Loading", "Please Wait...");
            commonDialog.show();

            Log.e("Company Selection", " : doLogin : BASE_URL : " + BASE_URL);
            Log.e("Company Selection", " : doLogin : Mobile : " + mobile + " - Password : " + pass);

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
            Call<LoginData> loginDataCall = api.getLogin(mobile, pass);

            loginDataCall.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    try {
                        if (response.body() != null) {
                            LoginData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                commonDialog.dismiss();
                                Toast.makeText(CompanySelectionActivity.this, "" + data.getErrorMessage().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Login : ", " onError : " + data.getErrorMessage().getMessage());
                            } else {
                                commonDialog.dismiss();

                                Log.e("Login : ", " DATA : " + data);

                                SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(data.getMUser());
                                editor.putString("userData", json);
                                editor.putInt("Company", company);
                                editor.apply();
                                HomeActivity.BASE_URL = "";
                                Intent i = new Intent(CompanySelectionActivity.this, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            }
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(CompanySelectionActivity.this, "You Are Not Registered For This Company", Toast.LENGTH_SHORT).show();
                            Log.e("Login : ", " NULL");
                        }
                    } catch (Exception e) {
                        commonDialog.dismiss();
                        Toast.makeText(CompanySelectionActivity.this, "Sorry, Unable To Process", Toast.LENGTH_SHORT).show();
                        Log.e("Login : ", " Exception : " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    commonDialog.dismiss();
                    Toast.makeText(CompanySelectionActivity.this, "Sorry, Unable To Process", Toast.LENGTH_SHORT).show();
                    Log.e("Login : ", " onFailure : " + t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //Log.e("CompanyActivity: ", "-- back pressed");
    }
}
