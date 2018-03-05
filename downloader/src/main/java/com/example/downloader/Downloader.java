package com.example.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.downloader.async.DownloadAction;
import com.example.downloader.cache.LocalMemoryCache;
import com.example.downloader.models.Request;
import com.example.downloader.models.RequestInterface;

/**
 * Created by vivek on 04/03/18.
 */

public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();

    private static Downloader instance;

    private DownloadManager downloadManager;

    private Downloader(Context context) {
        downloadManager = new DownloadManager(LocalMemoryCache.getInstance());
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
        downloadManager.submit(request);
    }


    static final int GOT_IMAGE = 2001;
    static final int GOT_JSON = 2002;
    static final int GOT_FILE = 2003;
    static final int GOT_STRING = 2004;
    static final int ERROR = 2020;
    static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GOT_IMAGE:
                    DownloaderMessage imgAction = (DownloaderMessage)msg.obj;
                    if (imgAction.request.getTag() instanceof ImageView){
                        ((ImageView)imgAction.request.getTag()).setImageBitmap(
                                ((DownloadAction.ImageDownloadAction)imgAction.action).getBitmap());
                    }else {
                        RequestInterface.ImageInterface imageCallback = ((RequestInterface.ImageInterface)imgAction.request.getCallback());
                        imageCallback.gotImage(((DownloadAction.ImageDownloadAction)imgAction.action).getBitmap());
                    }
                    break;
                case GOT_JSON:
                    DownloaderMessage jsonAction = (DownloaderMessage)msg.obj;
                    RequestInterface.JsonInterface imageCallback = (RequestInterface.JsonInterface)jsonAction.request.getCallback();
                    imageCallback.gotJson(((DownloadAction.JsonDownloadAction)jsonAction.action).getJsonElement());
                    break;
                case GOT_FILE:
                    DownloaderMessage fileAction = (DownloaderMessage) msg.obj;
                    RequestInterface.FileInterface fileCallback = (RequestInterface.FileInterface) fileAction.request.getCallback();
                    fileCallback.gotFile(((DownloadAction.FileDownloadAction)fileAction.action).getFile());
                    break;
                case GOT_STRING:
                    DownloaderMessage stringAction = (DownloaderMessage) msg.obj;
                    RequestInterface.StringInterface stringCallback = (RequestInterface.StringInterface) stringAction.request.getCallback();
                    stringCallback.gotString(((DownloadAction.StringDownloadAction)stringAction.action).getString());
                    break;
                case ERROR:
                    DownloaderMessage errorAction = (DownloaderMessage) msg.obj;
                    if (errorAction != null && errorAction.request.getCallback()!=null){
                        errorAction.request.getCallback()
                                .onError(new Exception("Error while fetching url:" + errorAction.request.getUrl()));
                    }
                    break;
            }
        }
    };

    static class DownloaderMessage {
        public Request request;
        public DownloadAction action;

        public DownloaderMessage(Request request, DownloadAction action) {
            this.request = request;
            this.action = action;
        }
    }

}
