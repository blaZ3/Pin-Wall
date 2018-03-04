package com.example.downloader.async;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by vivek on 04/03/18.
 */

public class ThreadPoolExecutorHelper {
    private static final String TAG = ThreadPoolExecutorHelper.class.getSimpleName();

    static ThreadPoolExecutorHelper instance;

    ThreadPoolExecutor threadPoolExecutor;

    private ThreadPoolExecutorHelper() {
        Log.d(TAG, "ThreadPoolExecutorHelper() called");
        Log.d(TAG, "ThreadPoolExecutorHelper() called on threadId:" + Thread.currentThread().getId());
        int cores = Runtime.getRuntime().availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(
                cores,
                cores,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        Log.d(TAG, "ThreadPoolExecutorHelper() called inited threadPoolExecutor with "+cores+" cores");
    }


    public static ThreadPoolExecutorHelper getInstance(){
        if (instance == null){
            instance = new ThreadPoolExecutorHelper();
        }

        return instance;
    }


    public Future<?> addTask(DownloaderTask task){
        Log.d(TAG, "addTask() called with: task = [" + task + "]");
        Log.d(TAG, "addTask() called on threadId:" + Thread.currentThread().getId());
        if (threadPoolExecutor != null){
            return threadPoolExecutor.submit(task.getRunnable());
        }else {
            throw new IllegalStateException("Thread pool executor is not initialised yet!");
        }
    }
}