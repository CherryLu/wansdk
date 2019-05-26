package com.wanappsdk.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.wanappsdk.R;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.Nagivator;

public class BigPicActivity extends BaseActivity {

    ImageView main_pic;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_bigpic);
        main_pic = findViewById(R.id.main_pic);
        url = getIntent().getStringExtra("URL");
        GlideUtil.loadImageView(this,url,main_pic);
        main_pic.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.main_pic){
            Nagivator.finishActivity(this);
        }
        super.onClick(view);
    }
}
