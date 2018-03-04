package com.example.downloader.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by vivek on 04/03/18.
 */

public interface DownloadAction {

    enum Type{
        IMAGE,
        JSON,
        FILE
    }


    Type getType();
    String getUrl();

    class ImageDownloadAction implements DownloadAction{

        private String url;
        private String filePath;

        public ImageDownloadAction(String url, String filePath){
            this.url = url;
            this.filePath = filePath;
        }

        public Bitmap getBitmap(){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            return bitmap;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public Type getType() {
            return Type.IMAGE;
        }
    }

}
