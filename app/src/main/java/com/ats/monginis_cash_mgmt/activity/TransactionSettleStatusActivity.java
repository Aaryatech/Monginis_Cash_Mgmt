package com.ats.monginis_cash_mgmt.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.IslamicCalendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.BuildConfig;
import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.bean.ErrorMessage;
import com.ats.monginis_cash_mgmt.bean.GetMoneyOutData;
import com.ats.monginis_cash_mgmt.bean.MUser;
import com.ats.monginis_cash_mgmt.bean.MoneyOutBean;
import com.ats.monginis_cash_mgmt.common.CommonDialog;
import com.ats.monginis_cash_mgmt.constants.Constants;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;
import com.ats.monginis_cash_mgmt.util.PermissionUtil;
import com.ats.monginis_cash_mgmt.util.RealPathUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ats.monginis_cash_mgmt.activity.HomeActivity.BASE_URL;

public class TransactionSettleStatusActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edReturnAmt, edTotalAmt, edReason, edGivenAmt;
    Spinner spBill;
    TextView tvUploadBill, tvBillName, tvSubmit, tvCancel, tvCompanyName;
    LinearLayout llUpload;
    ImageView ivBillImage, ivCancelImage;

    public static String path, imagePath;

    File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "MonginisMgmt");
    File f;

    private int trId, personId, deptId, purposeId, outBy, isSettle, settleUserid, isBill, isApproved, approvedBy;
    private String outDate, outTime, outRemark, settleDate, returnReason, billPhoto, approvalDate, approvalDatetime, rejReason;
    private float outAmt, returnAmt, expAmt, rejReturnAmt, trClosedAmt;

    int userId;

    Bitmap myBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_settle_status);

        if (PermissionUtil.checkAndRequestPermissions(this)) {

        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userData", "");
        MUser userBean = gson.fromJson(json, MUser.class);
        try {
            if (userBean != null) {
                userId = userBean.getUserId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            //Log.e("Transaction Bean", "  : " + getIntent().getExtras().getString("TransactionBean"));
            String json2 = getIntent().getExtras().getString("TransactionBean");
            GetMoneyOutData beanData = gson.fromJson(json2, GetMoneyOutData.class);
            if (beanData != null) {
                trId = beanData.getTrId();
                outDate = beanData.getOutDate();
                outTime = beanData.getOutDatetime();
                outAmt = beanData.getOutAmt();
                personId = beanData.getPersonId();
                deptId = beanData.getDeptId();
                purposeId = beanData.getPurposeId();
                outRemark = beanData.getOutRemark();
                outBy = beanData.getOutBy();
                isSettle = beanData.getIsSettle();
                settleUserid = beanData.getSettleUserid();
                settleDate = beanData.getSettleDate();
                returnAmt = beanData.getReturnAmt();
                returnReason = beanData.getReturnReason();
                isBill = beanData.getIsBill();
                billPhoto = beanData.getBillPhoto();
                isApproved = beanData.getIsApproved();
                approvalDate = beanData.getApprovalDate();
                approvalDatetime = beanData.getApprovalDatetime();
                approvedBy = beanData.getApprovedBy();
                rejReason = beanData.getRejReason();
                expAmt = beanData.getExpAmt();
                rejReturnAmt = beanData.getRejReturnAmt();
                trClosedAmt = beanData.getTrClosedAmt();
            }
        } catch (Exception e) {
            //Log.e("Exception : ", "--" + e.getMessage());
        }

        tvCompanyName = findViewById(R.id.tvTranscSettleActivity_CompanyName);
        llUpload = findViewById(R.id.llTrSettle_Upload);
        edReturnAmt = findViewById(R.id.edTrSettle_ReturnAmt);
        edTotalAmt = findViewById(R.id.edTrSettle_TotalAmount);
        edGivenAmt = findViewById(R.id.edTrSettle_GivenAmt);
        edReason = findViewById(R.id.edTrSettle_Reason);
        spBill = findViewById(R.id.spTrSettle_Bill);
        tvUploadBill = findViewById(R.id.tvTrSettle_UploadBill);
        tvBillName = findViewById(R.id.tvTrSettle_BillName);
        tvSubmit = findViewById(R.id.tvTrSettle_Submit);
        tvCancel = findViewById(R.id.tvTrSettle_Cancel);
        tvUploadBill.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        ivBillImage = findViewById(R.id.ivTrSettle_Image);
        ivCancelImage = findViewById(R.id.ivTrSettle_CancelImage);
        ivBillImage.setOnClickListener(this);
        ivCancelImage.setOnClickListener(this);

        tvCompanyName.setText(HomeActivity.COMPANY_NAME);
        edGivenAmt.setText("" + outAmt);
        edReturnAmt.setText("0");
        edTotalAmt.setText("" + (outAmt - returnAmt));

        createFolder();

        ArrayList<String> billStatus = new ArrayList<>();
        //billStatus.add("Select Bill Status");
        billStatus.add("Yes");
        billStatus.add("No");

        ArrayAdapter spAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, billStatus);
        spBill.setAdapter(spAdapter);

        spBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    llUpload.setVisibility(View.GONE);
//                } else
                if (position == 0) {
                    llUpload.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    llUpload.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edReturnAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    float returnAmt = Float.parseFloat(edReturnAmt.getText().toString());
                    edTotalAmt.setText("" + (outAmt - returnAmt));

                } catch (Exception e) {
                    edTotalAmt.setText("" + outAmt);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void createFolder() {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvTrSettle_UploadBill) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Choose");
            builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent pictureActionIntent = null;
                    pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pictureActionIntent, 101);
                }
            });
            builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            f = new File(folder + File.separator, "Camera.jpg");

                            String authorities = BuildConfig.APPLICATION_ID + ".provider";
                            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), authorities, f);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(intent, 102);

                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            f = new File(folder + File.separator, "Camera.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(intent, 102);

                        }
                    } catch (Exception e) {
                        ////Log.e("select camera : ", " Exception : " + e.getMessage());
                    }
                }
            });
            builder.show();


        } else if (v.getId() == R.id.tvTrSettle_Submit) {
            boolean isValidate = false;
            if (edReturnAmt.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Return Amount", Toast.LENGTH_SHORT).show();
                edReturnAmt.requestFocus();
            } else if (Float.parseFloat(edReturnAmt.getText().toString()) > outAmt) {
                Toast.makeText(this, "Return Amount Should Not Be Greater Than Given Amount", Toast.LENGTH_SHORT).show();
                edReturnAmt.requestFocus();
            } else if (spBill.getSelectedItemPosition() == 0) {
                if (imagePath == null) {
                    Toast.makeText(this, "Please Upload Bill Image", Toast.LENGTH_SHORT).show();
                    tvBillName.requestFocus();
                } else {
                    isValidate = true;
                }
            } else if (spBill.getSelectedItemPosition() == 1) {
                if (edReason.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please Enter Reason", Toast.LENGTH_SHORT).show();
                    edReason.requestFocus();
                } else {
                    isValidate = true;
                }
            }

            if (isValidate) {
                float retAmt = Float.parseFloat(edReturnAmt.getText().toString());
                String reason = edReason.getText().toString();
                String image;
                int billStatus = spBill.getSelectedItemPosition();

                if (imagePath == null) {
                    imagePath = "";
                }

                if (billStatus == 0) {
                    image = imagePath;
                } else {
                    image = "";
                }


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                String currDate = dateFormat.format(cal.getTime());

                Log.e("OutDate : " + outDate, "    OutTime : " + outTime);

                MoneyOutBean bean = new MoneyOutBean(trId, outDate, outTime, outAmt, personId, deptId, purposeId, outRemark, outBy, 1, userId, currDate, retAmt, reason, billStatus, image, isApproved, approvalDate, approvalDatetime, approvedBy, rejReason, expAmt, rejReturnAmt, trClosedAmt);
                // insertTransaction(bean);


                if (imagePath.isEmpty()) {
                    Log.e("ImagePath : ", "------- Empty");
                    insertTransaction(bean);
                    Log.e("ImagePath : ", "------- Empty ---- Bean : " + bean);

                } else {
                    if (!billPhoto.isEmpty()) {
                        Log.e("BillPhoto : ", "-------  : " + billPhoto);
                        bean.setBillPhoto(billPhoto);
                        sendImage(billPhoto, bean);
                    } else {
                        Log.e("BillPhoto : ", "-------  Empty ");

                        File imgFile = new File(imagePath);
                        int pos = imgFile.getName().lastIndexOf(".");
                        String ext = imgFile.getName().substring(pos + 1);
                        billPhoto = trId + "_" + System.currentTimeMillis() + "." + ext;

                        Log.e("BillPhoto : ", "-------  Name :  " + billPhoto);


                        bean.setBillPhoto(billPhoto);
                        sendImage(billPhoto, bean);
                    }
                }

            }


        } else if (v.getId() == R.id.tvTrSettle_Cancel) {
            finish();
        } else if (v.getId() == R.id.ivTrSettle_Image) {
            if (myBitmap != null) {
                showFullScreenImagePreview(myBitmap);
            }
        } else if (v.getId() == R.id.ivTrSettle_CancelImage) {
            myBitmap = null;
            ivBillImage.setImageBitmap(myBitmap);
            imagePath = "";
        }
    }

    //--------------------------IMAGE-----------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String realPath;
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK && requestCode == 102) {
            try {
                String path = f.getAbsolutePath();
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivBillImage.setImageBitmap(myBitmap);
                    //Log.e("Camera : ", " IMAGE SIZE : " + myBitmap.getByteCount());

                    myBitmap = shrinkBitmap(imgFile.getAbsolutePath(), 720, 720);
                    //Log.e("Camera : ", "Shrink IMAGE SIZE : " + myBitmap.getByteCount());

                    try {
                        FileOutputStream out = new FileOutputStream(path);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        Log.e("Image Saved  ", "---------------");

                    } catch (Exception e) {
                        Log.e("Exception : ", "--------" + e.getMessage());
                        e.printStackTrace();
                    }
                }
                imagePath = f.getAbsolutePath();
                tvBillName.setText("" + f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == 101) {
            try {
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                Uri uriFromPath = Uri.fromFile(new File(realPath));
                myBitmap = getBitmapFromCameraData(data, this);

                ivBillImage.setImageBitmap(myBitmap);
                imagePath = uriFromPath.getPath();
                tvBillName.setText("" + uriFromPath.getPath());
                //Log.e("Real Path : ", " ---------- " + realPath);

                try {

                    FileOutputStream out = new FileOutputStream(uriFromPath.getPath());
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    //Log.e("Image Saved  ", "---------------");

                } catch (Exception e) {
                    // Log.e("Exception : ", "--------" + e.getMessage());
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Log.e("Image Compress : ", "-----Exception : ------" + e.getMessage());
            }
        }
    }


    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        String picturePath = cursor.getString(columnIndex);
        path = picturePath;
        cursor.close();

        Bitmap bitm = shrinkBitmap(picturePath, 720, 720);
        Log.e("Image Size : ---- ", " " + bitm.getByteCount());

        return bitm;
        // return BitmapFactory.decodeFile(picturePath, options);
    }

    public static Bitmap shrinkBitmap(String file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    public void showFullScreenImagePreview(Bitmap bitmap) {
        final Dialog picDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        picDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picDialog.setContentView(R.layout.preview_image);
        ImageView ivPreview = (ImageView) picDialog.findViewById(R.id.ivPreviewImage);
        ivPreview.setImageBitmap(bitmap);
       /* //Log.e("Image : ", "" + url);
        Picasso.with(getContext())
                .load(url)
                .placeholder(R.mipmap.logo)
                .error(R.drawable.noimage)
                .into(ivPreview);*/

        picDialog.show();
    }

   /* public static Bitmap decodeFile(String pathName) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
            try {
                bitmap = BitmapFactory.decodeFile(pathName, options);
                Log.e("DecodeFile : ", "Decoded successfully for sampleSize " + options.inSampleSize);

                break;
            } catch (OutOfMemoryError outOfMemoryError) {
                Log.e("DecodeFile : ", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize
                        + " retrying with higher value");
            }
        }
        return bitmap;
    }*/


    public void insertTransaction(MoneyOutBean moneyOutBean) {
        final CommonDialog commonDialog = new CommonDialog(this, "Loading", "Please Wait...");
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
                            Toast.makeText(TransactionSettleStatusActivity.this, "Unable To Process", Toast.LENGTH_SHORT).show();
                            //Log.e("", "");
                        } else {
                            commonDialog.dismiss();
                            Toast.makeText(TransactionSettleStatusActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    } else {
                        commonDialog.dismiss();
                        Toast.makeText(TransactionSettleStatusActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    commonDialog.dismiss();
                    Toast.makeText(TransactionSettleStatusActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                    //Log.e("Transaction : ", " Exception : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ErrorMessage> call, Throwable t) {
                commonDialog.dismiss();
                Toast.makeText(TransactionSettleStatusActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendImage(String filename, final MoneyOutBean bean) {
        final CommonDialog commonDialog = new CommonDialog(this, "Loading", "Please Wait...");
        commonDialog.show();

        //Log.e("IMAGE PATH : ", "-------- : " + imagePath);

        File imgFile = new File(imagePath);


//        try {
//            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 2, new FileOutputStream(imgFile));
//            Log.e("Image Compress","-------");
//        } catch (Throwable t) {
//            Log.e("ERROR", "Error compressing file." + t.toString());
//            t.printStackTrace();
//        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFile.getName(), requestFile);

        RequestBody imgName = RequestBody.create(MediaType.parse("text/plain"), filename);

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

        Call<JSONObject> call = api.imageUpload(body, imgName);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                insertTransaction(bean);
                imagePath = "";
                Log.e("Response : ", "--" + response.body());
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Error : ", "--" + t.getMessage());
                commonDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(TransactionSettleStatusActivity.this, "Unable To Process", Toast.LENGTH_SHORT).show();
            }
        });
    }
//
}
