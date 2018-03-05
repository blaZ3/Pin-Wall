package com.example.downloader;

import android.content.Context;
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
import com.example.downloader.models.Request;


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


    public static final int FILE_DOWNLOAD_SUCCESS = 1001;
    public static final int FILE_DOWNLOAD_FAILED = 1002;

    public DownloadManager(Context context) {
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
            switch (msg.what){
                case FILE_DOWNLOAD_SUCCESS:
                    DownloadAction action = (DownloadAction) msg.obj;

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
                                }else if (action.getType().equals(DownloadAction.Type.JSON)){
                                    message.what = Downloader.GOT_JSON;
                                }else if (action.getType().equals(DownloadAction.Type.STRING)){
                                    message.what = Downloader.GOT_STRING;
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
                    Message message = Downloader.MAIN_HANDLER.obtainMessage();
                    message.what = Downloader.ERROR;
                    Downloader.MAIN_HANDLER.sendMessage(message);
                    break;
            }
        }
    }


    public void submitAndEnqueue(Request request){
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
                    currentTasks.put(Utils.getMD5Hash(request.getUrl()), fileDownloadTask);
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
