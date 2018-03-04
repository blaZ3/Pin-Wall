package com.example.downloader.network;

import android.util.Log;

import com.example.downloader.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vivek on 03/12/17.
 */

public class NetworkClient {
    private static final String TAG = NetworkClient.class.getSimpleName();

    static DownloadNetworkService  downloadNetworkService;

    public static DownloadNetworkService getNetworkService(){
        if (downloadNetworkService == null){
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.followRedirects(true);
            client.followSslRedirects(true);
            client.addNetworkInterceptor(interceptor);
            OkHttpClient httpClient = client.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Utils.ROOT)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            downloadNetworkService = retrofit.create(DownloadNetworkService.class);
        }

        return downloadNetworkService;
    }

    private static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.d(TAG, "intercept: " + request.url().toString());
            return chain.proceed(request);

        }
    };

}
