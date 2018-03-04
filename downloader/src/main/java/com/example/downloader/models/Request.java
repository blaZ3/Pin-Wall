package com.example.downloader.models;

import com.example.downloader.Downloader;

import java.util.concurrent.Future;

/**
 * Created by vivek on 04/03/18.
 */

public final class Request {

    private String url;
    private Object tag;
    private Future future;
    private RequestInterface callback;

    private Request(String url, Object tag, Future future, RequestInterface callback) {
        this.url = url;
        this.tag = tag;
        this.future = future;
        this.callback = callback;
    }

    public String getUrl() {
        return url;
    }

    public Object getTag() {
        return tag;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public RequestInterface getCallback() {
        return callback;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Builder for request
    ///////////////////////////////////////////////////////////////////////////

    public static class RequestBuilder{

        private String url;
        private Object tag;
        private RequestInterface callback;

        private Downloader downloader;

        public RequestBuilder(Downloader downloader, String url) {
            this.downloader = downloader;
            this.url = url;
        }

        public RequestBuilder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public RequestBuilder setCallback(RequestInterface callback) {
            this.callback = callback;
            return this;
        }

        public void start(){
            if (tag == null){
                throw new IllegalArgumentException("tag cannot be empty");
            }else {
                Request request = new Request(url, tag, null, callback);
                downloader.submit(request);
            }
        }

    }

}
