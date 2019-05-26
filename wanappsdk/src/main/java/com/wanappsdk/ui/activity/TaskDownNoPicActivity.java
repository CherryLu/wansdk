package com.wanappsdk.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wanappsdk.R;
import com.wanappsdk.animotion.ShowGold;
import com.wanappsdk.baen.BaseBean;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.http.ProgressListener;
import com.wanappsdk.http.ProgressResponseBody;
import com.wanappsdk.http.RetrofitService;
import com.wanappsdk.ui.view.MeCollectionDialog;
import com.wanappsdk.ui.view.UpLoadCallBack;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.ScreenUtil;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.TResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class TaskDownNoPicActivity extends TakePhotoActivity implements View.OnClickListener,UpLoadCallBack{

    ImageView backImage;
    TextView mainTitle;
    TextView rightTxt;
    ImageView rightImage;
    ImageView appPic;
    TextView appName;
    TextView appSize;
    TextView appLable1;
    TextView appLable2;
    TextView getmoney;
    TextView copy_name;
    LinearLayout copy_layout;

    Button task_start;
    ImageView images;


    LinearLayout download_area;
    TextView progress_txt;
    ProgressBar progressBar;

    TextView downloadtitle;

    TextView shuoming;
    TextView notices;

    LinearLayout image_layout,image_simple_layout;


    private boolean isDownloadComplete;

    private int requestCode;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 200:
                    float progress  = (float) msg.obj;
                    isDownloadComplete = true;
                    if (progressBar!=null){
                        progressBar.setProgress((int) (progress*100));
                    }

                    if (progress_txt!=null){
                        progress_txt.setText((int) (progress*100)+"");
                    }

                    if (progress==1){//下载完成

                        isDownloadComplete = false;
                        downloadtitle.setVisibility(View.VISIBLE);
                        download_area.setVisibility(View.GONE);
                        downloadtitle.setText("下载完成");//跳转安装页面
                        //download_install.setVisibility(View.VISIBLE);
                        String filePath = (String) msg.getData().getString("FILE");
                        File file = new File(filePath);
                        ApkUtils.installApk(TaskDownNoPicActivity.this,file);
                        requestCode = ApkUtils.START_INSTALL;
                        //getDownLoadCompletion();
                    }

                    break;
                case 201:
                    isDownloadComplete = false;
                    if (downloadtitle!=null){
                        downloadtitle.setVisibility(View.VISIBLE);
                        download_area.setVisibility(View.GONE);
                        downloadtitle.setText("下载错误");
                    }

                    break;

            }
            super.handleMessage(msg);
        }
    };




    TaskData mainTask;
    TaskData stepTask;
    LinearLayout pic_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mainTask = (TaskData) getIntent().getSerializableExtra("MAIN");
        stepTask = (TaskData) getIntent().getSerializableExtra("TASK");



        initView();
        initData();
        pic_upload = findViewById(R.id.pic_upload);
        pic_upload.setVisibility(View.GONE);

    }


    private void checkChange(){
        if ("二维码".equals(mainTask.getJobMarket())||"跳转".equals(mainTask.getJobMarket())){
            return;
        }
        if (canComplete&&ApkUtils.hasInstall(this,mainTask.getApkName())){
            task_start.setText("提交任务");
        }
    }

    private void showCollectionDialog(final int  which){
        if (!TextUtils.isEmpty(stepTask.getCredit_card())){
            if (stepTask.getCredit_card().contains("1")&&stepTask.getCredit_card().length()==8){
                List<String> showIt = new ArrayList<>();
                for (int i = 0;i<stepTask.getCredit_card().length();i++){
                    showIt.add(stepTask.getCredit_card().charAt(i)+"");
                }
                MeCollectionDialog dialog = new MeCollectionDialog(TaskDownNoPicActivity.this, mainTask, new AlertHelper.IAlertListener() {
                    @Override
                    public void sure() {
                        completeTask(stepTask,"aaa");
                        Toast.makeText(TaskDownNoPicActivity.this,"提交成功",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void cancel() {
                        Toast.makeText(TaskDownNoPicActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }
                },showIt);
                dialog.show();
            }else {
                completeTask(stepTask,"aaa");
            }

        }else {
            completeTask(stepTask,"aaa");
        }
    }

    private void getIsDownloadComplete(){
        if (mainTask==null){
            return;
        }
        String packetName = "";
        switch (mainTask.getJobMarket()){
            case "平台":
                mainTask.setMaeket(false);
                packetName = mainTask.getApkName();
                isDownloadComplete = false;
                canComplete = false;
                break;
            case "跳转":

                //ApkUtils.startUrl(this,mainTask.getDownUrl());
                break;
            case "二维码":

                break;
            case "华为市场":
                packetName = "com.huawei.appmarket";
                break;
            case "vivo市场":
                packetName = "com.bbk.appstore";
                break;
            case "oppo市场":
                packetName = "com.oppo.market";
                break;
            case "360市场":
                packetName = "com.qihoo.appstore";
                break;
            case "搜狗市场":
                packetName = "com.sogou.androidtool";
                break;
            case "金立市场":
                packetName = "com.gionee.aora.market";
                break;
            case "豌豆荚市场":
                packetName = "com.wandoujia.phoenix2";
                break;
            case "百度市场":
                packetName = "com.baidu.appsearch";
                break;
            case "应用宝":
                packetName = "com.tencent.android.qqdownloader";
                break;
            case "小米市场":
            case "小米":
                packetName = "com.xiaomi.market";
                break;
            case "安智市场":
                packetName = "cn.goapk.market";
                break;
            case "pp助手":
                packetName = "com.pp.assistant";

                if (ApkUtils.hasInstall(this,packetName)){
                    isDownloadComplete = false;
                    canComplete = false;
                }else {
                    canComplete = false;
                    isDownloadComplete = false;
                }
                break;
        }


        if (TextUtils.isEmpty(packetName)){
            canComplete = false;
            isDownloadComplete = false;
        }else {
            if (ApkUtils.hasInstall(this,packetName)){
                isDownloadComplete = false;
                canComplete = false;
            }else {
                canComplete = false;
                isDownloadComplete = false;
            }
        }


    }




    @Override
    protected void onStart() {
        super.onStart();

        if (downloadtitle.getVisibility()==View.VISIBLE){
            if (ApkUtils.hasInstall(this,mainTask.getApkName())){
                downloadtitle.setText("点击打开APP");
                downloadtitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApkUtils.startAPP(TaskDownNoPicActivity.this,mainTask.getApkName());
                    }
                });
            }
        }

        //getIsDownloadComplete();
        checkChange();
    }


    private boolean canComplete;
    @Override
    protected void onRestart() {
        super.onRestart();
        if (requestCode==ApkUtils.START_INSTALL){
            //检查是否安装
            if (ApkUtils.hasInstall(this,mainTask.getApkName())){//完成安装任务
                downloadtitle.setText("点击打开APP");
                downloadtitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApkUtils.startAPP(TaskDownNoPicActivity.this,mainTask.getApkName());
                    }
                });
                canComplete = true;
                //completeTask(stepTask,"aaa");
                requestCode = 0;

            }else {//未完成
                Toast.makeText(this,"您尚未安装APP,请安装后打开",Toast.LENGTH_SHORT).show();
                downloadtitle.setText("点击安装APP");
                downloadtitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestCode = ApkUtils.START_INSTALL;
                        String  path = Environment.getExternalStorageDirectory()+"/wandownload/"+fileNames;
                        File file = new File(path);
                        ApkUtils.installApk(TaskDownNoPicActivity.this,file);
                    }
                });
                requestCode = 0;
            }
        }

        checkChange();
    }

    private void startDownload(String fileName, String url){
        fileNames = fileName;
        download_area.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if (!getPackageManager().canRequestPackageInstalls()){
                ApkUtils.startInstallPermissionSettingActivity(this);
                return;
            }
        }

        fileDownLoad(url,fileName,handler);


    }
    private String fileNames;
    private void downlaodTask(TaskData taskData){
        int  isMarket = 2;
        String  marketName="";
        String marketPacketName ="";
        String downloadUrl = "";
        if (taskData==null){
            return;
        }
        switch (mainTask.getJobMarket()){
            case "平台":
                mainTask.setMaeket(false);
                isMarket = 0;
                marketName = mainTask.getAppName();
                marketPacketName = mainTask.getApkName();
                downloadUrl = mainTask.getDownUrl();
                break;
            case "跳转":
                marketName = mainTask.getAppName();
                marketPacketName = mainTask.getApkName();
                isMarket = 1;
                downloadUrl = mainTask.getDownUrl();
               //ApkUtils.startUrl(this,mainTask.getDownUrl());
                break;
            case "二维码":
                marketName = mainTask.getAppName();
                marketPacketName = mainTask.getApkName();
                isMarket = 1;
                downloadUrl = mainTask.getDownUrl();
                break;
            case "华为市场":
                marketName = "华为市场.apk";
                marketPacketName = "com.huawei.appmarket";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/jinli.apk";
                break;
            case "vivo市场":
                marketName = "vivo市场.apk";
                marketPacketName = "com.bbk.appstore";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/vivo.apk";
                break;
            case "oppo市场":
                marketName = "oppo市场.apk";
                marketPacketName = "com.oppo.market";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/oppo.apk";
                break;
            case "360市场":
                marketName = "360市场.apk";
                marketPacketName = "com.qihoo.appstore";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/360.apk";
                break;
            case "搜狗市场":
                marketName = "搜狗市场.apk";
                marketPacketName = "com.sogou.androidtool";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/sougou.apk";
                break;
            case "金立市场":
                marketName = "金立市场.apk";
                marketPacketName = "com.gionee.aora.market";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/jinli.apk";
                break;
            case "豌豆荚市场":
                marketName = "豌豆荚市场.apk";
                marketPacketName = "com.wandoujia.phoenix2";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/Wandoujia.apk";
                break;
            case "百度市场":
                marketName = "百度市场.apk";
                marketPacketName = "com.baidu.appsearch";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/baidu.apk";
                break;
            case "应用宝":
                marketName = "应用宝.apk";
                marketPacketName = "com.tencent.android.qqdownloader";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/1.apk";
                break;
            case "小米市场":
            case "小米":
                marketName = "小米市场.apk";
                marketPacketName = "com.xiaomi.market";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/xmyysc.apk";
                break;
            case "安智市场":
                marketName = "安智市场.apk";
                marketPacketName = "cn.goapk.market";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/anzhi.apk";
                break;
            case "pp助手":
                marketName = "pp助手.apk";
                marketPacketName = "com.pp.assistant";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/PP.apk";
                break;
            case "小米游戏中心":
                marketName = "小米游戏中心.apk";
                marketPacketName = "com.xiaomi.gamecenter";
                downloadUrl = "http://fastdfs.zhuankaixin.cn/group1/M00/25/5A/fUAFU1w1a46EZZP_AAAAAEa-_kY.xiaomi";
                break;
            case "华为游戏中心":
                marketName = "华为游戏中心.apk";
                marketPacketName = "com.huawei.gamebox";
                downloadUrl = "http://fastdfs.zhuankaixin.cn/group1/M00/11/E3/fUAFU1v00pmEAnX5AAAAAPcVwyA.huawei";
                break;
            case "应用汇市场":
                marketName = "应用汇市场.apk";
                marketPacketName = "com.yingyonghui.market";
                downloadUrl = "http://fastdfs.zhuankaixin.cn/group1/M00/08/CD/fUAFU1vX_aOEaoWaAAAAAH2LruU.yingyo";
                break;
            case "qq浏览器":
                marketName = "qq浏览器.apk";
                marketPacketName = "com.tencent.mtt";
                downloadUrl = "http://cdn.lizhuanwang.xyz/jobapk/QQliulanqi.apk";
                break;
        }

      /*  if (ApkUtils.hasInstall(this,mainTask.getApkName())){
            Toast.makeText(this,"您已经安装此APP,请先卸载后再来完成任务",Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (TextUtils.isEmpty(marketName)||TextUtils.isEmpty(marketPacketName)||TextUtils.isEmpty(downloadUrl)){
            Toast.makeText(this,"文件名或下载地址为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (isMarket==2){//市场 如果已经安装 直接跳转
            if (ApkUtils.hasInstall(this,marketPacketName)){
                ApkUtils.startAPP(this,marketPacketName);
                requestCode = ApkUtils.START_INSTALL;
            }else {
                startDownload(marketName,downloadUrl);
            }

            return;
        }
        if (isMarket==0){
            startDownload(marketName,downloadUrl);
        }else if (isMarket==1){
            ApkUtils.startUrl(this,downloadUrl);
        }

    }

 /*   private void uploadPics(){
        if (pics!=null&&pics.size()>0&&stepTask!=null){
            ProgressDialog progressDialog = new ProgressDialog(this, pics,stepTask.getId());
            progressDialog.setUpLoadCallBack(this);
            progressDialog.show();
        }else {
            Toast.makeText(this,"请先选择图片",Toast.LENGTH_SHORT).show();
        }
    }*/
    boolean  isStartApp;
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back_image) {
            Nagivator.finishActivity(this);
        }else if (i ==R.id.task_start){
            if (canComplete){
                if (!isStartApp){
                    Toast.makeText(this,"请按照任务说明顺序完成任务",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ApkUtils.hasInstall(this,mainTask.getApkName())){
                    showCollectionDialog(0);
                    //completeTask(stepTask,"aaa");
                }else {
                    Toast.makeText(this,"您尚未安装APP,请安装后提交",Toast.LENGTH_SHORT).show();
                }

            }else {
                if (!isDownloadComplete){
                    isStartApp = true;
                    downlaodTask(stepTask);
                }else {
                    Toast.makeText(this,"正在下载...",Toast.LENGTH_SHORT).show();
                }
            }

        }else if (i==R.id.copy_name){
            ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label",copy_name.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            Toast.makeText(this,"已复制至剪切板",Toast.LENGTH_SHORT).show();
        }
    }

    private void initView(){

        backImage = findViewById(R.id.back_image);
        backImage.setOnClickListener(this);
        mainTitle = findViewById(R.id.main_title);
        rightTxt = findViewById(R.id.right_txt);
        rightImage = findViewById(R.id.right_image);
        appPic = findViewById(R.id.app_pic);

        appName = findViewById(R.id.app_name);
        appSize = findViewById(R.id.app_size);
        appLable1 = findViewById(R.id.app_lable1);
        appLable2 = findViewById(R.id.app_lable2);
        getmoney = findViewById(R.id.getmoney);

        download_area = findViewById(R.id.download_area);
        download_area.setVisibility(View.GONE);
        progress_txt = findViewById(R.id.progress_txt);
        progressBar = findViewById(R.id.progress);
        downloadtitle = findViewById(R.id.title);
        downloadtitle.setVisibility(View.GONE);

        task_start = findViewById(R.id.task_start);
        task_start.setOnClickListener(this);

        shuoming = findViewById(R.id.shuoming);
        notices = findViewById(R.id.notices);
        copy_name = findViewById(R.id.copy_name);
        copy_name.setOnClickListener(this);
        copy_layout = findViewById(R.id.copy_layout);

        image_layout = findViewById(R.id.image_layout);
        image_simple_layout = findViewById(R.id.image_simple_layout);

        images = findViewById(R.id.images);

        mainTitle.setText(mainTask.getJobName());

    }

    private void initSimpleLayout(final List<String> images) {
        image_simple_layout.removeAllViews();
        for (int i=0;i<images.size();i++ ){
            final String url = images.get(i);
            ImageView imageView = (ImageView) View.inflate(this,R.layout.moddle_image,null);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.dip2px(this,200),ScreenUtil.dip2px(this,300),1));
            if (i==0){
                imageView.setPadding(0,0,ScreenUtil.dip2px(this,5),0);
            }else {
                imageView.setPadding(ScreenUtil.dip2px(this,5),0,0,0);
            }
            GlideUtil.loadImageView(this,images.get(i),imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskDownNoPicActivity.this,BigPicActivity.class);
                    intent.putExtra("URL",url);
                    startActivity(intent);
                }
            });
            image_simple_layout.addView(imageView);
        }
    }

    private void initData(){

        shuoming.setText(stepTask.getStepGuide());
        String[] strs = stepTask.getStepNotice().split("&");
        notices.setText(strs[0]);
        if (strs.length>1){
            images.setVisibility(View.VISIBLE);
            GlideUtil.loadImageView(this,strs[1],images);
        }else {
            images.setVisibility(View.GONE);
        }

        GlideUtil.loadImageView(this,mainTask.getJobLogo(),appPic);
        appName.setText(mainTask.getAppName());
        if (mainTask.getTags()!=null){
            String[] strings = mainTask.getTags().split(";");
            if (strings.length>0){
                appLable1.setText(strings[0]);
            }
            if (strings.length>1){
                appLable2.setText(strings[1]);
            }
        }

        appSize.setText(stepTask.getStepTitle());
        getmoney.setText("+" + ApkUtils.getMuchMoney(stepTask.getStepPoints()));
        if (TextUtils.isEmpty(stepTask.getKeyWord())){
            copy_layout.setVisibility(View.GONE);
        }else {
            copy_layout.setVisibility(View.VISIBLE);
            copy_name.setText(stepTask.getKeyWord());
        }

      /*  initCreateLayout();
        String urls = stepTask.getSimpleUrl();
        String[] strings = urls.split(";");

        simplePic = new ArrayList<>();

        for (int i = 0;i<strings.length;i++){

            simplePic.add(strings[i]);

        }
        initSimpleLayout(simplePic);*/
    }


    /**
     * 初始化上传
     */
    private void initCreateLayout() {
        image_layout.removeAllViews();
        /*View view = View.inflate(this, R.layout.moddle_upload_image, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTakePhoto().onEnableCompress(getCompressConfig(),true);
                getTakePhoto().onPickMultiple(3);
            }
        });

        image_layout.addView(view);*/

        adduploadImage();
    }

    private void adduploadImage(){
        View view = View.inflate(this, R.layout.moddle_upload_image, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTakePhoto().onEnableCompress(getCompressConfig(),true);
                getTakePhoto().onPickMultiple(3);
            }
        });

        image_layout.addView(view);

    }


    private void initLayout(final List<String> images) {
    /*    image_layout.removeAllViews();
        for (int i=0;i<images.size();i++ ){
            final RelativeLayout layout = (RelativeLayout) View.inflate(this,R.layout.up_pic_del,null);
            ImageView imageView = layout.findViewById(R.id.pic);
            ImageView delete = layout.findViewById(R.id.del);

            if (i==0){
                imageView.setPadding(0,0, ScreenUtil.dip2px(this,5),0);
            }else {
                imageView.setPadding(ScreenUtil.dip2px(this,5),0,0,0);
            }

            GlideUtil.loadImageView(this,images.get(i),imageView);
            final int finalI = i;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_layout.removeView(layout);
                    if (image_layout.getChildCount()==0){
                        View view = View.inflate(TaskDownNoPicActivity.this, R.layout.moddle_upload_image, null);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getTakePhoto().onEnableCompress(getCompressConfig(),true);
                                getTakePhoto().onPickMultiple(3);
                            }
                        });
                        image_layout.addView(view);
                    }

                }
            });
            image_layout.addView(layout);
        }

        adduploadImage();
        checkChange();
*/
    }


    private CompressConfig getCompressConfig(){

        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(102400)
                .setMaxPixel(400)
                .enableReserveRaw(true)
                .create();

        return config;
    }


    private Call<ResponseBody> call;
    /**
     *
     * @param url   完整的下载链接
     * @param fileName   带后缀的文件名
     */
    public void fileDownLoad(final String url, String fileName, final Handler handler) {
       /* int i = url.lastIndexOf("/");
        final String title = url.substring(0, i + 1);*/
        final String name = fileName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://codeload.github.com/");//传入服务器BaseUrl
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder().retryOnConnectionFailure(true).addNetworkInterceptor(new okhttp3.Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        return response.newBuilder().body(new ProgressResponseBody(response.body(), new ProgressListener() {
                            @Override
                            public void onProgress(long progress, long total, boolean done) {
                                LogUtils.e("Download","progress : "+progress+"         total : "+total);
                                if (handler!=null){
                                    Message message = Message.obtain();
                                    message.what = 200;
                                    String path = Environment.getExternalStorageDirectory()+"/wandownload/"+name;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("FILE",path);

                                    message.setData(bundle);

                                    double to = total;
                                    double pr = progress;
                                    float pro = (float) (pr/to);
                                    LogUtils.e("Download","pro : "+pro);
                                    message.obj = pro;
                                    handler.sendMessage(message);
                                }

                            }
                        })).build();
                    }
                }).build();
                RetrofitService apiService = builder.client(client).build().create(RetrofitService.class);
                call = apiService.getFile(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final retrofit2.Response<ResponseBody> response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (response == null || response.body() == null) {//空地址
                                        LogUtils.e("Download","空地址");
                                        if (handler!=null){
                                            Message message = Message.obtain();
                                            message.what = 201;//
                                            handler.sendMessage(message);
                                        }
                                        return;
                                    }
                                    InputStream is = response.body().byteStream();
                                    String files = Environment.getExternalStorageDirectory()+"/wandownload";
                                    File pfile = new File(files);
                                    if (!pfile.exists()){
                                        pfile.mkdir();
                                    }
                                    File loadFile = new File(pfile, name);//保存路径与文件名
                                    FileOutputStream fos = new FileOutputStream(loadFile);
                                    BufferedInputStream bis = new BufferedInputStream(is);
                                    byte[] bytes = new byte[1024];
                                    int len;
                                    while ((len = bis.read(bytes)) != -1) {
                                        fos.write(bytes, 0, len);
                                        fos.flush();
                                    }
                                    fos.close();
                                    bis.close();
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();//下载失败
                                    LogUtils.e("Download","下载失败 ："+e.getMessage());
                                    if (handler!=null){
                                        Message message = Message.obtain();
                                        message.what = 201;
                                        handler.sendMessage(message);
                                    }
                                }

                            }
                        }).start();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();//下载失败
                        LogUtils.e("Download","onFailure ："+t.getMessage());
                        if (handler!=null){
                            Message message = Message.obtain();
                            message.what = 201;
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        }).start();

    }


    /**
     * 任务完成
     * @param taskData
     * @param urls
     */
    private void completeTask(final TaskData taskData, String urls){

        final SweetAlertDialog mDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("同步中...").show();


        ApiServiceManager.getTaskComplete(mainTask.getApkName(), taskData.getId(), urls, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                mDialog.dismiss();
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseBean baseComlete = gson.fromJson(json,BaseBean.class);
                    if (baseComlete.getCode()==110){

                        ShowGold showGold = new ShowGold(TaskDownNoPicActivity.this);
                        showGold.show((LinearLayout) findViewById(R.id.container), TaskDownNoPicActivity.this, new ShowGold.AnimotionFinish() {
                            @Override
                            public void onFinish() {
                                if (taskData.getChecktype()==0||taskData.getChecktype()==2){//免审
                                    Toast.makeText(TaskDownNoPicActivity.this,"提交成功，任务奖励已经到账",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(TaskDownNoPicActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                                }
                                Nagivator.finishActivity(TaskDownNoPicActivity.this);
                                return;
                            }
                        });


                    }else {
                        Toast.makeText(TaskDownNoPicActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TaskDownNoPicActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                mDialog.dismiss();
                Toast.makeText(TaskDownNoPicActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                mDialog.dismiss();
                Gson gson = new Gson();
                BaseBean baseComlete = gson.fromJson(json,BaseBean.class);
                if (baseComlete.getCode()==110){
                    Toast.makeText(TaskDownNoPicActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(TaskDownNoPicActivity.this);
                    return;
                }
                Toast.makeText(TaskDownNoPicActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (call!=null){
            call.cancel();
        }
        requestCode = 0;
        super.onDestroy();

    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
       /* if (pics==null){
            pics = new ArrayList<>();
        }else {
            pics.clear();
        }
        ArrayList<TImage> tImages =  result.getImages();
        for (int i =0;i<tImages.size();i++){
            pics.add(tImages.get(i).getOriginalPath());
        }
        initLayout(pics);*/

    }

    @Override
    public void onSuccessed(String name) {
        completeTask(stepTask,name);
    }

    @Override
    public void onFail() {

    }
}
