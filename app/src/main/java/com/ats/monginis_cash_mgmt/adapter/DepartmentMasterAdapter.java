package com.ats.monginis_cash_mgmt.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.bean.DepartmentList;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.fragment.DepartmentMasterFragment;
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

/**
 * Created by maxadmin on 19/12/17.
 */

public class DepartmentMasterAdapter extends BaseAdapter {

    Context context;
    private ArrayList<DepartmentList> originalValues;
    private ArrayList<DepartmentList> displayedValues;
    LayoutInflater inflater;

    public DepartmentMasterAdapter(Context context, ArrayList<DepartmentList> catArray) {
        this.context = context;
        this.originalValues = catArray;
        this.displayedValues = catArray;
        inflater = LayoutInflater.from(context);
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

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.custom_company_master_layout, null);

        TextView tvName = v.findViewById(R.id.tvCustomComp_Name);
        TextView tvBal = v.findViewById(R.id.tvCustomComp_Bal);
        ImageView ivPopup = v.findViewById(R.id.ivCustomComp_menu);

        tvBal.setVisibility(View.GONE);
        tvName.setText("" + displayedValues.get(position).getDeptName());

        ivPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.crud_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_edit) {

                            HomeActivity activity = (HomeActivity) context;
                            Fragment adf = new DepartmentMasterFragment();
                            Bundle args = new Bundle();
                            args.putString("DeptName", displayedValues.get(position).getDeptName());
                            args.putInt("DeptId", displayedValues.get(position).getDeptId());
                            adf.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "HomeFragment").commit();

                        } else if (menuItem.getItemId() == R.id.action_delete) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                            builder.setTitle("Confirm Action");
                            builder.setMessage("Do You Really Want To Delete Department?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDept(displayedValues.get(position).getDeptId());
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return v;
    }

    public void deleteDept(int deptId) {
        final CommonDialog commonDialog = new CommonDialog(context, "Loading", "Please Wait...");
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

        Call<ErrorMessage> errorMessageCall = api.deleteDepartment(deptId);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(context, "Unable To Delete", Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            HomeActivity activity = (HomeActivity) context;
                            Fragment adf = new DepartmentMasterFragment();
                            Bundle args = new Bundle();
                            args.putString("DeptName", "");
                            args.putInt("DeptId", 0);
                            adf.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, adf, "HomeFragment").commit();

                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(context, "Unable To Delete", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(context, "Unable To Delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(context, "Unable To Delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
