package com.mvvm.kipl.mvvmdemo;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mvvm.kipl.mvvmdemo.data.api.ApiService;
import com.mvvm.kipl.mvvmdemo.util.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Admin on 2/21/2018.
 */

public class Application extends android.app.Application {

    private ApiService apiService;

    @Override
    public void onCreate() {
        super.onCreate();


        Gson gson = new GsonBuilder().create();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES)
            .connectionPool(new ConnectionPool(0, 5 * 60 * 1000, TimeUnit.SECONDS)).addInterceptor(httpLoggingInterceptor).build();

        //Init retrofit
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        apiService = retrofit.create(ApiService.class);


    }

    public ApiService getApiService() {
        return apiService;
    }
}
