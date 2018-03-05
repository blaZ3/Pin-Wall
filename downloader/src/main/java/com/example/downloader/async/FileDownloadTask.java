package com.example.downloader.async;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import com.example.downloader.DownloadManager;
import com.example.downloader.FileHelper;
import com.example.downloader.network.NetworkClient;


import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * Created by vivek on 04/03/18.
 */

public class FileDownloadTask implements DownloaderTask{
    private static final String TAG = FileDownloadTask.class.getSimpleName();

    private String url;
    private FileHelper fileHelper;

    public FileDownloadTask(String url, FileHelper fileHelper) {
        this.url = url;
        this.fileHelper = fileHelper;
    }

    @Override
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {

                if (!Thread.interrupted()){
                    try {
                        retrofit2.Response<ResponseBody> response = NetworkClient.
                                getNetworkService().downloadFileUrl(url).execute();

                        Message message = DownloadManager.HANDLER.obtainMessage();

                        if (response.isSuccessful() && response.code() == 200){

                            String filePath = fileHelper.getFilePathForUrl(url);
                            fileHelper.writeResponseBodyToDisk(response.body(), filePath);

                            message.what = DownloadManager.FILE_DOWNLOAD_SUCCESS;

                            MediaType mediaType = response.body().contentType();
                            if (mediaType.toString().contains("image")){
                                message.obj = new DownloadAction.ImageDownloadAction(url, filePath);
                            }else if (mediaType.toString().contains("json")){
                                message.obj = new DownloadAction.JsonDownloadAction(url, filePath);
                            }else if (mediaType.toString().contains("text")){
                                message.obj = new DownloadAction.StringDownloadAction(url, filePath);
                            }else{
                                message.obj = new DownloadAction.FileDownloadAction(url, filePath);
                            }

                        }else {
                            message.what = DownloadManager.FILE_DOWNLOAD_FAILED;
                            message.obj = url;
                        }

                        DownloadManager.HANDLER.sendMessage(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Message message = DownloadManager.HANDLER.obtainMessage();
                        message.what = DownloadManager.FILE_DOWNLOAD_FAILED;
                        message.obj = url;

                        DownloadManager.HANDLER.sendMessage(message);
                    }
                }

            }
        };
    }


    @Override
    public String toString() {
        return "FileDownloadTask{" +
                "url='" + url + '\'' +
                '}';
    }
}