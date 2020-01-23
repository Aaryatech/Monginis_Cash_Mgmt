package com.ats.monginis_cash_mgmt.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.bean.LoginData;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edMobile, edPassword;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        edMobile = findViewById(R.id.edLogin_Mobile);
        edPassword = findViewById(R.id.edLogin_Password);
        tvLogin = findViewById(R.id.tvLogin_Submit);
        tvLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLogin_Submit) {
//            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//            finish();
            if (edMobile.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                edMobile.requestFocus();
            } else if (edMobile.getText().toString().length() != 10) {
                Toast.makeText(this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                edMobile.requestFocus();
            } else if (edPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                edPassword.requestFocus();
            } else {
               /* Intent intent = new Intent(this, CompanySelectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mobile", edMobile.getText().toString());
                bundle.putString("password", edPassword.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();*/
                doLogin(edMobile.getText().toString(), edPassword.getText().toString());
            }
        }
    }


    public void doLogin(String mobile, String pass) {
        if (Constants.isOnline(this)) {
            final CommonDialog commonDialog = new CommonDialog(LoginActivity.this, "Loading", "Please Wait...");
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
            Call<LoginData> loginDataCall = api.getLogin(mobile, pass);
            loginDataCall.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    try {
                        if (response.body() != null) {
                            LoginData data = response.body();
                            if (data.getErrorMessage().getError()) {
                                commonDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "" + data.getErrorMessage().getMessage(), Toast.LENGTH_SHORT).show();
                                //Log.e("Login : ", " onError : " + data.getErrorMessage().getMessage());
                            } else {
                                commonDialog.dismiss();

                                //Log.e("Login : ", " DATA : " + data);

                                SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(data.getMUser());
                                editor.putString("userData", json);
                                editor.putInt("Company", 1);
                                editor.apply();
                                HomeActivity.BASE_URL = "";
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                                /*i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);*/
                                finish();

                               /* startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();*/

                            }
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Unable To Login", Toast.LENGTH_SHORT).show();
                            //Log.e("Login : ", " NULL");
                        }
                    } catch (Exception e) {
                        commonDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Unable To Login", Toast.LENGTH_SHORT).show();
                        //Log.e("Login : ", " Exception : " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    commonDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Unable To Login", Toast.LENGTH_SHORT).show();
                    //Log.e("Login : ", " onFailure : " + t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

}
