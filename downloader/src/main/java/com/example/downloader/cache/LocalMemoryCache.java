package com.example.downloader.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by vivek on 05/03/18.
 */

public class LocalMemoryCache implements LocalCache {
    private static final String TAG = LocalMemoryCache.class.getSimpleName();

    static LocalMemoryCache instance;

    private LruCache<String, Object> mMemoryCache;

    public static LocalMemoryCache getInstance(){
        if (instance == null){
            instance = new LocalMemoryCache();
        }
        return instance;
    }

    private LocalMemoryCache(){
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Object>(cacheSize) {
            @Override
            protected int sizeOf(String key, Object obj) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                try{
                    if (obj instanceof Bitmap){
                        return ((Bitmap)obj).getByteCount() / 1024;
                    }else if (obj instanceof JsonElement){
                        return obj.toString().getBytes().length / 1024;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return 0;
            }
        };
    }

    @Override
    public Object getValueFromCache(String key) {
        try{
            return mMemoryCache.get(key);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void putValueToCache(String key, Object value) {
        mMemoryCache.put(key, value);
    }
}
