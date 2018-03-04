package com.example.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.downloader.async.DownloadAction;
import com.example.downloader.models.Request;
import com.example.downloader.models.RequestInterface;

/**
 * Created by vivek on 04/03/18.
 */

public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();

//    public static final Handler HANDLER = new Handler(Looper.getMainLooper()){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };


    private static Downloader instance;

    private DownloadManager downloadManager;

    private Downloader(Context context) {
        downloadManager = new DownloadManager(context);
    }

    public static Downloader with(Context context){
        if (instance == null){
            synchronized (Downloader.class){
                instance = new Downloader(context);
            }
        }
        return instance;
    }


    public Request.RequestBuilder download(String url){
        if (!TextUtils.isEmpty(url)){
            return new Request.RequestBuilder(this, url);
        }
        throw new IllegalArgumentException("Url cannot be empty or null");
    }

    public void cancel(String tag){
        if (!TextUtils.isEmpty(tag)){
            downloadManager.cancel(tag);
        }else {
            throw new IllegalArgumentException("tag cannot be empty or null");
        }
    }


    public void submit(Request request) {
        downloadManager.submitAndEnqueue(request);
    }


    static final int SHOW_IMAGE = 2001;
    static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_IMAGE:
                    DownloaderMainAction mainAction = (DownloaderMainAction)msg.obj;
                    if (mainAction.request.getTag() instanceof ImageView){
                        ((ImageView)mainAction.request.getTag()).setImageBitmap(
                                ((DownloadAction.ImageDownloadAction)mainAction.action).getBitmap());
                    }else {
                        ((RequestInterface.ImageInterface)mainAction.request.getCallback())
                                .gotImage(((DownloadAction.ImageDownloadAction)
                                        mainAction.action).getBitmap());
                    }
                    break;
            }
        }
    };

    static class DownloaderMainAction{
        public Request request;
        public DownloadAction action;
        public RequestInterface callback;

        public DownloaderMainAction(Request request, DownloadAction action) {
            this.request = request;
            this.action = action;
        }
    }

}
