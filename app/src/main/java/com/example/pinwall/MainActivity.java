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
                        .download("https://jsonplaceholder.typicode.com/posts/1")
                        .setTag(TAG+"json")
                        .setCallback(new RequestInterface.JsonInterface() {
                            @Override
                            public void gotJson(JsonElement jsonElement) {
                                Log.d(TAG, "gotJson: "+jsonElement);
                            }

                            @Override
                            public void onError(Exception ex) {

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
