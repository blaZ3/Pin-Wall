package com.example.pinwall;

import com.example.pinwall.network.MockNetworkClient;
import com.example.pinwall.pins.PinModel;
import com.example.pinwall.pins.PinsPresenter;
import com.example.pinwall.pins.PinsScreen;
import com.example.pinwall.pins.pojos.Pin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * Created by vivek on 05/03/18.
 */

public class PinsWallTests {

    PinsPresenter pinsPresenter;

    @Mock
    PinsScreen pinsScreen;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void test_load_pins(){
        pinsPresenter = new PinsPresenter(pinsScreen,
                new PinModel(new MockNetworkClient(false)));

        pinsPresenter.loadPins();

        Mockito.verify(pinsScreen, Mockito.times(1)).showLoading();
        Mockito.verify(pinsScreen, Mockito.times(1)).gotPins((ArrayList<Pin>) Mockito.any());
        Mockito.verify(pinsScreen, Mockito.times(0)).showNetworkError();
        Mockito.verify(pinsScreen, Mockito.times(1)).hideLoading();
    }

    @Test
    public void test_load_pins_fail(){
        pinsPresenter = new PinsPresenter(pinsScreen,
                new PinModel(new MockNetworkClient(true)));

        pinsPresenter.loadPins();

        Mockito.verify(pinsScreen, Mockito.times(1)).showLoading();
        Mockito.verify(pinsScreen, Mockito.times(0)).gotPins((ArrayList<Pin>) Mockito.any());
        Mockito.verify(pinsScreen, Mockito.times(1)).showNetworkError();
        Mockito.verify(pinsScreen, Mockito.times(1)).hideLoading();
    }

    @Test
    public void test_refresh_pins(){
        pinsPresenter = new PinsPresenter(pinsScreen,
                new PinModel(new MockNetworkClient(false)));

        pinsPresenter.refreshPins();

        Mockito.verify(pinsScreen, Mockito.times(1)).showRefreshing();
        Mockito.verify(pinsScreen, Mockito.times(1)).gotRefreshedPins((ArrayList<Pin>) Mockito.any());
        Mockito.verify(pinsScreen, Mockito.times(0)).showError();
        Mockito.verify(pinsScreen, Mockito.times(1)).hideRefreshing();
    }


    @Test
    public void test_refresh_pins_fail(){
        pinsPresenter = new PinsPresenter(pinsScreen,
                new PinModel(new MockNetworkClient(true)));

        pinsPresenter.refreshPins();

        Mockito.verify(pinsScreen, Mockito.times(1)).showRefreshing();
        Mockito.verify(pinsScreen, Mockito.times(0)).gotRefreshedPins((ArrayList<Pin>) Mockito.any());
        Mockito.verify(pinsScreen, Mockito.times(1)).showError();
        Mockito.verify(pinsScreen, Mockito.times(1)).hideRefreshing();
    }

}
