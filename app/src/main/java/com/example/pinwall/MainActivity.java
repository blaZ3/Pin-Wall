package com.example.pinwall;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.downloader.Downloader;
import com.example.downloader.models.RequestInterface;
import com.example.pinwall.databinding.ActivityMainBinding;

import java.util.Map;

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
                        .setTag(dataBinding.imgTest)
                        .start();


                    //        Downloader.with(this)
                    //                .cancel(TAG);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    RequestInterface.ImageInterface imageInterface = new RequestInterface.ImageInterface() {
        @Override
        public void gotImage(final Bitmap bitmap) {
            dataBinding.imgTest.post(new Runnable() {
                @Override
                public void run() {
                    dataBinding.imgTest.setImageBitmap(bitmap);
                }
            });
        }

        @Override
        public void onError(Exception ex) {

        }
    };
}
