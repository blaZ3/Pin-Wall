package com.example.pinwall.pins;

import com.example.pinwall.pins.pojos.Pin;

import java.util.ArrayList;

/**
 * Created by vivek on 05/03/18.
 */

public interface PinsScreen {
    String TAG = PinsScreen.class.getSimpleName();

    void doInit();
    void showLoading();
    void hideLoading();

    void gotPins(ArrayList<Pin> pins);
    void gotRefreshedPins(ArrayList<Pin> pins);

    void showRefreshing();
    void hideRefreshing();

    void showError();
    void showNetworkError();

}
