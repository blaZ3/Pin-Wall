package com.example.downloader.cache;

/**
 * Created by vivek on 05/03/18.
 */

public interface LocalCache {

    Object getValueFromCache(String key);

    void putValueToCache(String key, Object value);

}
