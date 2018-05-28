package com.ats.monginis_cash_mgmt.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.activity.ViewImageActivity;
import com.ats.monginis_cash_mgmt.bean.CashBalanceDataBean;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.MoneyOutBean;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

public class ApprovalFragment extends Fragment implements View.OnClickListener {

    private TextView tvRupees, tvApprove, tvReject, tvCompanyName, tvPhysicalRupees;
    private ListView lvList;
    private CheckBox cbAll;

    ApprovalAdapter adapter;

    int userId;

    private ArrayList<GetMoneyOutData> moneyOutEntryArray = new ArrayList<>();

    public static Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    private ArrayList<GetMoneyOutData> selectedIdArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approval, container, false);
        getActivity().setTitle("Approval");

        SharedPreferences pref = getContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json2, MUser.class);
        //Log.e("User Bean : ", "---------------" + userBean);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
            }
        } catch (Exception e) {
        }

        tvCompanyName = view.findViewById(R.id.tvApproval_CompanyName);
        tvRupees = view.findViewById(R.id.tvApproval_Rupees);
        tvApprove = view.findViewById(R.id.tvApprove_Approve);
        tvReject = view.findViewById(R.id.tvApprove_Reject);
        tvPhysicalRupees = view.findViewById(R.id.tvApproval_PhysicalRupees);
        lvList = view.findViewById(R.id.lvApproval_List);
        cbAll = view.findViewById(R.id.cbApproval);

        tvReject.setVisibility(View.GONE);
        tvApprove.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);

        getAllMoneyOutEntries();
        getCashBalanceData();

        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedIdArray.clear();
                    adapter = new ApprovalAdapter(getContext(), moneyOutEntryArray, 1);
                    lvList.setAdapter(adapter);
                } else {
                    selectedIdArray.clear();
                    adapter = new ApprovalAdapter(getContext(), moneyOutEntryArray, 0);
                    lvList.setAdapter(adapter);
                }
            }
        });

        return view;
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

        Call<ArrayList<GetMoneyOutData>> dataCall = api.getApprovalEntries();
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
                                map.put(moneyOutEntryArray.get(i).getTrId(), false);
                            }
                        }

                        adapter = new ApprovalAdapter(getContext(), moneyOutEntryArray, 0);
                        lvList.setAdapter(adapter);

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
                //Log.e("Money Out Entry : ", " ---Failure :  " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvApprove_Approve) {
            //Log.e("Ids List : ", " ------ " + selectedIdArray);

            if (selectedIdArray.size() > 0) {
                ArrayList<MoneyOutBean> beanArray = new ArrayList<>();
                for (int i = 0; i < selectedIdArray.size(); i++) {
                    try {
                        if (selectedIdArray.get(i).getRejReturnAmt() > 0) {
                            MoneyOutBean bean = new MoneyOutBean(selectedIdArray.get(i).getTrId(), selectedIdArray.get(i).getOutDate(), selectedIdArray.get(i).getOutDatetime(), selectedIdArray.get(i).getOutAmt(), selectedIdArray.get(i).getPersonId(), selectedIdArray.get(i).getDeptId(), selectedIdArray.get(i).getPurposeId(), selectedIdArray.get(i).getOutRemark(), selectedIdArray.get(i).getOutBy(), selectedIdArray.get(i).getIsSettle(), selectedIdArray.get(i).getSettleUserid(), selectedIdArray.get(i).getSettleDate(), selectedIdArray.get(i).getReturnAmt(), selectedIdArray.get(i).getReturnReason(), selectedIdArray.get(i).getIsBill(), selectedIdArray.get(i).getBillPhoto(), 1, "", "", userId, selectedIdArray.get(i).getRejReason(), selectedIdArray.get(i).getExpAmt(), selectedIdArray.get(i).getRejReturnAmt(), selectedIdArray.get(i).getRejReturnAmt());
                            beanArray.add(bean);
                        } else {
                            float trCloseAmt = selectedIdArray.get(i).getOutAmt() - selectedIdArray.get(i).getReturnAmt();
                            MoneyOutBean bean = new MoneyOutBean(selectedIdArray.get(i).getTrId(), selectedIdArray.get(i).getOutDate(), selectedIdArray.get(i).getOutDatetime(), selectedIdArray.get(i).getOutAmt(), selectedIdArray.get(i).getPersonId(), selectedIdArray.get(i).getDeptId(), selectedIdArray.get(i).getPurposeId(), selectedIdArray.get(i).getOutRemark(), selectedIdArray.get(i).getOutBy(), selectedIdArray.get(i).getIsSettle(), selectedIdArray.get(i).getSettleUserid(), selectedIdArray.get(i).getSettleDate(), selectedIdArray.get(i).getReturnAmt(), selectedIdArray.get(i).getReturnReason(), selectedIdArray.get(i).getIsBill(), selectedIdArray.get(i).getBillPhoto(), 1, "", "", userId, selectedIdArray.get(i).getRejReason(), selectedIdArray.get(i).getExpAmt(), selectedIdArray.get(i).getRejReturnAmt(), trCloseAmt);
                            beanArray.add(bean);
                        }
                    } catch (Exception e) {
                    }
                }
                insertApproveTransaction(beanArray);
            } else {
                Toast.makeText(getContext(), "Please Select Entries", Toast.LENGTH_SHORT).show();
            }
        }
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

        Call<ErrorMessage> errorMessageCall = api.insertMoneyOut(moneyOutBean);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable To Process" + message.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            getAllMoneyOutEntries();
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

    public void insertApproveTransaction(ArrayList<MoneyOutBean> moneyOutBean) {
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

        Call<ErrorMessage> errorMessageCall = api.insertApproveRejectMoneyOut(moneyOutBean);
        errorMessageCall.enqueue(new Callback<ErrorMessage>() {
            @Override
            public void onResponse(Call<ErrorMessage> call, Response<ErrorMessage> response) {
                try {
                    if (response.body() != null) {
                        ErrorMessage message = response.body();
                        if (message.getError()) {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable To Process" + message.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            getAllMoneyOutEntries();
                            getCashBalanceData();
                            cbAll.setChecked(false);
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

    public void getCashBalanceData() {
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

        Call<CashBalanceDataBean> dataCall = api.getCashBalance();
        dataCall.enqueue(new Callback<CashBalanceDataBean>() {
            @Override
            public void onResponse(Call<CashBalanceDataBean> call, Response<CashBalanceDataBean> response) {
                try {
                    if (response.body() != null) {
                        commonDialog.dismiss();

                        CashBalanceDataBean data = response.body();
                        tvRupees.setText("Rs. " + String.format("%.2f", data.getOpBalance()));
                        tvPhysicalRupees.setText("Rs. " + String.format("%.2f", data.getPhysicalCash()));


                    } else {
                        commonDialog.dismiss();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CashBalanceDataBean> call, Throwable t) {
                commonDialog.dismiss();
                t.printStackTrace();
            }
        });
    }


    //-----------------------------------------------------------------------------------
    //ADAPTER--------------------------------

    public class ApprovalAdapter extends BaseAdapter {

        Context context;
        private ArrayList<GetMoneyOutData> originalValues;
        private ArrayList<GetMoneyOutData> displayedValues;
        LayoutInflater inflater;
        int cbStatus;
        private Boolean isTouched = false;

        public ApprovalAdapter(Context context, ArrayList<GetMoneyOutData> catArray, int cbStatus) {
            this.context = context;
            this.originalValues = catArray;
            this.displayedValues = catArray;
            inflater = LayoutInflater.from(context);
            this.cbStatus = cbStatus;
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
            CheckBox cbCheck;
            TextView tvName, tvDate, tvReject, tvGivenAmt, tvReturnAmt, tvBillAmt, tvExpAmt, tvRejReturnAmt, tvPurpose, tvRemark;
            ImageView ivPhoto;
            LinearLayout llHead, llData, llExpAmt, llRejReturnAmt, llPurpose, llRemark;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            ViewHolder holder = null;

            if (v == null) {
                v = inflater.inflate(R.layout.custom_approval_layout, null);
                holder = new ViewHolder();
                holder.llHead = v.findViewById(R.id.llCustomApproval_Head);
                holder.llData = v.findViewById(R.id.llCustomApproval_Data);
                holder.tvName = v.findViewById(R.id.tvCustomApproval_Name);
                holder.tvDate = v.findViewById(R.id.tvCustomApproval_Date);
                holder.cbCheck = v.findViewById(R.id.cbCustomApproval);
                holder.tvReject = v.findViewById(R.id.tvCustomApproval_Reject);
                holder.tvGivenAmt = v.findViewById(R.id.tvCustomApproval_GivenAmt);
                holder.tvReturnAmt = v.findViewById(R.id.tvCustomApproval_ReturnAmt);
                holder.tvBillAmt = v.findViewById(R.id.tvCustomApproval_BillAmt);
                holder.tvExpAmt = v.findViewById(R.id.tvCustomApproval_ExpAmt);
                holder.tvRejReturnAmt = v.findViewById(R.id.tvCustomApproval_RejReturnAmt);
                holder.ivPhoto = v.findViewById(R.id.ivCustomApproval_Img);
                holder.llExpAmt = v.findViewById(R.id.llCustomApproval_ExpAmt);
                holder.llRejReturnAmt = v.findViewById(R.id.llCustomApproval_RejReturnAmt);
                holder.llPurpose = v.findViewById(R.id.llCustomApproval_Purpose);
                holder.llRemark = v.findViewById(R.id.llCustomApproval_Reason);
                holder.tvPurpose = v.findViewById(R.id.tvCustomApproval_Purpose);
                holder.tvRemark = v.findViewById(R.id.tvCustomApproval_Reason);

                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            Boolean value = map.get(displayedValues.get(position).getTrId());
            holder.cbCheck.setChecked(value);

            isTouched = false;
            holder.cbCheck.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isTouched = true;
                    return false;
                }
            });

            holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isTouched) {
                        isTouched = false;
                        if (isChecked) {
                            map.put(displayedValues.get(position).getTrId(), true);
                            selectedIdArray.add(displayedValues.get(position));
                        } else {
                            map.put(displayedValues.get(position).getTrId(), false);
                            selectedIdArray.remove(displayedValues.get(position));
                        }
                    }
                }
            });

            if (displayedValues.get(position).getExpAmt() > 0) {
                holder.llExpAmt.setVisibility(View.VISIBLE);
                holder.llRejReturnAmt.setVisibility(View.VISIBLE);
            }

            holder.llPurpose.setVisibility(View.VISIBLE);
            holder.llRemark.setVisibility(View.VISIBLE);

            holder.tvName.setText("" + displayedValues.get(position).getPersonName());

            holder.tvPurpose.setText("" + displayedValues.get(position).getPurpose());
            holder.tvRemark.setText("" + displayedValues.get(position).getOutRemark());

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

            String image = displayedValues.get(position).getBillPhoto();

            SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
            int company = pref.getInt("Company", 0);

            try {

                String IMAGE_URL = "";
                if (company == 1) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_1;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                } else if (company == 2) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_2;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                } else if (company == 3) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_3;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                } else if (company == 4) {
                    IMAGE_URL = InterfaceApi.IMAGE_URL_4;
                    Picasso.with(context)
                            .load(IMAGE_URL + image)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(holder.ivPhoto);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            if (cbStatus == 1) {
                holder.cbCheck.setChecked(true);
                selectedIdArray.add(displayedValues.get(position));
            }

            final ViewHolder finalHolder = holder;
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

            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("BillImage", displayedValues.get(position).getBillPhoto());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            holder.tvReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
                    dialog.setContentView(R.layout.custom_reject_transaction);

                    final TextView btnCancle = dialog.findViewById(R.id.tvCustomReject_Cancel);
                    final TextView btnReject = dialog.findViewById(R.id.tvCustomReject_Rej);
                    final EditText edRemark = dialog.findViewById(R.id.edCustomReject_RejRemark);
                    final EditText edExpAmt = dialog.findViewById(R.id.edCustomReject_ExpAmt);
                    final EditText edBillAmt = dialog.findViewById(R.id.edCustomReject_BillAmt);
                    final float billAmt = displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt();
                    edBillAmt.setText("" + (displayedValues.get(position).getOutAmt() - displayedValues.get(position).getReturnAmt()));
                    edExpAmt.setText("" + displayedValues.get(position).getExpAmt());

                    btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edExpAmt.getText().toString().isEmpty()) {
                                edExpAmt.setError("Required");
                                edExpAmt.requestFocus();
                            } else if (Float.parseFloat(edExpAmt.getText().toString()) > billAmt) {
                                Toast.makeText(getActivity(), "Expected Amount Should Not Be More Than Bill Amount", Toast.LENGTH_SHORT).show();
                                edExpAmt.requestFocus();
                            } else if (edRemark.getText().toString().isEmpty()) {
                                edRemark.setError("Required");
                                edRemark.requestFocus();
                            } else {
                                float expAmt = Float.parseFloat(edExpAmt.getText().toString());
                                String rejRemark = edRemark.getText().toString();
                                MoneyOutBean bean = new MoneyOutBean(displayedValues.get(position).getTrId(), displayedValues.get(position).getOutDate(), displayedValues.get(position).getOutDatetime(), displayedValues.get(position).getOutAmt(), displayedValues.get(position).getPersonId(), displayedValues.get(position).getDeptId(), displayedValues.get(position).getPurposeId(), displayedValues.get(position).getOutRemark(), displayedValues.get(position).getOutBy(), displayedValues.get(position).getIsSettle(), displayedValues.get(position).getSettleUserid(), displayedValues.get(position).getSettleDate(), displayedValues.get(position).getReturnAmt(), displayedValues.get(position).getReturnReason(), displayedValues.get(position).getIsBill(), displayedValues.get(position).getBillPhoto(), 2, "", "", userId, rejRemark, expAmt, displayedValues.get(position).getRejReturnAmt(), displayedValues.get(position).getTrClosedAmt());
                                ArrayList<MoneyOutBean> beanArray = new ArrayList<>();
                                beanArray.add(bean);
                                insertApproveTransaction(beanArray);
//                                insertTransaction(bean);
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
