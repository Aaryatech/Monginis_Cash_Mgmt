package com.ats.monginis_cash_mgmt.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.MoneyOutBean;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;

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

import static android.content.Context.MODE_PRIVATE;
import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class RejectedEntryFragment extends Fragment implements View.OnClickListener {

    private ListView lvEntries;
    private TextView tvCompanyName, tvMoneyOut, tvTrSettle;

    int userId;
    private ArrayList<GetMoneyOutData> moneyOutEntryArray = new ArrayList<>();
    RejectionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rejected_entry, container, false);
        getActivity().setTitle("Rejected Entries");

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

        lvEntries = view.findViewById(R.id.lvRejectedEntry_List);
        tvCompanyName = view.findViewById(R.id.tvRejEntries_CompanyName);
        tvMoneyOut = view.findViewById(R.id.tvRejEntry_MoneyOut);
        tvTrSettle = view.findViewById(R.id.tvRejEntry_TrSettle);

        tvTrSettle.setOnClickListener(this);
        tvMoneyOut.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);


        getRejectionEntries();
        return view;
    }

    public void getRejectionEntries() {
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getRejectedEntries(userId);
        dataCall.enqueue(new Callback<ArrayList<GetMoneyOutData>>() {
            @Override
            public void onResponse(Call<ArrayList<GetMoneyOutData>> call, Response<ArrayList<GetMoneyOutData>> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();
                        moneyOutEntryArray.clear();
                        moneyOutEntryArray = response.body();

                        if (moneyOutEntryArray.size() > 0) {
                            for (int i = 0; i < moneyOutEntryArray.size(); i++) {
                            }
                        }

                        adapter = new RejectionAdapter(getContext(), moneyOutEntryArray);
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

    public void insertTransaction(MoneyOutBean moneyOutBean) {
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

        Call<ErrorMessage> errorMessageCall = api.insertSettleMoneyOut(moneyOutBean);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
                            Log.e("", "");
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            getRejectionEntries();
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(getActivity(), "Unable To Process", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvRejEntry_MoneyOut) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new MoneyOutFragment(), "HomeFragment");
            ft.commit();
        } else if (view.getId() == R.id.tvRejEntry_TrSettle) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new TransactionSettlementFragment(), "HomeFragment");
            ft.commit();
        }

    }

    //-----------------------------------------------------------------------------------
    //ADAPTER--------------------------------

    public class RejectionAdapter extends BaseAdapter {

        Context context;
        private ArrayList<GetMoneyOutData> originalValues;
        private ArrayList<GetMoneyOutData> displayedValues;
        LayoutInflater inflater;
        private Boolean isTouched = false;

        public RejectionAdapter(Context context, ArrayList<GetMoneyOutData> catArray) {
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

        public class ViewHolder {
            TextView tvName, tvDate, tvReApprove, tvGivenAmt, tvExpAmt, tvReturnAmt, tvBillAmt, tvRejReturnAmt;
            ImageView ivPhoto;
            LinearLayout llHead, llData;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            RejectionAdapter.ViewHolder holder = null;

            if (v == null) {
                v = inflater.inflate(R.layout.custom_rejected_entires_layout, null);
                holder = new RejectionAdapter.ViewHolder();
                holder.llHead = v.findViewById(R.id.llCustomReject_Head);
                holder.llData = v.findViewById(R.id.llCustomReject_Data);
                holder.tvName = v.findViewById(R.id.tvCustomReject_Name);
                holder.tvDate = v.findViewById(R.id.tvCustomReject_Date);
                holder.tvReApprove = v.findViewById(R.id.tvCustomReject_ReApprove);
                holder.tvGivenAmt = v.findViewById(R.id.tvCustomReject_GivenAmt);
                holder.tvReturnAmt = v.findViewById(R.id.tvCustomReject_ReturnAmt);
                holder.tvBillAmt = v.findViewById(R.id.tvCustomReject_BillAmt);
                holder.tvExpAmt = v.findViewById(R.id.tvCustomReject_ExpAmt);
                holder.tvRejReturnAmt = v.findViewById(R.id.tvCustomReject_RejReturnAmt);
                holder.ivPhoto = v.findViewById(R.id.ivCustomReject_Img);
                v.setTag(holder);
            } else {
                holder = (RejectionAdapter.ViewHolder) v.getTag();
            }

            holder.tvName.setText("" + displayedValues.get(position).getPersonName());

            String dt = displayedValues.get(position).getOutDate();
            String yr = dt.substring(0, 4);
            String mn = dt.substring(5, 7);
            String dy = dt.substring(8);
            holder.tvDate.setText(dy + "/" + mn + "/" + yr);

            holder.tvGivenAmt.setText("Given Amount: " + displayedValues.get(position).getOutAmt());
            holder.tvReturnAmt.setText("Return Amount: " + displayedValues.get(position).getReturnAmt());
            holder.tvBillAmt.setText("Bill Amount: " + (displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt()));
            holder.tvExpAmt.setText("Expected Amount: " + displayedValues.get(position).getExpAmt());
            holder.tvRejReturnAmt.setText("Rejected Return Amount: " + displayedValues.get(position).getRejReturnAmt());

           /* String image = displayedValues.get(position).getBillPhoto();
            try {

                Picasso.with(context)
                        .load(image)
                        .placeholder(R.drawable.default_img)
                        .error(R.drawable.default_img)
                        .into(holder.ivPhoto);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ApprovalAdapter : ", " Image : " + e.getMessage());
            }
*/


            final RejectionAdapter.ViewHolder finalHolder = holder;
            holder.llHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalHolder.llData.getVisibility() == View.GONE) {
                        finalHolder.llData.setVisibility(View.VISIBLE);
                    } else if (finalHolder.llData.getVisibility() == View.VISIBLE) {
                        finalHolder.llData.setVisibility(View.GONE);
                    }
                }
            });

          /*  holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("BillImage", displayedValues.get(position).getBillPhoto());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });*/

            holder.tvReApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
                    dialog.setContentView(R.layout.custom_re_approve_layout);

                    final TextView btnCancle = dialog.findViewById(R.id.tvCustomReApprove_Cancel);
                    final TextView btnReject = dialog.findViewById(R.id.tvCustomReApprove_ReApprove);
                    final EditText edRemark = dialog.findViewById(R.id.edCustomReApprove_RejRemark);
                    final EditText edExpAmt = dialog.findViewById(R.id.edCustomReApprove_ExpAmt);
                    final EditText edReApproveAmt = dialog.findViewById(R.id.edCustomReApprove_ReApproveAmt);

                    edRemark.setVisibility(View.GONE);

                    edExpAmt.setText("" + displayedValues.get(position).getExpAmt());
                    edReApproveAmt.setText("" + displayedValues.get(position).getExpAmt());
                    final float expAmt = displayedValues.get(position).getExpAmt();
                    final float billAmt = (displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt());

                    btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edReApproveAmt.getText().toString().isEmpty()) {
                                edReApproveAmt.setError("Required");
                                edReApproveAmt.requestFocus();
                            } else if (Float.parseFloat(edReApproveAmt.getText().toString()) > billAmt || (Float.parseFloat(edReApproveAmt.getText().toString()) < expAmt)) {
                                edReApproveAmt.setError("Amount Should Be Between Bill Amount And Expected Amount");
                                edReApproveAmt.requestFocus();
                            } else {
                                float reApproveAmt = Float.parseFloat(edReApproveAmt.getText().toString());
                                MoneyOutBean bean = new MoneyOutBean(displayedValues.get(position).getTrId(), displayedValues.get(position).getOutDate(), displayedValues.get(position).getOutDatetime(), displayedValues.get(position).getOutAmt(), displayedValues.get(position).getPersonId(), displayedValues.get(position).getDeptId(), displayedValues.get(position).getPurposeId(), displayedValues.get(position).getOutRemark(), displayedValues.get(position).getOutBy(), displayedValues.get(position).getIsSettle(), displayedValues.get(position).getSettleUserid(), displayedValues.get(position).getSettleDate(), displayedValues.get(position).getReturnAmt(), displayedValues.get(position).getReturnReason(), displayedValues.get(position).getIsBill(), displayedValues.get(position).getBillPhoto(), 0, displayedValues.get(position).getApprovalDate(), displayedValues.get(position).getApprovalDatetime(), userId, displayedValues.get(position).getRejReason(), displayedValues.get(position).getExpAmt(), reApproveAmt, displayedValues.get(position).getTrClosedAmt());
                                insertTransaction(bean);
                                dialog.dismiss();
                            }
                        }
                    });

                    btnCancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            return v;
        }

    }
}
