package com.ats.monginis_cash_mgmt.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.ats.monginis_cash_mgmt.activity.HomeActivity;
import com.ats.monginis_cash_mgmt.interfaces.InterfaceApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maxadmin on 19/12/17.
 */

public class Constants {

    public static final String MY_PREF = "PettyCash";

//    public static final String BASE_URL_1 = "http://132.148.143.124:8080/cashmgmt/";
//    public static final String BASE_URL_2 = "http://132.148.143.124:8080/mrg/";
//    public static final String BASE_URL_3 = "http://132.148.143.124:8080/gfpllogi/";
//    public static final String BASE_URL_4 = "http://132.148.143.124:8080/cp/";

    public static final String BASE_URL_1 = "http://132.148.148.215:8080/cashmgmt/";
//    public static final String BASE_URL_2 = "http://132.148.151.41:8080/mrg/";
//    public static final String BASE_URL_3 = "http://132.148.151.41:8080/gfpllogi/";
//    public static final String BASE_URL_4 = "http://132.148.151.41:8080/cp/";

//    public static final String BASE_URL_1 = "http://192.168.2.5:8089/";
//    public static final String BASE_URL_2 = "http://192.168.2.5:8089/";
//    public static final String BASE_URL_3 = "http://192.168.2.5:8089/";
//    public static final String BASE_URL_4 = "http://192.168.2.5:8089/";


    public static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())

                            .build();


                    Response response = chain.proceed(request);

                    // Inresponse=response.body().string();
                    // Customize or return the response
                    return response;
                }
            })
            .readTimeout(10000, TimeUnit.SECONDS)
            .connectTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .build();


    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(HomeActivity.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build();


    // public static InterfaceApi myInterface = retrofit.create(InterfaceApi.class);

    public static boolean isOnline(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(context, "No Internet Connection ! ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
