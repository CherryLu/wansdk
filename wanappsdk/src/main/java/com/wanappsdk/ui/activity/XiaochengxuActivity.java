package com.wanappsdk.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wanappsdk.R;
import com.wanappsdk.WanSdkManager;
import com.wanappsdk.baen.BaseBean;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.DeviceMessace;
import com.wanappsdk.baen.LoginData;
import com.wanappsdk.baen.TitleMessage;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.keeper.DevicesManager;
import com.wanappsdk.ui.fragment.XiaochengxuFragment;
import com.wanappsdk.ui.view.PagerSlidingTabStrip;
import com.wanappsdk.utils.GetSystemInfoUtil;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.TimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/9/10.
 */

public class XiaochengxuActivity extends BaseActivity {


    LinearLayout layoutTitle;
    ViewPager viewpager;
    PagerSlidingTabStrip pagerSlidingTabStrip;
    ImageView backImage;
    TextView mainTitle;
    TextView rightTxt;
    ImageView rightImage;

    private List<TitleMessage> messages;

    private List<Fragment> sortFragments;

    private FragmentAdapter adapter;

    private int position;


    private void initView(){
        layoutTitle = findViewById(R.id.layout_title);
        viewpager = findViewById(R.id.viewpager);
        pagerSlidingTabStrip = findViewById(R.id.pagerSlidingTabStrip);
        backImage = findViewById(R.id.back_image);
        mainTitle = findViewById(R.id.main_title);
        rightTxt = findViewById(R.id.right_txt);
        rightImage = findViewById(R.id.right_image);

        backImage.setOnClickListener(this);


    }


    private void getTitleMessage(){
        messages = new ArrayList<>();

        for (int i =0;i<1;i++){

            TitleMessage  message = new TitleMessage();
            switch (i){
                case 0:
                    message.setTitle("小程序任务");
                    break;
                case 1:
                  //  message.setTitle("签到任务");
                    break;
                case 2:
                  //  message.setTitle("新闻任务");
                    break;
            }

            messages.add(message);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        initView();
        //ApiServiceManager.getIP(this);
        setTitleBar("简单赚钱");
        sortFragments = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            askPermission();
        } else {
            loginSDK();

        }
    }


