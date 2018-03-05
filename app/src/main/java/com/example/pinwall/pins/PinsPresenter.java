package com.example.pinwall.pins;

import android.content.Context;
import android.util.Log;

import com.example.downloader.Downloader;
import com.example.downloader.models.RequestInterface;
import com.example.pinwall.App;
import com.example.pinwall.pins.pojos.Pin;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by vivek on 05/03/18.
 */

public class PinsPresenter {
    private static final String TAG = PinsPresenter.class.getSimpleName();

    private PinsScreen screen;
    private PinModel pinModel;

    private int currOffset;
    private final int LIMIT = 10;

    public PinsPresenter(PinsScreen screen, PinModel pinModel){
        this.screen = screen;
        this.pinModel = pinModel;
    }


    public void refreshPins(){
        screen.showRefreshing();

        pinModel.getLatestFeed(new RequestInterface.StringInterface() {
            @Override
            public void gotString(String string) {
                screen.hideRefreshing();
                Gson gson = new Gson();
                ArrayList<Pin> pins =  gson.fromJson(string,
                        new TypeToken<ArrayList<Pin>>(){}.getType());
                if (pins != null && pins.size()>0){
                    screen.gotRefreshedPins(pins);
                }else {
                    screen.showError();
                }
            }

            @Override
            public void onError(Exception ex) {
                screen.hideRefreshing();
                screen.hideLoading();
                screen.showNetworkError();
            }
        });
    }

    public void loadPins(){
        screen.showLoading();

        pinModel.getHomeFeed(currOffset, LIMIT, new RequestInterface.StringInterface() {
            @Override
            public void gotString(String string) {
                screen.hideLoading();
                Gson gson = new Gson();
                ArrayList<Pin> pins =  gson.fromJson(string,
                        new TypeToken<ArrayList<Pin>>(){}.getType());
                if (pins != null && pins.size()>0){
                    currOffset += pins.size();
                    screen.gotPins(pins);
                }else {
                    screen.showError();
                }
            }

            @Override
            public void onError(Exception ex) {
                screen.hideLoading();
                screen.showNetworkError();
            }
        });
    }

    //can be used for pagination
    public void paginatePins(){

    }

}
