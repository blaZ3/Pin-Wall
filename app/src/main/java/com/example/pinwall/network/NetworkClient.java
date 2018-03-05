package com.example.pinwall.network;

import com.example.downloader.models.RequestInterface;

/**
 * Created by vivek on 05/03/18.
 */

public interface NetworkClient {

    void getFeed(RequestInterface.StringInterface stringInterface);

}
