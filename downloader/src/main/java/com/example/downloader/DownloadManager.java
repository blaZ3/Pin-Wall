package com.example.downloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.example.downloader.async.DownloadAction;
import com.example.downloader.async.DownloaderTask;
import com.example.downloader.async.FileDownloadTask;
import com.example.downloader.async.ThreadPoolExecutorHelper;
import com.example.downloader.cache.LocalCache;
import com.example.downloader.models.Request;
import com.google.gson.JsonElement;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vivek on 04/03/18.
 */

/**
 *
 *
 * Download manager handles the actual downloading of the file, it checks if the
 * resource is available in the mem cache and starts an executor if necessary
 *
 * RUNS ON A DIFFERENT THREAD
 *
 */
public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();

    private DownloadManagerThread downloadManagerThread;

    public static Handler HANDLER;

    private ThreadPoolExecutorHelper threadPoolExecutorHelper;

    private HashMap<Object, Request> currentRequests = new HashMap<>();
    private HashMap<String, DownloaderTask> currentTasks = new HashMap<>();

    private LocalCache localCache;

    public static final int FILE_DOWNLOAD_SUCCESS = 1001;
    public static final int FILE_DOWNLOAD_FAILED = 1002;

    public DownloadManager(LocalCache localCache) {
        this.localCache = localCache;

        downloadManagerThread = new DownloadManagerThread();
        downloadManagerThread.start();

        HANDLER = new DownloadManagerThreadHandler(downloadManagerThread.getLooper());

        threadPoolExecutorHelper = ThreadPoolExecutorHelper.getInstance();
    }

    public void cancel(String tag) {
        if (currentRequests.containsKey(tag)){

        }
    }

    class DownloadManagerThread extends HandlerThread{
        public DownloadManagerThread() {
            super(Utils.THREAD_PREFIX + TAG, Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    class DownloadManagerThreadHandler extends Handler{
        public DownloadManagerThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadAction action = null;
            if (msg.obj instanceof DownloadAction){
                action = (DownloadAction) msg.obj;
            }

            switch (msg.what){
                case FILE_DOWNLOAD_SUCCESS:
                    synchronized (currentRequests){
                        ArrayList<Object> tags = new ArrayList<>();

                        for (Request request: currentRequests.values()){

                            Log.d(TAG, "handleMessage: req url" + request.getUrl());
                            Log.d(TAG, "handleMessage: action url" + action.getUrl());

                            if (request.getUrl().equals(action.getUrl())){
                                tags.add(request.getTag());

                                Message message = Downloader.MAIN_HANDLER.obtainMessage();
                                if (action.getType().equals(DownloadAction.Type.IMAGE)){
                                    message.what = Downloader.GOT_IMAGE;
                                    //add to cache
                                    localCache.putValueToCache(request.getUrl(),
                                            ((DownloadAction.ImageDownloadAction)action).getBitmap());
                                }else if (action.getType().equals(DownloadAction.Type.JSON)){
                                    message.what = Downloader.GOT_JSON;
                                    //add to cache
                                    localCache.putValueToCache(request.getUrl(),
                                            ((DownloadAction.JsonDownloadAction)action).getJsonElement());
                                }else if (action.getType().equals(DownloadAction.Type.STRING)){
                                    message.what = Downloader.GOT_STRING;
                                    localCache.putValueToCache(request.getUrl(),
                                            ((DownloadAction.StringDownloadAction)action).getString());
                                }else{
                                    message.what = Downloader.GOT_FILE;
                                }

                                message.obj = new Downloader.DownloaderMessage(request, action);
                                Downloader.MAIN_HANDLER.sendMessage(message);

                                currentTasks.remove(request.getUrl());
                            }
                        }

                        for (Object tag : tags){
                            currentRequests.remove(tag);
                        }

                        currentRequests.notifyAll();
                    }

                    break;
                case FILE_DOWNLOAD_FAILED:
                    synchronized (currentRequests){
                        ArrayList<Object> tags = new ArrayList<>();
                        ArrayList<String> urls = new ArrayList<>();

                        for (Request request: currentRequests.values()){
                            if (request.getUrl().equals((String) msg.obj)){

                                tags.add(request.getTag());
                                urls.add(request.getUrl());

                                Message message = Downloader.MAIN_HANDLER.obtainMessage();
                                message.what = Downloader.ERROR;
                                message.obj = new Downloader.DownloaderMessage(request, action);
                                Downloader.MAIN_HANDLER.sendMessage(message);
                            }

                        }

                        for (Object tag:tags){
                            currentRequests.remove(tag);
                        }

                        for (String url:urls){
                            currentTasks.remove(url);
                        }

                        currentRequests.notifyAll();
                    }
                    break;
            }
        }
    }


    public void submit(Request request){
        Log.d(TAG, "submit() called with: request = [" + request + "]");
        //check if result is already in the cache
        Object cacheValue = localCache.getValueFromCache(request.getUrl());
        if (cacheValue != null){
            Log.d(TAG, "submit: got key in cache");
            if (cacheValue instanceof Bitmap){
                Message message = Downloader.MAIN_HANDLER.obtainMessage();
                message.what = Downloader.GOT_IMAGE;
                Downloader.DownloaderMessage downloaderMessage = new Downloader
                        .DownloaderMessage(request,
                        new DownloadAction.ImageDownloadAction(request.getUrl(), (Bitmap) cacheValue));
                message.obj = downloaderMessage;
                Downloader.MAIN_HANDLER.sendMessage(message);
            }else if (cacheValue instanceof JsonElement){
                Message message = Downloader.MAIN_HANDLER.obtainMessage();
                message.what = Downloader.GOT_JSON;
                Downloader.DownloaderMessage downloaderMessage = new Downloader
                        .DownloaderMessage(request,
                        new DownloadAction.JsonDownloadAction(request.getUrl(), (JsonElement) cacheValue));
                message.obj = downloaderMessage;
                Downloader.MAIN_HANDLER.sendMessage(message);
            }else if (cacheValue instanceof String){
                Message message = Downloader.MAIN_HANDLER.obtainMessage();
                message.what = Downloader.GOT_STRING;
                Downloader.DownloaderMessage downloaderMessage = new Downloader
                        .DownloaderMessage(request,
                        new DownloadAction.StringDownloadAction(request.getUrl(), (String) cacheValue, true));
                message.obj = downloaderMessage;
                Downloader.MAIN_HANDLER.sendMessage(message);
            }else {
                submitAndEnqueue(request);
            }
        }else {
            submitAndEnqueue(request);
        }
    }

    private void submitAndEnqueue(Request request){
        Log.d(TAG, "submitAndEnqueue() called with: request = [" + request + "]");
        //check if the request is already in process
        if (currentRequests.containsKey(request.getTag())){
            //if tag already exists dont do anything
            Log.d(TAG, "submitAndEnqueue: duplicate request tag:"+request.getTag()+" already queued");
        }else {
            Log.d(TAG, "submitAndEnqueue: adding request to queue:"+request.getTag());
            if (!currentTasks.containsKey(request.getUrl())){
                FileDownloadTask fileDownloadTask = new FileDownloadTask(request.getUrl(), FileHelper.getInstance());
                request.setFuture(threadPoolExecutorHelper.addTask(fileDownloadTask));
                synchronized (currentTasks){
                    Log.d(TAG, "submitAndEnqueue: adding task to queue"+request.getUrl());
                    currentTasks.put(request.getUrl(), fileDownloadTask);
                    currentTasks.notifyAll();
                }
            }else {
                Log.d(TAG, "submitAndEnqueue: url already in queue");
            }
            synchronized (currentRequests){
                currentRequests.put(request.getTag(), request);
                currentRequests.notifyAll();
            }
        }
    }


}
