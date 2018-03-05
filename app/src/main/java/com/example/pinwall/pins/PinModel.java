package com.example.pinwall.pins;

import com.example.downloader.models.RequestInterface;
import com.example.pinwall.network.NetworkClient;
import com.example.pinwall.pins.pojos.Pin;

import java.util.ArrayList;

/**
 * Created by vivek on 05/03/18.
 */

public class PinModel {

    private NetworkClient networkClient;

    public PinModel(NetworkClient networkClient){
        this.networkClient = networkClient;
    }

    /**
     * offset and limit can be used for pagination of the api
     * @param offset
     * @param limit
     * @param stringInterface
     */
    public void getHomeFeed(int offset, int limit, RequestInterface.StringInterface stringInterface){
        networkClient.getFeed(stringInterface);
    }

    public void getLatestFeed(RequestInterface.StringInterface stringInterface){
        networkClient.getFeed(stringInterface);
    }

}