    private void initTitle() {
        XiaochengxuFragment fragment = new XiaochengxuFragment();
        Bundle bundle = new Bundle();
        bundle.putString("MID", "0");
        fragment.setArguments(bundle);
        sortFragments.add(fragment);

       /* if (messages == null || messages.size() <= 0) {
            return;
        }
        layoutTitle.removeAllViews();
        for (int i = 0; i < messages.size(); i++) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.tv_main_tab, layoutTitle, false);
            textView.setText(messages.get(i).getTitle());
            layoutTitle.addView(textView);
            if (i==0){
                ArtificalFragment fragment = new ArtificalFragment();
                Bundle bundle = new Bundle();
                bundle.putString("MID", messages.get(i).getMid());
                fragment.setArguments(bundle);
                sortFragments.add(fragment);
                textView.setSelected(true);
            }


        }*/

        adapter = new FragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(1);
        pagerSlidingTabStrip.setViewPager(viewpager);

        LogUtils.e("POS",position+"");

        viewpager.setCurrentItem(position);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
        ArrayList<String> permissions = new ArrayList<String>();
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (checkSelfPermission(Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.BLUETOOTH);
        }
        if (permissions != null && permissions.size() > 0) {
            String[] st = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                st[i] = permissions.get(i);
            }
            requestPermissions(st, 1);
        }else {
            loginSDK();

        }
    }

    private void loginSDK(){
        Map<Integer,String> map = new HashMap<>();
        String imei = "";
        if (DevicesManager.getInstance().readUserData()){
            imei = DevicesManager.getInstance().getDeviceId();
            LogUtils.e("Permission","true   :  "+imei);
            //imei = MD5Util.lowerMD5(DevicesManager.getInstance().getDeviceId()+"mac"+ApiServiceManager.getMacAddr()+"inid"+ GetDeviceid.getid(this)+"bid"+ApiServiceManager.getMacid(this));
            map.put(0,DevicesManager.getInstance().messace.getImei1());
            map.put(1,DevicesManager.getInstance().messace.getImei2());
            WanSdkManager.setUserId(imei);
        }else {

            TelephonyManager telephonyManager = (TelephonyManager) WanSdkManager.getContext().getSystemService(TELEPHONY_SERVICE);

            Map<String,String> stringMap = GetSystemInfoUtil.getImeiAndMeid(this);
            if (map!=null){
                map.clear();
            }
            map.put(0,stringMap.get("imei1"));
            map.put(1,stringMap.get("imei2"));


            String str = null;

            if ((stringMap.get("meid")!=null&&stringMap.get("meid").length()>13&&stringMap.get("meid").length()<16)||(map.get(0)!=null&&map.get(0).length()>13&&map.get(0).length()<16)||(map.get(1)!=null&&map.get(1).length()>13&&map.get(1).length()<16)){
                if (stringMap.get("meid")!=null){
                    if (stringMap.get("meid").length()==14){
                        str = stringMap.get("meid");
                    }else {

                        int  imei1 =  map.get(0).charAt(0);
                        int imei2 = map.get(1).charAt(0);

                        if (imei1>imei2){
                            str = map.get(1);
                        }else {
                            str = map.get(0);
                        }

                    }
                }else {
                    int  imei1 =  map.get(0).charAt(0);
                    int imei2 = map.get(1).charAt(0);
                    if (imei1>imei2){
                        str = map.get(1);
                    }else {
                        str = map.get(0);
                    }

                }
            }else {
                Toast.makeText(this,"非法设备",Toast.LENGTH_SHORT).show();
                finish();

            }
            imei = str;
            WanSdkManager.setUserId(imei);
            DeviceMessace messace = new DeviceMessace();
            messace.getAllData(this,telephonyManager);
            DevicesManager.getInstance().messace = messace;
            DevicesManager.getInstance().saveUserData();
        }

        ApiServiceManager.loginSDK(imei, Build.BRAND, map.get(0), map.get(1), new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseBean bean = gson.fromJson(json,BaseBean.class);
                    if (bean.getCode()==100||bean.getCode()==119){//成功
                        getTitleMessage();
                        position = getIntent().getIntExtra("POS",0);
                        initTitle();
                    }else {//失败
                        Toast.makeText(XiaochengxuActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                        Nagivator.finishActivity(XiaochengxuActivity.this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(XiaochengxuActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(XiaochengxuActivity.this);
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(XiaochengxuActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                Nagivator.finishActivity(XiaochengxuActivity.this);
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                Gson gson = new Gson();
                LoginData bean = gson.fromJson(json,LoginData.class);
                LogUtils.e("Decode","LoginSDK : "+json);
                if (baseString.getCode()==100||baseString.getCode()==119){//成功
                    if (TextUtils.isEmpty(bean.getMemo())){
                        getTitleMessage();
                        position = getIntent().getIntExtra("POS",0);
                        initTitle();
                    }else {
                        String str = bean.getAsToTime();
                       /* Date time =new Date(str);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh：mm：ss");*/
                        String timeFormat = TimeUtils.formatDate1(str);

                        Toast.makeText(XiaochengxuActivity.this,"由于您未按照要求提交图片或存在作弊行为，任务区将关停至"+timeFormat+",如有问题请联系客服！",Toast.LENGTH_SHORT).show();
                        Nagivator.finishActivity(XiaochengxuActivity.this);
                    }

                }else if (baseString.getCode()==102||baseString.getCode()==103){
                    Toast.makeText(XiaochengxuActivity.this,"非法用户，如有问题请联系客服！",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(XiaochengxuActivity.this);

                }else {//失
                    Nagivator.finishActivity(XiaochengxuActivity.this);
                }

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    this.finish();
                    return;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        getTitleMessage();
        position = getIntent().getIntExtra("POS",0);
        initTitle();
        loginSDK();
    }







    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId()==R.id.back_image){
            Nagivator.finishActivity(this);
        }
    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return sortFragments.get(position);
        }

        @Override
        public int getCount() {
            return sortFragments.size();
        }
    }
}
