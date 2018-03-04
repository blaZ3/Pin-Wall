package com.example.downloader.async;

import com.example.downloader.models.Request;

/**
 * Created by vivek on 04/03/18.
 */

public interface DownloaderTask {

    Runnable getRunnable();

}
