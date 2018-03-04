package com.example.downloader.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

    class JsonDownloadAction implements DownloadAction{

        private String url;
        private String filePath;

        public JsonDownloadAction(String url, String filePath){
            this.url = url;
            this.filePath = filePath;
        }

        public JsonElement getJsonElement(){
            try  {
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                JsonParser parser = new JsonParser();
                JsonElement JsonElement = parser.parse(br);
                return JsonElement;
            }catch (Exception ex){
                return null;
            }
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public Type getType() {
            return Type.JSON;
        }
    }

    class FileDownloadAction implements DownloadAction{

        private String url;
        private String filePath;

        public FileDownloadAction(String url, String filePath){
            this.url = url;
            this.filePath = filePath;
        }

        public File getFile(){
            return new File(filePath);
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public Type getType() {
            return Type.FILE;
        }
    }
}
