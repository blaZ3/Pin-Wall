package com.example.pinwall;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.downloader.Downloader;
import com.example.downloader.models.RequestInterface;
import com.example.pinwall.databinding.ActivityMainBinding;
import com.example.pinwall.network.DownloaderNetworkClient;
import com.example.pinwall.pins.PinModel;
import com.example.pinwall.pins.PinsAdapter;
import com.example.pinwall.pins.PinsPresenter;
import com.example.pinwall.pins.PinsScreen;
import com.example.pinwall.pins.pojos.Pin;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements PinsScreen {

    private ActivityMainBinding dataBinding;

    private PinsPresenter pinsPresenter;

    private PinsAdapter pinsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        pinsPresenter = new PinsPresenter(this,
                new PinModel(DownloaderNetworkClient.getInstance(getApplicationContext())));


        dataBinding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInit();
            }
        });

        dataBinding.pullRefreshMain.setOnRefreshListener(onRefreshListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        doInit();
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pinsPresenter.refreshPins();
        }
    };

    PinsAdapter.PinsAdapterInterface pinsAdapterInterface = new PinsAdapter.PinsAdapterInterface() {
        @Override
        public void pinSelected(int position) {
            showToast("Pin selected");
        }

        @Override
        public void paginate() {
            showToast("Start pagination");
        }
    };

    @Override
    public void doInit() {
        pinsPresenter.loadPins();

        dataBinding.pullRefreshMain.setVisibility(View.GONE);
        dataBinding.layoutRetry.setVisibility(View.GONE);
        dataBinding.progressMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void gotPins(ArrayList<Pin> pins) {
        Log.d(TAG, "gotPins() called with: pins = [" + pins.size() + "]");
        pinsAdapter = new PinsAdapter(MainActivity.this, pins, pinsAdapterInterface);

        dataBinding.recyclerMain.setLayoutManager(new GridLayoutManager(this, 2));
        dataBinding.recyclerMain.setAdapter(pinsAdapter);

        dataBinding.pullRefreshMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void gotRefreshedPins(ArrayList<Pin> pins) {
        Log.d(TAG, "gotRefreshedPins() called with: pins = [" + pins + "]");
        pinsAdapter = new PinsAdapter(MainActivity.this, pins, pinsAdapterInterface);

        dataBinding.recyclerMain.setLayoutManager(new GridLayoutManager(this, 2));
        dataBinding.recyclerMain.setAdapter(pinsAdapter);

        dataBinding.pullRefreshMain.setVisibility(View.VISIBLE);
        dataBinding.pullRefreshMain.setRefreshing(false);
    }

    @Override
    public void showLoading() {
        dataBinding.pullRefreshMain.setVisibility(View.GONE);
        dataBinding.progressMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        dataBinding.progressMain.setVisibility(View.GONE);
    }

    @Override
    public void showRefreshing() {
        dataBinding.pullRefreshMain.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() {
        dataBinding.pullRefreshMain.setRefreshing(false);
    }

    @Override
    public void showError() {
        Log.d(TAG, "showError() called");
        showToast("Some error occurred please try again");
        showRetry();
    }

    @Override
    public void showNetworkError() {
        Log.d(TAG, "showNetworkError() called");
        showToast("Please check your network");

        showRetry();
    }

    private void showRetry() {
        dataBinding.layoutRetry.setVisibility(View.VISIBLE);
        dataBinding.pullRefreshMain.setVisibility(View.GONE);
        dataBinding.progressMain.setVisibility(View.GONE);
    }

}
