package com.example.downloader.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by vivek on 04/03/18.
 */

public interface DownloadAction {

    enum Type{
        IMAGE,
        JSON,
        FILE,
        STRING
    }

    Type getType();
    String getUrl();

    class ImageDownloadAction implements DownloadAction{

        private String url;
        private String filePath;
        private Bitmap bitmap;

        public ImageDownloadAction(String url, String filePath){
            this.url = url;
            this.filePath = filePath;
        }

        public ImageDownloadAction(String url, Bitmap bitmap){
            this.url = url;
            this.bitmap = bitmap;
            this.filePath = null;
        }

        public Bitmap getBitmap(){
            if (bitmap == null && filePath!=null){
                bitmap = BitmapFactory.decodeFile(filePath);
            }
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
        private JsonElement jsonElement;

        public JsonDownloadAction(String url, String filePath){
            this.url = url;
            this.filePath = filePath;
        }

        public JsonDownloadAction(String url, JsonElement jsonElement){
            this.url = url;
            this.jsonElement = jsonElement;
            this.filePath = null;
        }

        public JsonElement getJsonElement(){
            if (jsonElement == null && filePath != null){
                try  {
                    BufferedReader br = new BufferedReader(new FileReader(filePath));
                    JsonParser parser = new JsonParser();
                    jsonElement = parser.parse(br);

                }catch (Exception ex){
                    jsonElement = null;
                }
            }
            return jsonElement;
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

    class StringDownloadAction implements DownloadAction{

        private String url;
        private String filePath;

        public StringDownloadAction(String url, String filePath){
            this.url = url;
            this.filePath = filePath;
        }

        public String getString(){
            FileInputStream fin = null;
            try  {
                File fl = new File(filePath);
                fin = new FileInputStream(fl);
                String ret = convertStreamToString(fin);
                return ret;
            }catch (Exception ex){
                return null;
            }finally {
                //Make sure you close all streams.
                if (fin!=null){
                    try {
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        String convertStreamToString(InputStream is) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        }


        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public Type getType() {
            return Type.STRING;
        }
    }
}
