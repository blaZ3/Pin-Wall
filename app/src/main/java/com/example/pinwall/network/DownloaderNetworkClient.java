package com.example.pinwall.network;

import android.content.Context;

import com.example.downloader.Downloader;
import com.example.downloader.models.RequestInterface;
import com.example.pinwall.App;

/**
 * Created by vivek on 05/03/18.
 */

public class DownloaderNetworkClient implements NetworkClient {

    private Context context;

    static DownloaderNetworkClient instance;

    public static DownloaderNetworkClient getInstance(Context context){
        if (instance == null){
            instance = new DownloaderNetworkClient(context);
        }
        return instance;
    }

    private DownloaderNetworkClient(Context context){
        this.context = context;
    }

    @Override
    public void getFeed(RequestInterface.StringInterface stringInterface) {
        Downloader.with(context)
                .download(App.FEED_URL)
                .setCallback(stringInterface)
                .start();
    }
}
