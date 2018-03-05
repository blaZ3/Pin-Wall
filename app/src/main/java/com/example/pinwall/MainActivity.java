package com.example.pinwall;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.downloader.Downloader;
import com.example.downloader.models.RequestInterface;
import com.example.pinwall.databinding.ActivityMainBinding;
import com.google.gson.JsonElement;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        dataBinding.bntTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Downloader.with(MainActivity.this)
                        .download("https://source.android.com/_static/images/android/touchicon-180.png")
                        .setTag(dataBinding.imgTest1)
                        .start();

                Downloader.with(MainActivity.this)
                        .download("https://source.android.com/_static/images/android/touchicon-180.png")
                        .setTag(dataBinding.imgTest2)
                        .start();

                Downloader.with(MainActivity.this)
                        .download("http://pastebin.com/raw/wgkJgazE")
                        .setCallback(new RequestInterface.StringInterface() {
                            @Override
                            public void gotString(String string) {
                                Log.d(TAG, "gotString() called with: string = [" + string + "]");
                            }

                            @Override
                            public void onError(Exception ex) {
                                Log.d(TAG, "onError() called with: ex = [" + ex + "]");
                            }
                        })
                        .start();


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
