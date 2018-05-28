package com.ats.monginis_cash_mgmt.fragment;


import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.BuildConfig;
import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.bean.PersonListData;
import com.ats.monginis_cash_mgmt.bean.PurposeListData;
import com.ats.monginis_cash_mgmt.bean.UserListData;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class ReportsFragment extends Fragment implements View.OnClickListener {

    private EditText edFromDate, edToDate;
    private Spinner spPerson, spPurpose, spEnterBy;
    private TextView tvSubmit, tvFromDate, tvToDate,tvCompanyName;

    int yyyy, mm, dd;
    long fromDateMillis, toDateMillis;

    private ArrayList<String> userNameArray = new ArrayList<>();
    private ArrayList<Integer> userIdArray = new ArrayList<>();

    private ArrayList<String> personNameArray = new ArrayList<>();
    private ArrayList<Integer> personIdArray = new ArrayList<>();

    private ArrayList<String> purposeNameArray = new ArrayList<>();
    private ArrayList<Integer> purposeIdArray = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        getActivity().setTitle("Reports");

        edFromDate = view.findViewById(R.id.edReports_FromDate);
        edToDate = view.findViewById(R.id.edReports_ToDate);
        spPerson = view.findViewById(R.id.spReports_Person);
        spPurpose = view.findViewById(R.id.spReports_Purpose);
        spEnterBy = view.findViewById(R.id.spReports_EnterBy);
        tvSubmit = view.findViewById(R.id.tvReports_Submit);
        tvFromDate = view.findViewById(R.id.tvReports_FromDate);
        tvToDate = view.findViewById(R.id.tvReports_ToDate);   tvCompanyName = view.findViewById(R.id.tvReports_CompanyName);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        tvSubmit.setOnClickListener(this);
        edFromDate.setOnClickListener(this);
        edToDate.setOnClickListener(this);

        getPersonList();
        getPurposeList();
        getUserList();
//        getReport();

        return view;
    }


    public void getReport(String from, String to, int user, int person, int purpose) {
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

        Call<ResponseBody> responseBodyCall = api.getPdfReport(from, to, user, person, purpose);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e("Response : ", " ------ " + response.body());
                    if (writeResponseBodyToDisk(response.body())) {
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "No Report Found", Toast.LENGTH_SHORT).show();
                    }
                    commonDialog.dismiss();
                } catch (Exception e) {
                    commonDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                commonDialog.dismiss();
            }
        });
    }


    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory() + File.separator + "MonginisReport.pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.e("Reports : ", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    Log.e("File Path : ", " ---- " + futureStudioIconFile.getAbsolutePath());

//                    File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "MonginisReport.pdf");
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.fromFile(file1), "application/pdf");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);

                    File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "MonginisReport.pdf");
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(Uri.fromFile(file1), "application/pdf");
                    } else {
                        if (file1.exists()) {
                            String authorities = BuildConfig.APPLICATION_ID + ".provider";
                            Uri uri = FileProvider.getUriForFile(getContext(), authorities, file1);
                            intent.setDataAndType(uri, "application/pdf");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edReports_FromDate) {
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
        } else if (view.getId() == R.id.edReports_ToDate) {
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
        } else if (view.getId() == R.id.tvReports_Submit) {

            if (edFromDate.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Select From Date", Toast.LENGTH_SHORT).show();
                edFromDate.requestFocus();
            } else if (edToDate.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please Select To Date", Toast.LENGTH_SHORT).show();
                edToDate.requestFocus();
            } else {

                String from = tvFromDate.getText().toString();
                String to = tvToDate.getText().toString();
                int personId = personIdArray.get(spPerson.getSelectedItemPosition());
                int purposeId = purposeIdArray.get(spPurpose.getSelectedItemPosition());
                int enterById = userIdArray.get(spEnterBy.getSelectedItemPosition());

                getReport(from, to, enterById, personId, purposeId);

            }


        }
    }


    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
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

    private DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
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
                            userNameArray.clear();
                            userIdArray.clear();
                            userNameArray.add("All");
                            userIdArray.add(0);
                            for (int i = 0; i < data.getUserList().size(); i++) {
                                userNameArray.add(data.getUserList().get(i).getUserName());
                                userIdArray.add(data.getUserList().get(i).getUserId());
                            }

                            ArrayAdapter userAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, userNameArray);
                            spEnterBy.setAdapter(userAdapter);

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
                            personNameArray.add("All");
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
                            purposeNameArray.add("All");
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

}
