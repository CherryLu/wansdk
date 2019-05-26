package com.wansdk.cn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wanappsdk.WanSdkManager;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void startMain(View view) {
        WanSdkManager.startMainActivity(this);
    }

    public void startRead(View view) {
        WanSdkManager.startReadActivity(this);
    }


    public void startXiaochengxu(View view){
        WanSdkManager.startXiaochengxuActivity(this);
    }
}
