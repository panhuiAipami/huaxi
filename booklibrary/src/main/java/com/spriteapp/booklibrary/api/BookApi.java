package com.spriteapp.booklibrary.api;

import android.util.Log;

import com.spriteapp.booklibrary.api.interceptor.HeaderInterceptor;
import com.spriteapp.booklibrary.api.interceptor.ResponseInterceptor;
import com.spriteapp.booklibrary.constant.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class BookApi {

    private static final int TIME_OUT = 8;
    private static BookApi instance;
    public BookApiService service;
    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            Log.d(">>>", message);

        }
    });

    private BookApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(BookApiService.class);
    }

    public static BookApi getInstance() {
        if (instance == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new HeaderInterceptor())
                    .addInterceptor(new ResponseInterceptor())
                    .build();
            instance = new BookApi(client);
        }
        return instance;
    }
}
