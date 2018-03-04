package com.example.downloader;

import android.content.Context;

/**
 * Created by vivek on 04/03/18.
 */

public class Downloader {

    static Downloader instance;

    private Downloader(Context context) {

    }

    public static Downloader with(Context context){
        if (instance == null){
            synchronized (Downloader.class){
                instance = new Downloader(context);
            }
        }

        return instance;
    }


}
