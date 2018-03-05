package com.example.pinwall;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by vivek on 05/03/18.
 */

public class BaseActivity extends AppCompatActivity {


    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
