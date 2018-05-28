package com.ats.monginis_cash_mgmt.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.UserList;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class AddUserFragment extends Fragment implements View.OnClickListener {

    private EditText edUsername, edPass, edConfirmPass, edMobile, edEmail, edLimit;
    private Spinner spType;
    private TextView tvSubmit, tvCancel, tvCompanyName;

    int uId = 0, isActive = 0, uType;
    String uName, uMobile, uEmail, uPass;
    float uLimit;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern;
    private Matcher matcher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        getActivity().setTitle("Add User");

        edUsername = view.findViewById(R.id.edAddUser_Username);
        edPass = view.findViewById(R.id.edAddUser_Password);
        edConfirmPass = view.findViewById(R.id.edAddUser_ConfirmPassword);
        edMobile = view.findViewById(R.id.edAddUser_Mobile);
        edEmail = view.findViewById(R.id.edAddUser_Email);
        edLimit = view.findViewById(R.id.edAddUser_Limit);
        spType = view.findViewById(R.id.spAddUser_Type);
        tvSubmit = view.findViewById(R.id.tvAddUser_Submit);
        tvCancel = view.findViewById(R.id.tvAddUser_Cancel);
        tvCompanyName = view.findViewById(R.id.tvAddUser_CompanyName);
        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);


        ArrayList<String> userTypeArray = new ArrayList<>();
        userTypeArray.add("Select User Type");
        userTypeArray.add("Approver");
        userTypeArray.add("Initiator");

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, userTypeArray);
        spType.setAdapter(typeAdapter);

        try {
            uId = getArguments().getInt("UserId");
            uName = getArguments().getString("UserName");
            uPass = getArguments().getString("UserPass");
            uMobile = getArguments().getString("UserMobile");
            uEmail = getArguments().getString("UserEmail");
            uLimit = getArguments().getFloat("UserLimit");
            isActive = getArguments().getInt("IsActive");
            uType = getArguments().getInt("UserType");

            edUsername.setText("" + uName);
            edPass.setText("" + uPass);
            edConfirmPass.setText("" + uPass);
            edMobile.setText("" + uMobile);
            edEmail.setText("" + uEmail);
            edLimit.setText("" + uLimit);

            if (uType == 0) {
                spType.setSelection(1);
            } else if (uType == 1) {
                spType.setSelection(2);
            }

        } catch (Exception e) {
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvAddUser_Submit) {
            if (spType.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please Select User Type", Toast.LENGTH_SHORT).show();
                spType.requestFocus();
            } else if (edUsername.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                edUsername.requestFocus();
            } else if (edPass.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                edPass.requestFocus();
            } else if (edConfirmPass.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                edConfirmPass.requestFocus();
            } else if (!edPass.getText().toString().equals(edConfirmPass.getText().toString())) {
                Toast.makeText(getActivity(), "Password Not Matched", Toast.LENGTH_SHORT).show();
                edConfirmPass.requestFocus();
            } else if (edMobile.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                edMobile.requestFocus();
            } else if (edMobile.getText().toString().length() != 10) {
                Toast.makeText(getActivity(), "Please Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                edMobile.requestFocus();
            } else if (edEmail.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter Email Id", Toast.LENGTH_SHORT).show();
                edEmail.requestFocus();
            } else if (!validateEmail(edEmail.getText().toString())) {
                Toast.makeText(getActivity(), "Please Enter Valid Email Id", Toast.LENGTH_SHORT).show();
                edEmail.requestFocus();
            } else if (edLimit.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Enter User Cash Limit", Toast.LENGTH_SHORT).show();
                edLimit.requestFocus();
            } else {

                int type = spType.getSelectedItemPosition() - 1;
                String name = edUsername.getText().toString();
                String pass = edPass.getText().toString();
                String mobile = edMobile.getText().toString();
                String email = edEmail.getText().toString();
                float limit = Float.parseFloat(edLimit.getText().toString());

                UserList bean = new UserList(uId, name, type, pass, mobile, email, isActive, 0, limit);
                if (uId > 0) {
                    editUserData(bean);
                } else {
                    insertUser(bean);
                }

            }


        } else if (v.getId() == R.id.tvAddUser_Cancel) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new UserManagementFragment(), "HomeFragment");
            ft.commit();
        }
    }

    public void insertUser(UserList mUser) {
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

        Call<ErrorMessage> errorMessageCall = api.insertUser(mUser);
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
                            ft.replace(R.id.content_frame, new UserManagementFragment(), "HomeFragment");
                            ft.commit();
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        //Log.e("AddUser : ", " NULL ");
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                    //Log.e("AddUser : ", " Exception : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editUserData(UserList mUser) {
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


        Call<ErrorMessage> errorMessageCall = api.editUser(mUser);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, new UserManagementFragment(), "HomeFragment");
                            ft.commit();
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                        //Log.e("AddUser : ", " NULL ");
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
                    //Log.e("AddUser : ", " Exception : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "Unable To Save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
