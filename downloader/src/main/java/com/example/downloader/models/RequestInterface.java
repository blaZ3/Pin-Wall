package com.example.downloader.models;

import android.graphics.Bitmap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Created by vivek on 04/03/18.
 */

public interface RequestInterface {


    interface ImageInterface extends RequestInterface{
        void gotImage(Bitmap bitmap);
    }

    interface JsonInterface extends RequestInterface{
        void gotJson(JsonElement jsonElement);
    }

    interface FileInterface extends RequestInterface{
        void gotFile(File file);
    }

    interface StringInterface extends RequestInterface{
        void gotString(String string);
    }

    void onError(Exception ex);
}
